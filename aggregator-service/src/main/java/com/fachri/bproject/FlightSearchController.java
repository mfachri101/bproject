package com.fachri.bproject;

import com.fachri.bproject.model.FlightSearchRequest;
import com.fachri.bproject.model.FlightSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

  @RestController
  @RequestMapping("/api/v1/flights")
  @RequiredArgsConstructor
  public class FlightSearchController {

    private final FlightSearchAsyncService searchService;

    @PostMapping("/search")
    public CompletableFuture<ResponseEntity<FlightSearchResponse>> search(
      @RequestBody FlightSearchRequest request
    ) {
      return searchService.performAsyncSearch(request)
        .thenApply(ResponseEntity::ok)
        .exceptionally(ex -> ResponseEntity.status(504).build());
    }
}
