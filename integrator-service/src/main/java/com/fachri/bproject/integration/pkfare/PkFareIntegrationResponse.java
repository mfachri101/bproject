package com.fachri.bproject.integration.pkfare;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PkFareIntegrationResponse {
  private String status;
  private String msg;
  private DataDto data;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DataDto {
    private String shoppingId;
    private List<Solution> solutions;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Solution {
    private String solutionId;
    private List<Flight> flights;
    private Pricing pricing;
    private Policy policy;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Flight {
    private List<Segment> segments;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Segment {
    private String depCity;
    private String arrCity;
    private String depTime;
    private String arrTime;
    private String marketingCarrier;
    private String flightNo;
    private String cabin;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Pricing {
    private String currency;
    private double adultPrice;
    private double adultTax;
    private double totalPrice;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Policy {
    private boolean isRefundable;
    private String baggage;
  }
}