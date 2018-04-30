package com.example.backendspring.controller;

import com.example.backendspring.config.SecuredAuthority;
import com.example.backendspring.function.TrustedHandlerFunc;
import com.example.backendspring.function.SecureHandlerFunc;
import com.example.backendspring.model.Answer;
import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.RegisterUser;
import com.example.backendspring.service.SecureUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Aleksey Popryadukhin on 29/04/2018.
 */
@RestController
@RequestMapping("security")
public class AuthController {

  private SecureUserService secureUserService;

  @Autowired
  public AuthController(SecureUserService secureUserService) {
    this.secureUserService = secureUserService;
  }

  @PostMapping("register")
  public @ResponseBody
  Answer register(@RequestBody RegisterUser registerUser, HttpServletResponse response) {
    // обрабатываем не авторизованные запрос на регистрацию
    return ((TrustedHandlerFunc<RegisterUser>) (data) ->
        secureUserService.register(data)
            .map(Answer::ok)
            .orElseGet(Answer::forbidden))
        .handleAuthRequest(response, registerUser);
  }

  @PostMapping("authorize")
  public @ResponseBody
  Answer authorize(@RequestBody RegisterUser registerUser, HttpServletResponse response) {
    // обрабатываем не авторизованные запрос на авторизацию
    return ((TrustedHandlerFunc<RegisterUser>) (data) ->
        secureUserService.authorize(data)
            .map(Answer::ok)
            .orElseGet(Answer::forbidden))
        .handleAuthRequest(response, registerUser);
  }

  @PostMapping("authenticate")
  public @ResponseBody
  Answer authenticate(@RequestBody AuthUser registerUser, HttpServletResponse response) {
    // обрабатываем не авторизованные запрос на аутентификацию
    return ((TrustedHandlerFunc<AuthUser>) (data) ->
        secureUserService.authenticate(data)
            .map(Answer::ok)
            .orElseGet(Answer::forbidden))
        .handleAuthRequest(response, registerUser);
  }

  @PostMapping("logout")
  public @ResponseBody
  Answer logout(@RequestBody AuthUser logoutUser, HttpServletRequest request, HttpServletResponse response) {
    // обрабатываем авторизованные запрос на выход
    return ((SecureHandlerFunc) authUser ->
        secureUserService.authenticate(authUser) // Авторизуем пользователя
    ).getAuthUser(request, SecuredAuthority.PING)
        .map(authUser -> // получаме авторизованного пользователя
            ((TrustedHandlerFunc<AuthUser>) (data) ->
                secureUserService.logout(data)
                    .map(Answer::ok)
                    .orElseGet(Answer::forbidden)
            ).handleAuthRequest(response, authUser) // обрабатываем запрос
        ).orElseGet(Answer::forbidden);
  }
}
