package com.fachri.bproject.repository;

import com.fachri.bproject.model.Airline;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirlineRepository extends MongoRepository<Airline, String> {
  // Standard CRUD operations
}
