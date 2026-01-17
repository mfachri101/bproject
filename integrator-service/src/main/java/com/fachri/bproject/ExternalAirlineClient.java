package com.fachri.bproject;

import com.fachri.bproject.model.FlightItineraries;
import com.fachri.bproject.model.FlightSearchRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExternalAirlineClient {
  public FlightItineraries fetchFromProvider(String providerId, FlightSearchRequest request) {
    return createMockItineraries(providerId, request);
  }

  // create mock itineraries
  private static FlightItineraries createMockItineraries(String providerId, FlightSearchRequest request) {
    return FlightItineraries.builder()
        .providerId(providerId)
        .routeKey(request.getOriginCode() + "-" + request.getDestinationCode() + "-" + request.getDepartureDate() + "-" + request.getSeatClass() + "-" + request.getPaxNumber())
        .itineraries(List.of())
        .build();
  }

}
