package com.example.backendspring.service;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

/**
 * Created by Aleksey Popryadukhin on 01/05/2018.
 */
public class Utils {

  private static final int RANDOM_STRING_LENGTH_20 = 20;
  private static final int RANDOM_STRING_LENGTH_32 = 32;

  public static String getRandomString20() {
    return getRandomString(RANDOM_STRING_LENGTH_20);
  }

  public static String getRandomString(int length) {
    return RandomStringUtils.randomAscii(length);
  }

  public static String getRandomUUID() {
    return UUID.randomUUID().toString();
  }

  public static String getRandomString32() {
    return getRandomString(RANDOM_STRING_LENGTH_32);
  }
}
