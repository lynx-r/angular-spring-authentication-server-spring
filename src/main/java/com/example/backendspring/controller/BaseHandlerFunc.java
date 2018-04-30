package com.example.backendspring.controller;


import com.example.backendspring.config.ErrorMessages;
import com.example.backendspring.config.IPath;
import com.example.backendspring.model.Answer;
import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.EnumSecureRole;
import com.example.backendspring.model.Payload;
import com.example.backendspring.service.SecureUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.example.backendspring.config.RequestConstants.*;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

/**
 * Created by Aleksey Popryaduhin on 16:37 01/10/2017.
 */
public interface BaseHandlerFunc<T extends Payload> {

  Answer process(T data);

  default Answer getAnswer(HttpServletRequest request, HttpServletResponse response, IPath path, T data) {
    String roleStr = request.getHeader(USER_ROLE_HEADER);

    Set<EnumSecureRole> roles = new HashSet<>(Collections.singletonList(EnumSecureRole.ANONYMOUS));
    if (StringUtils.isNotBlank(roleStr)) {
      roles = EnumSecureRole.parseRoles(roleStr);
    }
    if (path.isSecure() || EnumSecureRole.isSecure(roles)) {
      Answer processed = process(data);
      if (processed == null) {
        return getAnonymousAnswer(response);
      }
      return processed;
    } else {
      return getInsecureAnswer(data);
    }
  }

  default Answer getAnonymousAnswer(HttpServletResponse response) {
    String anonymousSession = putSessionAndSetCookieInResponse(response);
    AuthUser authUser = new AuthUser(anonymousSession)
        .setRole(EnumSecureRole.ANONYMOUS);
    return Answer.created(authUser)
        .statusCode(HTTP_FORBIDDEN)
        .message(HTTP_FORBIDDEN, ErrorMessages.UNABLE_TO_AUTHENTICATE);
  }

  default Answer getInsecureAnswer(T data) {
    Answer process = process(data);
    if (process.getBody() instanceof AuthUser) {
      process.setAuthUser((AuthUser) process.getBody());
    }
    return process;
  }

  /**
   * if does not have session give it him
   *
   * @return generate session and set cookie
   */
  default String putSessionAndSetCookieInResponse(HttpServletResponse response) {
    String anonymousSession = SecureUtils.getRandomString(SESSION_LENGTH);
    Cookie cookie = new Cookie(ANONYMOUS_SESSION_HEADER, anonymousSession);
    cookie.setMaxAge(COOKIE_AGE);
    cookie.setHttpOnly(true);
    response.addCookie(cookie);
    System.out.println("Set-Cookie " + ANONYMOUS_SESSION_HEADER + ": " + anonymousSession);
    return anonymousSession;
  }
}
