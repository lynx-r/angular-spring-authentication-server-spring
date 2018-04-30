package com.example.backendspring.function;


import com.example.backendspring.config.ErrorMessages;
import com.example.backendspring.model.Answer;
import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.EnumAuthority;
import com.example.backendspring.model.Payload;
import com.example.backendspring.service.SecureUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static com.example.backendspring.config.RequestConstants.*;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

/**
 * Created by Aleksey Popryaduhin on 16:37 01/10/2017.
 */
public interface BaseHandlerFunc<T extends Payload> {

  Answer process(T data);

  default Answer getAnswer(HttpServletResponse response, T data) {
    Answer processed = process(data);
    if (processed == null) {
      return getAnonymousAnswer(response);
    }
    return processed;
  }

  default Answer getAnonymousAnswer(HttpServletResponse response) {
    String anonymousSession = putSessionAndSetCookieInResponse(response);
    AuthUser authUser = new AuthUser(anonymousSession).setAuthority(EnumAuthority.ANONYMOUS);
    return Answer.created(authUser)
        .statusCode(HTTP_FORBIDDEN)
        .message(HTTP_FORBIDDEN, ErrorMessages.getString(ErrorMessages.UNABLE_AUTHENTICATE));
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
    return anonymousSession;
  }
}
