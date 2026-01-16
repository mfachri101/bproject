package com.fachri.bproject.repository;

import com.fachri.bproject.model.Airport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirportRepository extends MongoRepository<Airport, String> {
  // Standard CRUD operations
}
