package com.fachri.bproject.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class FlightSearchResponse {
  // The actual search results
  private List<Itinerary> itineraries;

  // The "Dictionary" to resolve IDs on the frontend
  private Map<String, Airport> airportMap;
  private Map<String, Airline> airlineMap;
}