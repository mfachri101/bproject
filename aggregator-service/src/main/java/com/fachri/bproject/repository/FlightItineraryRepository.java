package com.fachri.bproject.repository;

import com.fachri.bproject.model.FlightItineraries;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightItineraryRepository extends MongoRepository<FlightItineraries, String> {
  // Standard CRUD operations
}
