package com.fachri.bproject.model;

import lombok.Value;
import org.springframework.data.annotation.Id;

@Value
public class Airline {
  @Id
  String id; // "DL"
  String name;
}