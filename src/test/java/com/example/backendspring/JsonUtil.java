package com.example.backendspring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtil {

  private static ObjectMapper mapper = new ObjectMapper();

  public static <T> T readValue(String json, Class<T> clazz) {
    try {
      return mapper.readValue(json, clazz);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static <T> String writeValue(T obj)  {
    try {
      return mapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "";
    }
  }
}