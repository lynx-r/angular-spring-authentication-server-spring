package com.example.backendspring.controller;

import com.example.backendspring.exception.AuthException;
import com.example.backendspring.service.SecureUserService;
import com.example.backendspring.model.Answer;
import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.RegisterUser;
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
@RequestMapping("api")
public class AuthController {

  private SecureUserService secureUserService;

  public AuthController(SecureUserService secureUserService) {
    this.secureUserService = secureUserService;
  }

  @PostMapping("register")
  public @ResponseBody
  Answer register(@RequestBody RegisterUser registerUser, HttpServletRequest request, HttpServletResponse response) throws AuthException {
    Optional<AuthUser> register = secureUserService.register(registerUser);
    return register.map(Answer::ok).orElseThrow(AuthException::new);
  }

  @PostMapping("authorize")
  public @ResponseBody
  Answer authorize(@RequestBody RegisterUser registerUser, HttpServletRequest request, HttpServletResponse response) throws AuthException {
    Optional<AuthUser> register = secureUserService.authorize(registerUser);
    return register.map(Answer::ok).orElseThrow(AuthException::new);
  }

  @PostMapping("authenticate")
  public @ResponseBody
  Answer authorize(@RequestBody AuthUser authUser, HttpServletRequest request, HttpServletResponse response) throws AuthException {
    Optional<AuthUser> register = secureUserService.authenticate(authUser);
    return register.map(Answer::ok).orElseThrow(AuthException::new);
  }
}
