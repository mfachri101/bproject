package com.fachri.bproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightItineraries {
  @Id
  private String id;
  private List<Itinerary> itineraries;
  private String providerId;
  long lastUpdated;
  private String routeKey;
}
