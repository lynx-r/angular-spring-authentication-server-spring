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

  @Value("${CLIENT_URL}")
  private String clientUrls;

  @Value("${HEADERS}")
  private String corsHeaders;

  @Value("${METHODS}")
  private String corsMethods;

  @Value("${TOKEN_LENGTH}")
  private int tokenLength;

  @Value("${RANDOM_STRING_LENGTH}")
  private int randomStringLength;
}
