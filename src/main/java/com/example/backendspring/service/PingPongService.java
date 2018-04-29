package com.example.backendspring.service;

import com.example.backendspring.model.PongPayload;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Aleksey Popryadukhin on 29/04/2018.
 */
@Service
public class PingPongService {

  public Optional<PongPayload> getPong() {
    return Optional.of(new PongPayload("PONG"));
  }

}
