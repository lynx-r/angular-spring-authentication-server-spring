package com.example.backendspring.controller;

import com.example.backendspring.config.SecuredPath;
import com.example.backendspring.model.Answer;
import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.PingPayload;
import com.example.backendspring.service.PingPongService;
import com.example.backendspring.service.SecureUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Aleksey Popryadukhin on 29/04/2018.
 */
@RestController
@RequestMapping("secured")
public class ProtectedPingPongController {

  private PingPongService pingPongService;
  private SecureUserService secureUserService;

  @Autowired
  public ProtectedPingPongController(PingPongService pingPongService, SecureUserService secureUserService) {
    this.pingPongService = pingPongService;
    this.secureUserService = secureUserService;
  }

  @PostMapping("ping")
  public @ResponseBody
  Answer ping(@RequestBody PingPayload ping, HttpServletRequest request, HttpServletResponse response) {
    return ((SecurityHandlerFunc<AuthUser>) authUser ->
        secureUserService.authenticate(authUser) // Авторизуем пользователя
    ).getAuthUser(request, response, SecuredPath.PING)
        .map(authUser -> // получаме авторизованного пользователя
            ((ModelHandlerFunc<PingPayload>) (data) ->
                pingPongService.getPong(data) // обрабатываем запрос пользователя в сервисе
                    .map(Answer::ok)
                    .orElseGet(Answer::forbidden)
            ).handleRequest(request, response, SecuredPath.PING, ping) // обрабатываем запрос
        ).orElseGet(Answer::forbidden);
  }
}
