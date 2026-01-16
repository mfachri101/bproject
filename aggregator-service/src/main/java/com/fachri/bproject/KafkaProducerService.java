package com.fachri.bproject;

import com.fachri.bproject.model.FlightSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

  private final KafkaTemplate<String, FlightSearchRequest> kafkaTemplate;

  public CompletableFuture<List<String>> sendSearchSpec(FlightSearchRequest request) {
    String searchId = UUID.randomUUID().toString();

    // Kafka send returns a CompletableFuture<SendResult<K, V>>
    return kafkaTemplate.send("flight-search-queries", searchId, request)
      .thenApply(result -> {
        // Logic to return your List of Strings (e.g., searchId + partition)
        return List.of(searchId, result.getRecordMetadata().topic());
      });
  }
}
