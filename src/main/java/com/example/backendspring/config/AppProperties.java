package com.example.backendspring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Aleksey Popryaduhin on 08:57 11/06/2017.
 */
@Component
public class AppProperties {

  @Value("${CLIENT_URL}")
  private String clientUrls;

  @Value("${HEADERS}")
  private String corsHeaders;
  @Value("${METHODS}")
  private String corsMethods;

  public String getClientUrls() {
    return clientUrls;
  }

  public String getCorsHeaders() {
    return corsHeaders;
  }

  public String getCorsMethods() {
    return corsMethods;
  }
}
