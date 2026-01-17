package com.fachri.bproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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