package com.fachri.bproject.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class FlightSegment {
  String flightNumber;

  // Using IDs instead of full objects
  String airlineId;
  String originAirportId;
  String destinationAirportId;

  LocalDateTime departureTime;
  LocalDateTime arrivalTime;
  String aircraftCode;
}