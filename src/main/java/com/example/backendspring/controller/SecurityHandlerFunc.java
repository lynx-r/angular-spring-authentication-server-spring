package com.example.backendspring.controller;

import com.example.backendspring.config.IPath;
import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.EnumSecureRole;
import com.example.backendspring.model.Payload;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static com.example.backendspring.config.RequestConstants.*;
import static com.example.backendspring.service.SecureUtils.getSessionAndSetCookieInResponse;

/**
 * Created by Aleksey Popryaduhin on 16:37 01/10/2017.
 */
@FunctionalInterface
public interface SecurityHandlerFunc<T extends Payload> {

  Optional<AuthUser> isAuthenticated(AuthUser authUser);

  default Optional<AuthUser> getAuthUser(HttpServletRequest request, HttpServletResponse response, IPath path) {
    String userSession = getOrCreateSession(request, response);
    String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);

    AuthUser internalUserRole = getInternalUserRole(accessToken, userSession);
    if (path.isSecure()) {
      Optional<AuthUser> authenticated = isAuthenticated(internalUserRole);
      if (!authenticated.isPresent()) {
        return getForbidden(response);
      }
      AuthUser securedUser = authenticated.get();
      if (!hasRights(securedUser.getRoles(), path)) {
        return getForbidden(response);
      }
      Optional<AuthUser> securedAnswer = getSecureAnswer(securedUser);
      if (securedAnswer == null) {
        return getForbidden(response);
      }
      return securedAnswer;
    }
    return Optional.of(AuthUser.anonymous());
  }

  default boolean hasRights(Set<EnumSecureRole> roles, IPath path) {
    if (roles.contains(EnumSecureRole.BAN)) {
      return false;
    }
    return path.getRoles().isEmpty() || path.getRoles().containsAll(roles);
  }

  default Optional<AuthUser> getSecureAnswer(AuthUser authenticated) {
    return isAuthenticated(authenticated);
  }

  default Optional<AuthUser> getForbidden(HttpServletResponse response) {
    String anonymousSession = getSessionAndSetCookieInResponse(response);
    return Optional.of(new AuthUser(anonymousSession)
        .setRole(EnumSecureRole.ANONYMOUS));
  }

  default String getOrCreateSession(HttpServletRequest request, HttpServletResponse response) {
    // take user session which user got after login
    String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);
    if (StringUtils.isBlank(accessToken)) {
      // if anonymous user
      String anonymousSession = getCookieValue(request, ANONYMOUS_SESSION_HEADER);
      if (StringUtils.isBlank(anonymousSession)) {
        anonymousSession = getSessionAndSetCookieInResponse(response);
      }
      // return anonym session
      return anonymousSession;
    }
    // return transfer session
    return request.getHeader(USER_SESSION_HEADER);
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
