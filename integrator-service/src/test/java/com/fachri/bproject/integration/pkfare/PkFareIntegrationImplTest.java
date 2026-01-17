package com.fachri.bproject.integration.pkfare;

import com.fachri.bproject.integration.IntegrationResponse;
import com.fachri.bproject.model.FlightSearchRequest;
import com.fachri.bproject.model.PaxNumber;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class PkFareIntegrationImplTest {

  @BeforeEach
  void setUp() {

  }

  @Test
  public void testFetch() {
    ObjectMapper objectMapper = new ObjectMapper();
    PkFareIntegrationImpl pkFareIntegration = new PkFareIntegrationImpl(
      new PkFareAuthServiceImpl(),
      objectMapper,
      "http://localhost:9090",
      HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .connectTimeout(Duration.ofSeconds(10))
        .build()
    );
    CompletableFuture<IntegrationResponse> fetch = pkFareIntegration.fetch(new FlightSearchRequest(
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