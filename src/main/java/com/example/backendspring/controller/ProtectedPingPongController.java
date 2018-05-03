package com.example.backendspring.controller;

import com.example.backendspring.config.DefendedAuthority;
import com.example.backendspring.exception.AuthException;
import com.example.backendspring.function.AuthenticateRequestService;
import com.example.backendspring.function.TrustedHandlerFunc;
import com.example.backendspring.model.Answer;
import com.example.backendspring.model.PingPayload;
import com.example.backendspring.service.PingPongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Aleksey Popryadukhin on 29/04/2018.
 */
@RestController
@RequestMapping("defended")
public class ProtectedPingPongController {

  private PingPongService pingPongService;
  private AuthenticateRequestService authenticateRequestService;

  @Autowired
  public ProtectedPingPongController(PingPongService pingPongService, AuthenticateRequestService authenticateRequestService) {
    this.pingPongService = pingPongService;
    this.authenticateRequestService = authenticateRequestService;
  }

  @PostMapping("ping")
  public @ResponseBody
  Answer ping(@RequestBody PingPayload ping, HttpServletRequest request, HttpServletResponse response) {
    return authenticateRequestService
        .getAuthenticatedUser(request, DefendedAuthority.PING)
        .map(authUser -> // получаем авторизованного пользователя
            ((TrustedHandlerFunc<PingPayload>) (data) ->
                pingPongService.getPong(data, authUser) // обрабатываем запрос пользователя в сервисе
                    .map(Answer::ok)
                    .orElseGet(Answer::forbidden)
            ).handleRequest(response, ping, authUser) // обрабатываем запрос
        ).orElseThrow(AuthException::forbidden);
  }
}
