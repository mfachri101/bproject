package com.fachri.bproject.model;

import lombok.Value;
import org.springframework.data.annotation.Id;

import java.time.ZoneId;

@Value
public class Airport {
  @Id
  String id; // "JFK"
  String name;
  String city;
  ZoneId timezone;
}
