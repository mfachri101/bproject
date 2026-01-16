package com.fachri.bproject.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class FlightSearchRequest {
  String originCode;
  String destinationCode;
  LocalDate departureDate;
  SeatClass seatClass;
  PaxNumber paxNumber;
}

