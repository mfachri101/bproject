package com.fachri.bproject.repository;

import com.fachri.bproject.model.FlightItineraries;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightInventoryRepository extends MongoRepository<FlightItineraries, String> {

  // Optimized query using routeKey
  List<FlightMetadata> findMetadataByRouteKey(String routeKey);

// Projection interface to fetch only necessary fields
interface FlightMetadata {
  String getProviderId();
  long getLastUpdated();
  String getId(); // MongoDB ID to fetch full record later
}
}
