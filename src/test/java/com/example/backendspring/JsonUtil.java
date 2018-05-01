package com.example.backendspring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;
import java.util.List;

import static com.example.backendspring.JacksonObjectMapper.getMapper;

public class JsonUtil {

  public static <T> List<T> readValues(String json, Class<T> clazz) throws IOException {
    ObjectReader reader = getMapper().reader(clazz);
    return reader.<T>readValues(json).readAll();
  }

  public static <T> T readValue(String json, Class<T> clazz) throws IOException {
    return getMapper().readValue(json, clazz);
  }

  public static <T> String writeValue(T obj) throws JsonProcessingException {
    return getMapper().writeValueAsString(obj);
  }
}