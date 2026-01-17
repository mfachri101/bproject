package com.fachri.bproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchRequest {
  String originCode;
  String destinationCode;
  LocalDate departureDate;
  LocalDate returnDate;
  SeatClass seatClass;
  PaxNumber paxNumber;
}

