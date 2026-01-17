package com.fachri.bproject.integration.pkfare;

import com.fachri.bproject.integration.IntegrationBase;
import com.fachri.bproject.integration.IntegrationResponse;
import com.fachri.bproject.model.FlightSearchRequest;
import com.fachri.bproject.model.FlightSegment;
import com.fachri.bproject.model.Itinerary;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service(PkFareIntegrationImpl.PROVIDER_NAME)
public class PkFareIntegrationImpl extends IntegrationBase {

  public static final String PROVIDER_NAME = "pkfare";

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final PkFareAuthServiceImpl authService;
  private final ObjectMapper objectMapper;

  @Autowired
  public PkFareIntegrationImpl(PkFareAuthServiceImpl authService, ObjectMapper objectMapper, @Value("${integration.pkfare.baseUrl}") String baseUrl, @Qualifier("integrationHttpClient") HttpClient httpClient) {
    super(baseUrl, httpClient);
    this.authService = authService;
    this.objectMapper = objectMapper.copy().configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  protected String providerId() {
    return PROVIDER_NAME;
  }

  @Override
  public CompletableFuture<IntegrationResponse> fetch(FlightSearchRequest flightSearchRequest) {
    // Build request payload
    ObjectNode payload = objectMapper.createObjectNode();
    payload.put("origin", flightSearchRequest.getOriginCode());
    payload.put("destination", flightSearchRequest.getDestinationCode());
    payload.put("departureDate", flightSearchRequest.getDepartureDate().toString());

    return authService.getToken().thenCompose(token -> {
      HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/pkfare/api/v1/flight/search"))
        .header("Authorization", "Bearer " + token)
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
        .build();

      return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(response -> {
          try {
            PkFareIntegrationResponse root = objectMapper.readValue(response.body(), PkFareIntegrationResponse.class);
            IntegrationResponse integrationResponse = toIntegrationResponse(root, providerId());
            return IntegrationResponse.builder()
              .itineraries(integrationResponse.getItineraries())
              .providerId(providerId())
              .success("SUCCESS".equalsIgnoreCase(root.getStatus()))
              .build();
          } catch (Exception e) {
            throw new RuntimeException("Failed to map PKFare response", e);
          }
        });
    }).exceptionally(
      ex -> IntegrationResponse.builder()
        .itineraries(new ArrayList<>())
        .providerId(providerId())
        .success(false)
        .build()
    );
  }

  public static IntegrationResponse toIntegrationResponse(PkFareIntegrationResponse PkFareIntegrationResponse, String providerId) {
    List<Itinerary> itineraries = new ArrayList<>();
    if (PkFareIntegrationResponse.getData() != null && PkFareIntegrationResponse.getData().getSolutions() != null) {
      for (PkFareIntegrationResponse.Solution sol : PkFareIntegrationResponse.getData().getSolutions()) {
        List<FlightSegment> segments = new ArrayList<>();
        if (sol.getFlights() != null) {
          for (PkFareIntegrationResponse.Flight flight : sol.getFlights()) {
            if (flight.getSegments() != null) {
              for (PkFareIntegrationResponse.Segment seg : flight.getSegments()) {
                segments.add(FlightSegment.builder()
                  .flightNumber(seg.getFlightNo())
                  .airlineId(seg.getMarketingCarrier())
                  .originAirportId(seg.getDepCity())
                  .destinationAirportId(seg.getArrCity())
                  .departureTime(LocalDateTime.parse(seg.getDepTime(), FORMATTER))
                  .arrivalTime(LocalDateTime.parse(seg.getArrTime(), FORMATTER))
                  .build());
              }
            }
          }
        }
        Itinerary itinerary = Itinerary.builder()
          .id(sol.getSolutionId())
          .segments(segments)
          .price(sol.getPricing() != null ? (long) sol.getPricing().getTotalPrice() : null)
          .currency(sol.getPricing() != null ? sol.getPricing().getCurrency() : null)
          .build();
        itineraries.add(itinerary);
      }
    }
    return IntegrationResponse.builder()
      .itineraries(itineraries)
      .providerId(providerId)
      .success("SUCCESS".equalsIgnoreCase(PkFareIntegrationResponse.getStatus()))
      .build();
  }
}
