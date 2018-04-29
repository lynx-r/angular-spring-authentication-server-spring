package com.example.backendspring.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.example.backendspring.config.RequestConstants.ANONYMOUS_SESSION_HEADER;
import static com.example.backendspring.config.RequestConstants.COOKIE_AGE;
import static com.example.backendspring.config.RequestConstants.SESSION_LENGTH;

public class SecureUtils {

  public static String encrypt(String key, String initVector, String value) {
    try {
      IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
      SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

      byte[] encrypted = cipher.doFinal(value.getBytes());

      return Base64.encodeBase64String(encrypted);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return null;
  }

  public static String decrypt(String key, String initVector, String encrypted)
      throws IllegalBlockSizeException, BadPaddingException {
    try {
      IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
      SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
      byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

      return new String(original);
    } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
        | InvalidKeyException | NoSuchPaddingException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String digest(String data) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    return new String(digest.digest(data.getBytes(StandardCharsets.UTF_8)));
  }

  public static String getRandomString(int length) {
    return RandomStringUtils.randomAlphanumeric(length);
  }

  /**
   * if does not have session give it him
   *
   * @return generate session and set cookie
   * @param response
   */
  public static String getSessionAndSetCookieInResponse(HttpServletResponse response) {
    String anonymousSession = SecureUtils.getRandomString(SESSION_LENGTH);
    Cookie cookie = new Cookie(ANONYMOUS_SESSION_HEADER, anonymousSession);
    cookie.setMaxAge(COOKIE_AGE);
    cookie.setHttpOnly(true);
    response.addCookie(cookie);
    System.out.println("Set-Cookie " + ANONYMOUS_SESSION_HEADER + ": " + anonymousSession);
    return anonymousSession;
  }
}