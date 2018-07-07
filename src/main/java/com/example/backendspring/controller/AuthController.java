package com.example.backendspring.controller;

import com.example.backendspring.config.DefendedAuthority;
import com.example.backendspring.function.TrustedHandlerFunc;
import com.example.backendspring.model.Answer;
import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.UserCredentials;
import com.example.backendspring.service.AuthRequestService;
import com.example.backendspring.service.SecureUserService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Created by Aleksey Popryadukhin on 29/04/2018.
 */
@RestController
@RequestMapping("security")
public class AuthController {

  private AuthRequestService authRequestService;
  private SecureUserService secureUserService;

  public AuthController(AuthRequestService authRequestService, SecureUserService secureUserService) {
    this.authRequestService = authRequestService;
    this.secureUserService = secureUserService;
  }

  @PostMapping("auth-request")
  public @ResponseBody
  Answer authRequest(@Valid @RequestBody UserCredentials userCredentials, HttpServletRequest request, HttpServletResponse response) {
    // возвращаем клиенту соль, cost и misc
    return ((TrustedHandlerFunc<UserCredentials>) (data) ->
        Answer.ok(secureUserService.authRequest(data)))
        .handleAuthRequest(response, userCredentials);
  }

  @PostMapping("register")
  public @ResponseBody
  Answer register(@Valid @RequestBody UserCredentials userCredentials, HttpServletRequest request, HttpServletResponse response) {
    // обрабатываем не авторизованные запрос на регистрацию
    return ((TrustedHandlerFunc<UserCredentials>) (data) ->
        Answer.ok(secureUserService.register(data)))
        .handleAuthRequest(response, userCredentials);
  }

  @PutMapping("register")
  public @ResponseBody
  Answer postRegister(@Valid @RequestBody UserCredentials userCredentials, HttpServletRequest request, HttpServletResponse response) {
    // обрабатываем не авторизованные запрос на регистрацию
    return ((TrustedHandlerFunc<UserCredentials>) (data) ->
        Answer.ok(secureUserService.postRegister(data)))
        .handleAuthRequest(response, userCredentials);
  }

  @PostMapping("authorize")
  public @ResponseBody
  Answer authorize(@Valid @RequestBody UserCredentials userCredentials, HttpServletRequest request, HttpServletResponse response) {
    // обрабатываем не авторизованные запрос на авторизацию
    return ((TrustedHandlerFunc<UserCredentials>) (data) ->
        Answer.ok(secureUserService.authorize(data)))
        .handleAuthRequest(response, userCredentials);
  }

  @PostMapping("authenticate")
  public @ResponseBody
  Answer authenticate(@RequestBody AuthUser authUser, HttpServletRequest request, HttpServletResponse response) {
    // обрабатываем не авторизованные запрос на аутентификацию
    return ((TrustedHandlerFunc<AuthUser>) (data) ->
        Answer.ok(secureUserService.authenticate(data)))
        .handleAuthRequest(response, authUser);
  }

  @GetMapping("logout")
  public @ResponseBody
  Answer logout(HttpServletRequest request, HttpServletResponse response) {
    // обрабатываем авторизованные запрос на выход
    AuthUser authUser = authRequestService
        .getAuthenticatedUser(request, DefendedAuthority.PING);
    // получаем авторизованного пользователя
    return ((TrustedHandlerFunc<AuthUser>) (data) ->
        Answer.ok(secureUserService.logout(data))
    ).handleAuthRequest(response, authUser); // обрабатываем запрос
  }
}
