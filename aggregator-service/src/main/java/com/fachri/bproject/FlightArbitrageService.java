package com.fachri.bproject;

import com.fachri.bproject.model.Airline;
import com.fachri.bproject.model.Airport;
import com.fachri.bproject.model.FlightItineraries;
import com.fachri.bproject.model.FlightSearchResponse;
import com.fachri.bproject.model.Itinerary;
import com.fachri.bproject.repository.AirlineRepository;
import com.fachri.bproject.repository.AirportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightArbitrageService {

  private final AirlineRepository airlineRepository;
  private final AirportRepository airportRepository;

  public FlightSearchResponse mergeAndArbitrage(List<FlightItineraries> providerResults) {
    // 1. Flatten all itineraries from all providers into one list
    List<Itinerary> allItineraries = providerResults.stream()
      .flatMap(pr -> pr.getItineraries().stream())
      .toList();

    // 2. Arbitrage: Group by a unique "Flight Signature" to find duplicates
    // If two providers offer the same flight, we keep only the cheapest one
    Collection<Itinerary> cheapestItineraries = allItineraries.stream()
      .collect(Collectors.toMap(
        this::getUniqueFlightSignature, // Key
        itinerary -> itinerary,         // Value
        (existing, replacement) ->      // Merge function: keep cheaper
          existing.getPrice()
            .compareTo(replacement.getPrice()) <= 0
            ? existing : replacement
      ))
      .values();

    // 3. Sort by price ascending
    List<Itinerary> sortedResults = cheapestItineraries.stream()
      .sorted(Comparator.comparing(Itinerary::getPrice))
      .collect(Collectors.toList());

    // 4. Find any found airports and airlines for enrichment
    Set<String> airportCodes = new HashSet<>(16);
    Set<String> airlineIds = new HashSet<>(16);
    for (Itinerary itinerary : sortedResults) {
      for (var segment : itinerary.getSegments()) {
        airportCodes.add(segment.getOriginAirportId());
        airportCodes.add(segment.getDestinationAirportId());
        airlineIds.add(segment.getAirlineId());
      }
    }

    Map<String, Airport> airportIdToName = airlineRepository.findNamesByIds(airportCodes);
    Map<String, Airline> airlineIdToName = airportRepository.findNamesByIds(airlineIds);

    return FlightSearchResponse.builder()
      .itineraries(sortedResults)
      .airlineMap(airlineIdToName)
      .airportMap(airportIdToName)
      .build();
  }

  /**
   * Creates a unique key for an itinerary based on flight numbers and times.
   * This helps identify when Provider A and Provider B are selling the same trip.
   */
  private String getUniqueFlightSignature(Itinerary itinerary) {
    return itinerary.getSegments().stream()
      .map(s -> s.getAirlineId() + s.getFlightNumber() + s.getDepartureTime())
      .collect(Collectors.joining("|"));
  }
}