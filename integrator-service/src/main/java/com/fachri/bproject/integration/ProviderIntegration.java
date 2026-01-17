package com.fachri.bproject.integration;

import com.fachri.bproject.model.FlightSearchRequest;

import java.util.concurrent.CompletableFuture;

public interface ProviderIntegration {

  CompletableFuture<IntegrationResponse> fetch(FlightSearchRequest flightSearchRequest);

}
