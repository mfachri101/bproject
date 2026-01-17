package com.fachri.bproject;

import com.fachri.bproject.model.Airport;
import com.fachri.bproject.model.FlightItineraries;
import com.fachri.bproject.model.FlightSearchRequest;
import com.fachri.bproject.model.FlightSearchResponse;
import com.fachri.bproject.repository.AirlineRepository;
import com.fachri.bproject.repository.AirportRepository;
import com.fachri.bproject.repository.FlightItineraryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FlightSearchAsyncService {

  private final KafkaProducerService kafkaProducer;
  private final FlightItineraryRepository mongoRepository;
  private final FlightArbitrageService arbitrageService;
  private final AirportRepository airportRepository;

  // Use a ConcurrentHashMap to track pending requests
  private final Map<String, CompletableFuture<FlightSearchResponse>> pendingRequests = new ConcurrentHashMap<>();

  public CompletableFuture<FlightSearchResponse> performAsyncSearch(FlightSearchRequest request) {
    // Validate airports
    boolean originExists = airportRepository.existsById(request.getOriginCode());
    boolean destinationExists = airportRepository.existsById(request.getDestinationCode());

    if (!originExists || !destinationExists) {
      CompletableFuture<FlightSearchResponse> failed = new CompletableFuture<>();
      failed.completeExceptionally(new IllegalArgumentException("Invalid origin or destination airport"));
      return failed;
    }

    // Validate pax numbers
    // rules: adults >= 1, adults + children <= 7, infants <= adults, infants <= 4
    int adults = request.getPaxNumber().getAdult();
    int children = request.getPaxNumber().getChild();
    int infants = request.getPaxNumber().getInfant();
    if (adults < 1 || (adults + children) > 7 || infants > adults || infants > 4) {
      CompletableFuture<FlightSearchResponse> failed = new CompletableFuture<>();
      failed.completeExceptionally(new IllegalArgumentException("Invalid passenger numbers"));
      return failed;
    }

    // validate date
    // depart date must not be in the past, no longer than 1 year from now
    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate oneYearFromNow = today.plusYears(1);
    if (request.getDepartureDate().isBefore(today) || request.getDepartureDate().isAfter(oneYearFromNow)) {
      CompletableFuture<FlightSearchResponse> failed = new CompletableFuture<>();
      failed.completeExceptionally(new IllegalArgumentException("Invalid departure date"));
      return failed;
    }

    // 1. Send to Kafka (returns CompletableFuture<List<String>>)
    return kafkaProducer.sendSearchSpec(request)
      .thenCompose(infoList -> {
        String searchId = infoList.getFirst(); // The unique ID for this search

        // 2. Create a "promise" that will be completed when the response Kafka message arrives
        CompletableFuture<FlightSearchResponse> responseFuture = new CompletableFuture<>();
        pendingRequests.put(searchId, responseFuture);

        // Add a timeout to prevent memory leaks if Kafka never responds
        return responseFuture.orTimeout(30, TimeUnit.SECONDS)
          .whenComplete((res, ex) -> pendingRequests.remove(searchId));
      });
  }

  // 3. The Kafka Listener that receives the "Done" signal
  @KafkaListener(topics = "provider-response-topic", groupId = "api-gateways")
  public void onSearchResultReceived(@Header(KafkaHeaders.RECEIVED_KEY) String searchId, List<String> mongoKeys) {
    CompletableFuture<FlightSearchResponse> future = pendingRequests.get(searchId);

    if (future != null) {
      // 1. Fetch all provider records from MongoDB
      List<FlightItineraries> rawResults = mongoRepository.findAllById(mongoKeys);

      // 2. Perform Arbitrage/Merging
      FlightSearchResponse finalResponse = arbitrageService.mergeAndArbitrage(rawResults);

      // 3. Complete the web request
      future.complete(finalResponse);
    }
  }
}
