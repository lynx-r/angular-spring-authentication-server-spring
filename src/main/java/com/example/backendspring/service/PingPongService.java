package com.example.backendspring.service;

import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.PingPayload;
import com.example.backendspring.model.PongPayload;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Aleksey Popryadukhin on 29/04/2018.
 */
@Service
public class PingPongService {

  public Optional<PongPayload> getPong(PingPayload data, AuthUser authUser) {
    return Optional.of(new PongPayload(data.getPing() + " from " + authUser.getUsername() + " and PONG from server"));
  }

}
