package com.fachri.bproject.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Itinerary {
  private String id;
  private List<FlightSegment> segments;
  private Long price;
  private String currency;
}