package com.fachri.bproject.integration.abacus;

import com.fachri.bproject.integration.IntegrationResponse;
import com.fachri.bproject.integration.pkfare.PkFareAuthServiceImpl;
import com.fachri.bproject.integration.pkfare.PkFareIntegrationImpl;
import com.fachri.bproject.model.FlightSearchRequest;
import com.fachri.bproject.model.PaxNumber;
import com.fachri.bproject.model.SeatClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

public class AbacusNdcIntegrationImplTest {

  // init wiremock and stubs from wiremock directory before running this test
  @RegisterExtension
  static WireMockExtension wm = WireMockExtension.newInstance()
    .options(wireMockConfig()
      .dynamicPort()
      .withRootDirectory("../wiremock")
    )
    .build();

  @BeforeEach
  void setUp() {
    for (StubMapping mapping : wm.listAllStubMappings().getMappings()) {
      System.out.println("Stub Mapping: " + mapping.getName());
    }
  }

  @Test
  void testFetch() {
    ObjectMapper objectMapper = new ObjectMapper();
    AbacusNdcIntegrationImpl abacusNdcIntegration = new AbacusNdcIntegrationImpl(
      objectMapper,
      "http://localhost:" + wm.getPort(),
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
      SeatClass.ECONOMY,
      PaxNumber.fromString("1.0.0")
    ));

    IntegrationResponse join = fetch.join();
    assertNotNull(join);
    assertNotNull(join.getItineraries());
    assertFalse(join.getItineraries().isEmpty());
  }
}