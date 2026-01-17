package com.fachri.bproject;

import com.fachri.bproject.model.FlightItineraries;
import com.fachri.bproject.model.FlightSearchRequest;
import com.fachri.bproject.repository.FlightInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class IntegrationProviderService {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(IntegrationProviderService.class);

  private final RoutingConfigService routingConfig;
  private final FlightInventoryRepository mongoRepository;
  private final ExternalAirlineClient airlineClient;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  private static final long STALE_THRESHOLD = 900000; // 15 mins

  @KafkaListener(topics = "flight-search-queries")
  public void processRoute(@Header(KafkaHeaders.RECEIVED_KEY) String searchId, FlightSearchRequest request) {
    String routeKey = String.format("%s-%s-%s",
      request.getOriginCode(), request.getDestinationCode(), request.getDepartureDate());

    // 1. Get providers enabled for this specific route
    List<String> enabledProviders = routingConfig.getEnabledProviders(
      request.getOriginCode(), request.getDestinationCode());

    // 2. Find existing metadata for this route
    List<FlightInventoryRepository.FlightMetadata> cachedMetadata = mongoRepository.findMetadataByRouteKey(routeKey);

    List<CompletableFuture<String>> providerTasks = enabledProviders.stream()
      .map(providerId -> processProvider(searchId, routeKey, request, cachedMetadata))
      .toList();

    // 3. Wait for all providers (Cache or Live) to complete, then return all IDs
    CompletableFuture.allOf(providerTasks.toArray(new CompletableFuture[0]))
      .thenAccept(v -> {
        List<String> finalMongoIds = providerTasks.stream()
          .map(CompletableFuture::join)
          .filter(Objects::nonNull)
          .toList();

        // Signal Aggregation Layer with the list of MongoDB IDs
        LOGGER.info("Sending provider response for searchId: {} with IDs: {}", searchId, finalMongoIds);
        kafkaTemplate.send("provider-response-topic", searchId, finalMongoIds);
      });
  }

  private CompletableFuture<String> processProvider(String routeKey, String providerId,
    FlightSearchRequest request, List<FlightInventoryRepository.FlightMetadata> cachedMetadata) {

    return CompletableFuture.supplyAsync(() -> {
      // Check if this specific provider has fresh data in cache
      Optional<FlightInventoryRepository.FlightMetadata> meta = cachedMetadata.stream()
        .filter(m -> m.getProviderId().equals(providerId))
        .findFirst();

      if (meta.isPresent() && !isStale(meta.get().getLastUpdated())) {
        // CACHE HIT: Return the existing ID immediately
        return meta.get().getId();
      } else {
        // CACHE MISS / STALE: Fetch Live
        try {
          FlightItineraries freshData = airlineClient.fetchFromProvider(providerId, request);
          freshData.setRouteKey(routeKey);
          freshData.setProviderId(providerId);
          freshData.setLastUpdated(System.currentTimeMillis());

          // Save and return the new ID
          String id = mongoRepository.save(freshData).getId();
          return id;
        } catch (Exception e) {
          return null; // Handle provider failure
        }
      }
    });
  }

  private boolean isStale(long lastUpdated) {
    return (System.currentTimeMillis() - lastUpdated) > STALE_THRESHOLD;
  }
}