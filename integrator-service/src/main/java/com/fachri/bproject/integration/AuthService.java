package com.fachri.bproject.integration;

import java.util.concurrent.CompletableFuture;

public interface AuthService<T> {
  CompletableFuture<T> getToken();
}
