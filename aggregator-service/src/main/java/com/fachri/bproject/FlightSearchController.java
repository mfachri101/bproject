package com.fachri.bproject;

import com.fachri.bproject.model.FlightSearchRequest;
import com.fachri.bproject.model.FlightSearchResponse;
import com.fachri.bproject.model.PaxNumber;
import com.fachri.bproject.model.SeatClass;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

  @RestController
  @RequestMapping("/api/v1/flights")
  @RequiredArgsConstructor
  public class FlightSearchController {

    private final FlightSearchAsyncService searchService;

    @GetMapping("/search/{src}/{dst}/{date}/{passengers}/{seatClass}")
    public CompletableFuture<ResponseEntity<FlightSearchResponse>> search(
      @PathVariable("src") String src,
      @PathVariable("dst") String dst,
      @PathVariable("date") LocalDate date,
      @PathVariable("passengers") String passengers,
      @PathVariable("seatClass") SeatClass seatClass
    ) {
      FlightSearchRequest request = FlightSearchRequest.builder()
        .seatClass(seatClass)
        .originCode(src)
        .destinationCode(dst)
        .paxNumber(PaxNumber.fromString(passengers))
        .departureDate(date)
        .build();
      return searchService.performAsyncSearch(request)
        .thenApply(ResponseEntity::ok)
        .exceptionally(ex -> ResponseEntity.status(504).build());
    }
}
