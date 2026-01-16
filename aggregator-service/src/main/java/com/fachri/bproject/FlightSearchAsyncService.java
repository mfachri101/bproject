package com.fachri.bproject;

import com.fachri.bproject.model.FlightItineraries;
import com.fachri.bproject.model.FlightSearchRequest;
import com.fachri.bproject.model.FlightSearchResponse;
import com.fachri.bproject.repository.FlightItineraryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
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

  // Use a ConcurrentHashMap to track pending requests
  private final Map<String, CompletableFuture<FlightSearchResponse>> pendingRequests = new ConcurrentHashMap<>();

  public CompletableFuture<FlightSearchResponse> performAsyncSearch(FlightSearchRequest request) {
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
  @KafkaListener(topics = "flight-search-results", groupId = "api-gateways")
  public void onSearchResultReceived(ConsumerRecord<String, List<String>> record) {
    String searchId = record.key();
    List<String> mongoKeys = record.value(); // These are the IDs to find in MongoDB

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
