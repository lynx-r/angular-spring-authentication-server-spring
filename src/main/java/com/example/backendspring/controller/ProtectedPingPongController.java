package com.example.backendspring.controller;

import com.example.backendspring.config.Path;
import com.example.backendspring.model.Answer;
import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.PingPayload;
import com.example.backendspring.service.PingPongService;
import com.example.backendspring.service.SecureUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Created by Aleksey Popryadukhin on 29/04/2018.
 */
@RequestMapping("secure")
public class ProtectedPingPongController implements ModelHandlerFunc<PingPayload> {

  private PingPongService pingPongService;
  private SecureUserService secureUserService;

  public ProtectedPingPongController(PingPongService pingPongService, SecureUserService secureUserService) {
    this.pingPongService = pingPongService;
    this.secureUserService = secureUserService;
  }

  @PostMapping("pong")
  public @ResponseBody
  Answer pong(@RequestBody PingPayload ping, HttpServletRequest request, HttpServletResponse response) {
    return handleRequest(request, response, Path.PONG, ping);
  }

  @Override
  public Answer process(PingPayload data, Optional<AuthUser> token) {
    return pingPongService.getPong()
        .map(Answer::ok)
        .orElse(null);
  }

  @Override
  public Optional<AuthUser> isAuthenticated(AuthUser authUser) {
    return secureUserService.authenticate(authUser);
  }
}
