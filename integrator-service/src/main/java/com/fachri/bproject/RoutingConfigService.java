package com.fachri.bproject;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoutingConfigService {
  // This could be backed by a DB or a Config Map
  public List<String> getEnabledProviders(String origin, String destination) {
    // Logic: if international, return ["AMADEUS", "SABRE"]; if domestic, return ["SOUTHWEST"]
    return List.of("abacus-ndc", "pkfare");
  }
}