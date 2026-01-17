 package com.fachri.bproject.integration;

import java.net.http.HttpClient;

public abstract class IntegrationBase implements ProviderIntegration {
  protected String baseUrl;
  protected HttpClient httpClient;

  public IntegrationBase(String baseUrl, HttpClient httpClient) {
    this.baseUrl = baseUrl;
    this.httpClient = httpClient;
  }

  protected abstract String providerId();
}
