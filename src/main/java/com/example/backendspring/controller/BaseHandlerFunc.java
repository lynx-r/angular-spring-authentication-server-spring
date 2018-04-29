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
import java.util.*;

import static com.example.backendspring.config.RequestConstants.*;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

/**
 * Created by Aleksey Popryaduhin on 16:37 01/10/2017.
 */
public interface BaseHandlerFunc<T extends Payload> {

  Answer process(T data, Optional<AuthUser> token);

  Optional<AuthUser> isAuthenticated(AuthUser authUser);

  default Answer getAnswer(HttpServletRequest request, HttpServletResponse response, IPath path, T data) {
    String userSession = getOrCreateSession(request, response);
    String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);
    String roleStr = request.getHeader(USER_ROLE_HEADER);

    Set<EnumSecureRole> roles = new HashSet<>(Collections.singletonList(EnumSecureRole.ANONYMOUS));
    if (StringUtils.isNotBlank(roleStr)) {
      roles = EnumSecureRole.parseRoles(roleStr);
    }
    AuthUser internalUserRole = getInternalUserRole(accessToken, userSession);
    if (path.isSecure() || EnumSecureRole.isSecure(roles)) {
      Optional<AuthUser> authenticated = isAuthenticated(internalUserRole);
      if (!authenticated.isPresent()) {
        return getForbiddenAnswer(response);
      }
      AuthUser securedUser = authenticated.get();
      if (!hasRights(securedUser.getRoles(), path)) {
        return getForbiddenAnswer(response);
      }
      Answer securedAnswer = getSecureAnswer(data, securedUser);
      if (securedAnswer == null) {
        return getForbiddenAnswer(response);
      }
      return securedAnswer;
    } else {
      return getInsecureAnswer(data, internalUserRole);
    }
  }

  default boolean hasRights(Set<EnumSecureRole> roles, IPath path) {
    if (roles.contains(EnumSecureRole.BAN)) {
      return false;
    }
    return path.getRoles().isEmpty() || path.getRoles().containsAll(roles);
  }

  default Answer getSecureAnswer(T data, AuthUser authenticated) {
    Answer processed = process(data, Optional.of(authenticated));
    processed.setAuthUser(authenticated);
    return processed;
  }

  default Answer getForbiddenAnswer(HttpServletResponse response) {
    String anonymousSession = getSessionAndSetCookieInResponse(response);
    AuthUser authUser = new AuthUser(anonymousSession)
        .setRole(EnumSecureRole.ANONYMOUS);
    return Answer.created(authUser)
        .statusCode(HTTP_FORBIDDEN)
        .message(HTTP_FORBIDDEN, ErrorMessages.UNABLE_TO_AUTHENTICATE);
  }

  default Answer getInsecureAnswer(T data, AuthUser authUser) {
    Answer process = process(data, Optional.ofNullable(authUser));
    if (process.getBody() instanceof AuthUser) {
      process.setAuthUser((AuthUser) process.getBody());
    }
    return process;
  }

  default String getOrCreateSession(HttpServletRequest request, HttpServletResponse response) {
    // take user session which user got after login
    String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);
    if (StringUtils.isBlank(accessToken)) {
      // if anonymous user
      String anonymousSession = getCookieValue(request, ANONYMOUS_SESSION_HEADER);
//      String anonymousSession = request.cookie(ANONYMOUS_SESSION_HEADER);
      if (StringUtils.isBlank(anonymousSession)) {
        anonymousSession = getSessionAndSetCookieInResponse(response);
      }
      // return anonym session
      return anonymousSession;
    }
    // return transfer session
    return request.getHeader(USER_SESSION_HEADER);
  }

  /**
   * if does not have session give it him
   *
   * @return generate session and set cookie
   * @param response
   */
  default String getSessionAndSetCookieInResponse(HttpServletResponse response) {
    String anonymousSession = SecureUtils.getRandomString(SESSION_LENGTH);
    Cookie cookie = new Cookie(ANONYMOUS_SESSION_HEADER, anonymousSession);
    cookie.setMaxAge(COOKIE_AGE);
    cookie.setHttpOnly(true);
    response.addCookie(cookie);
    System.out.println("Set-Cookie " + ANONYMOUS_SESSION_HEADER + ": " + anonymousSession);
    return anonymousSession;
  }

  default AuthUser getInternalUserRole(String accessToken, String userSession) {
    if (StringUtils.isBlank(accessToken) || StringUtils.isBlank(userSession)) {
      return new AuthUser(userSession).setRole(EnumSecureRole.ANONYMOUS);
    }
    return new AuthUser(accessToken, userSession).setRole(EnumSecureRole.INTERNAL);
  }

  default String getCookieValue(HttpServletRequest req, String cookieName) {
    return Arrays.stream(req.getCookies())
        .filter(c -> c.getName().equals(cookieName))
        .findFirst()
        .map(Cookie::getValue)
        .orElse(null);
  }
}
