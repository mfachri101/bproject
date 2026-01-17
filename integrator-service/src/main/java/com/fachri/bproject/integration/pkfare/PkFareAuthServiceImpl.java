package com.fachri.bproject.integration.pkfare;

import com.fachri.bproject.integration.AuthService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class PkFareAuthServiceImpl implements AuthService<String> {
  @Override
  public CompletableFuture<String> getToken() {
    // In a real implementation, you would make an HTTP request to get the token
    // and some caching mechanism would be applied here. For simplicity, we return a static token.
    return CompletableFuture.completedFuture("static-pkfare-token");
  }
}
