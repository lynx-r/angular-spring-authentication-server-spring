package com.example.backendspring;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

import static com.example.backendspring.JacksonObjectMapper.getMapper;

public class JsonUtil {

  public static <T> T readValue(String json, Class<T> clazz) {
    try {
      return getMapper().readValue(json, clazz);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static <T> String writeValue(T obj)  {
    try {
      return getMapper().writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "";
    }
  }
}