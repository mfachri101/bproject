package com.fachri.bproject.integration;

import com.fachri.bproject.model.FlightItineraries;
import com.fachri.bproject.model.Itinerary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationResponse {
  List<Itinerary> itineraries;
  String providerId;
  boolean success;

  public FlightItineraries toFlightItineraries() {
    return FlightItineraries.builder()
        .itineraries(itineraries)
        .providerId(providerId)
        .build();
  }
}
