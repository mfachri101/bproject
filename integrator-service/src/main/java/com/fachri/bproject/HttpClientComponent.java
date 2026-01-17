package com.fachri.bproject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class HttpClientComponent {

  @Bean
  @Qualifier("integrationHttpClient")
  public java.net.http.HttpClient httpClient() {
    // http client with pooled connection
    return java.net.http.HttpClient.newBuilder()
        .version(java.net.http.HttpClient.Version.HTTP_2)
        .connectTimeout(java.time.Duration.ofSeconds(10))
        .build();
  }

}
