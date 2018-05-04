package com.example.backendspring.service;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

/**
 * Created by Aleksey Popryadukhin on 01/05/2018.
 */
public class Utils {

  public static String getRandomString20() {
    return getRandomString(20);
  }

  public static String getRandomString(int length) {
    return RandomStringUtils.randomAlphanumeric(length);
  }

  public static String getRandomUUID() {
    return UUID.randomUUID().toString();
  }
}
