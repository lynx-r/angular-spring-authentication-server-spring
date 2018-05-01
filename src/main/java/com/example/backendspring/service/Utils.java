package com.example.backendspring.service;

import org.apache.commons.lang3.RandomStringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
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

  public static String getCookieValue(HttpServletRequest req, String cookieName) {
    if (req.getCookies() != null) {
      return Arrays.stream(req.getCookies())
          .filter(c -> c.getName().equals(cookieName))
          .findFirst()
          .map(Cookie::getValue)
          .orElse(null);
    }
    return req.getSession(true).getId();
  }

  public static String getRandomUUID() {
    return UUID.randomUUID().toString();
  }
}
