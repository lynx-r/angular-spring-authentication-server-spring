package com.example.backendspring.controller;

import com.example.backendspring.config.AuthPath;
import com.example.backendspring.model.Answer;
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
  Answer register(@RequestBody RegisterUser registerUser, HttpServletRequest request, HttpServletResponse response) {
    // обрабатываем неавторизованные запрос на регистрацию
    return ((ModelHandlerFunc<RegisterUser>) (data) ->
        secureUserService.register(data)
            .map(Answer::ok)
            .orElseGet(Answer::forbidden))
        .handleRequest(request, response, AuthPath.REGISTER, registerUser);
  }

  @PostMapping("authorize")
  public @ResponseBody
  Answer authorize(@RequestBody RegisterUser registerUser, HttpServletRequest request, HttpServletResponse response) {
    // обрабатываем неавторизованные запрос на авторизацию
    return ((ModelHandlerFunc<RegisterUser>) (data) ->
        secureUserService.authorize(data)
            .map(Answer::ok)
            .orElseGet(Answer::forbidden))
        .handleRequest(request, response, AuthPath.AUTHORIZE, registerUser);
  }
}
