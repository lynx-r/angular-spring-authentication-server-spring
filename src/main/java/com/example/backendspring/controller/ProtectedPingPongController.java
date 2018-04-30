package com.example.backendspring.controller;

import com.example.backendspring.config.SecuredAuthority;
import com.example.backendspring.function.ModelHandlerFunc;
import com.example.backendspring.function.SecurityHandlerFunc;
import com.example.backendspring.model.Answer;
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
    return ((SecurityHandlerFunc) authUser ->
        secureUserService.authenticate(authUser) // Авторизуем пользователя
    ).getAuthUser(request, SecuredAuthority.PING)
        .map(authUser -> // получаме авторизованного пользователя
            ((ModelHandlerFunc<PingPayload>) (data) ->
                pingPongService.getPong(data, authUser) // обрабатываем запрос пользователя в сервисе
                    .map(Answer::ok)
                    .orElseGet(Answer::forbidden)
            ).handleRequest(request, response, SecuredAuthority.PING, ping) // обрабатываем запрос
        ).orElseGet(Answer::forbidden);
  }
}
