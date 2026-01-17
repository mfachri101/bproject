package com.fachri.bproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Itinerary {
  private String id;
  private List<FlightSegment> segments;
  private Long price;
  private String currency;
}