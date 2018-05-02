package com.example.backendspring.config;

import java.util.ResourceBundle;

/**
 * Created by Aleksey Popryadukhin on 30/04/2018.
 */
public class ErrorMessages {
  public static final String ACCESS_DENIED = "ACCESS_DENIED";
  public static final String UNABLE_AUTHENTICATE = "UNABLE_AUTHENTICATE";

  private static ResourceBundle resourceBundle =
      ResourceBundle.getBundle("ErrorMessages");

  public static String getString(String key) {
    return resourceBundle.getString(key);
  }
}
