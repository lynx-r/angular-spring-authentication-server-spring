package com.example.backendspring.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Aleksey Popryaduhin on 08:57 11/06/2017.
 */
@Component
@Getter
public class AppProperties {

  @Value("${ORIGIN_URL}")
  private String originUrl;

  @Value("${HEADERS}")
  private String headers;

  @Value("${METHODS}")
  private String methods;

  @Value("${TOKEN_LENGTH}")
  private int tokenLength;
}
