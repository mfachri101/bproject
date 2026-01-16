package com.fachri.bproject.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Builder
public class FlightItineraries {
  @Id
  private String id;
  private List<Itinerary> itineraries;
  private String providerId;
  long lastUpdated;
  private String routeKey;
}
