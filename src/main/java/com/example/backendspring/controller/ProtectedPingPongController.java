package com.example.backendspring.controller;

import com.example.backendspring.config.DefendedAuthority;
import com.example.backendspring.exception.AuthException;
import com.example.backendspring.service.AuthRequestService;
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
  private AuthRequestService authRequestService;

  @Autowired
  public ProtectedPingPongController(PingPongService pingPongService, AuthRequestService authRequestService) {
    this.pingPongService = pingPongService;
    this.authRequestService = authRequestService;
  }

  /**
   * Защищённый сервис `ping`. Проверка аутентификации производится прямо в контроллере,
   * но так же её можно добавить в фильтры, которые конфигурируются в `SecurityConfig`
   * @param ping
   * @param request
   * @param response
   * @return
   */
  @PostMapping("ping")
  public @ResponseBody
  Answer ping(@RequestBody PingPayload ping, HttpServletRequest request, HttpServletResponse response) {
    return authRequestService
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
