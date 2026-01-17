package com.fachri.bproject;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoutingConfigService {
  // This could be backed by a DB and cached for better performance
  // For now, we return a static list of enabled providers
  public List<String> getEnabledProviders(String origin, String destination) {
    return List.of("abacus-ndc", "pkfare");
  }
}