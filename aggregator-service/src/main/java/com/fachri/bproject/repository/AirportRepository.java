package com.fachri.bproject.repository;

import com.fachri.bproject.model.Airline;
import com.fachri.bproject.model.Airport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;

@Repository
public interface AirportRepository extends MongoRepository<Airport, String> {
  // Standard CRUD operations
}
