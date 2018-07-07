package com.example.backendspring.service;

import org.junit.Test;

/**
 * Created by Aleksey Popryadukhin on 07/07/2018.
 */
public class SecureUtilsTest {


  @Test
  public void genRandomString() {
    String str = Utils.getRandomString32();
    System.out.println(str);
  }
}