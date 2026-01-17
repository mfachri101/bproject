package com.fachri.bproject.integration.abacus;

import com.fachri.bproject.integration.IntegrationBase;
import com.fachri.bproject.integration.IntegrationResponse;
import com.fachri.bproject.model.FlightSearchRequest;
import com.fachri.bproject.model.FlightSegment;
import com.fachri.bproject.model.Itinerary;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.fachri.bproject.integration.abacus.AbacusNdcIntegrationImpl.PROVIDER_NAME;

@Service(PROVIDER_NAME)
public class AbacusNdcIntegrationImpl extends IntegrationBase {

  public static final String PROVIDER_NAME = "abacus-ndc";

  private final ObjectMapper objectMapper;

  @Autowired
  public AbacusNdcIntegrationImpl(ObjectMapper objectMapper, @Value("${integration.abacus-ndc.baseUrl}") String baseUrl, @Qualifier("integrationHttpClient") HttpClient httpClient) {
    super(baseUrl, httpClient);
    this.objectMapper = objectMapper.copy().configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  // AbacusNdcIntegrationImpl.java
  @Override
  public CompletableFuture<IntegrationResponse> fetch(FlightSearchRequest flightSearchRequest) {
    // Map FlightSearchRequest to API payload
    ObjectNode payload = objectMapper.createObjectNode();
    payload.put("origin", flightSearchRequest.getOriginCode());
    payload.put("destination", flightSearchRequest.getDestinationCode());
    payload.put("departureDate", flightSearchRequest.getDepartureDate().toString());

    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(baseUrl + "/gds-ndc/v1/shopping/flight-offers"))
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
      .build();

    return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
      .thenApply(response -> {
        try {
          return mapToIntegrationResponse(response.body(), providerId());
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }).exceptionally(
        ex -> IntegrationResponse.builder()
          .itineraries(new ArrayList<>())
          .providerId(providerId())
          .success(false)
          .build()
      );
  }

  public IntegrationResponse mapToIntegrationResponse(String json, String providerId) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(json);
    List<Itinerary> itineraries = new ArrayList<>();

    for (JsonNode offer : root.get("data")) {
      String id = offer.get("id").asText();
      JsonNode priceNode = offer.get("price");
      String currency = priceNode.get("currency").asText();
      Long price = Math.round(Double.parseDouble(priceNode.get("total").asText()));

      List<FlightSegment> segments = new ArrayList<>();
      for (JsonNode itineraryNode : offer.get("itineraries")) {
        for (JsonNode segmentNode : itineraryNode.get("segments")) {
          FlightSegment segment = FlightSegment.builder()
            .flightNumber(segmentNode.get("number").asText())
            .airlineId(segmentNode.get("carrierCode").asText())
            .originAirportId(segmentNode.get("departure").get("iataCode").asText())
            .destinationAirportId(segmentNode.get("arrival").get("iataCode").asText())
            .departureTime(LocalDateTime.parse(segmentNode.get("departure").get("at").asText()))
            .arrivalTime(LocalDateTime.parse(segmentNode.get("arrival").get("at").asText()))
            .build();
          segments.add(segment);
        }
      }

      Itinerary itinerary = Itinerary.builder()
        .id(id)
        .segments(segments)
        .price(price)
        .currency(currency)
        .build();

      itineraries.add(itinerary);
    }

    return IntegrationResponse.builder()
      .itineraries(itineraries)
      .providerId(providerId)
      .success(true)
      .build();
  }

  @Override
  protected String providerId() {
    return PROVIDER_NAME;
  }
}
