package com.fachri.bproject.integration.abacus;

import com.fachri.bproject.integration.IntegrationResponse;
import com.fachri.bproject.integration.pkfare.PkFareAuthServiceImpl;
import com.fachri.bproject.integration.pkfare.PkFareIntegrationImpl;
import com.fachri.bproject.model.FlightSearchRequest;
import com.fachri.bproject.model.PaxNumber;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class AbacusNdcIntegrationImplTest {
  @Test
  void testFetch() {
    ObjectMapper objectMapper = new ObjectMapper();
    AbacusNdcIntegrationImpl abacusNdcIntegration = new AbacusNdcIntegrationImpl(
      objectMapper,
      "http://localhost:9090",
      HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .connectTimeout(Duration.ofSeconds(10))
        .build()
    );
    CompletableFuture<IntegrationResponse> fetch = abacusNdcIntegration.fetch(new FlightSearchRequest(
      "CGK",
      "DPS",
      LocalDate.now().plusDays(7),
      null,
      PaxNumber.fromString("1.0.0")
    ));

    IntegrationResponse join = fetch.join();
    assertNotNull(join);
    assertNotNull(join.getItineraries());
    assertFalse(join.getItineraries().isEmpty());
  }
}