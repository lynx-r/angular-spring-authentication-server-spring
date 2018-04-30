package com.example.backendspring.controller;

import com.example.backendspring.config.IPath;
import com.example.backendspring.exception.AuthException;
import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.EnumSecureRole;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;

import static com.example.backendspring.config.RequestConstants.*;
import static com.example.backendspring.service.SecureUtils.getCookieValue;

/**
 * Created by Aleksey Popryaduhin on 16:37 01/10/2017.
 */
@FunctionalInterface
public interface SecurityHandlerFunc {

  Optional<AuthUser> isAuthenticated(AuthUser authUser);

  default Optional<AuthUser> getAuthUser(HttpServletRequest request, IPath path) {
    String userSession = getOrCreateSession(request);
    String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);

    AuthUser clientAuthUser = getClientAuthUser(accessToken, userSession);
    if (path.isSecure()) {
      return isAuthenticated(clientAuthUser)
          .map(authUser -> {
            if (!hasRights(authUser.getRoles(), path)) {
              throw new AuthException();
            }
            return authUser;
          });
    }
    return Optional.of(AuthUser.anonymous());
  }

  default boolean hasRights(Set<EnumSecureRole> roles, IPath path) {
    return path.getRoles().isEmpty() || path.getRoles().containsAll(roles);
  }

  default String getOrCreateSession(HttpServletRequest request) {
    // take user session which user got after login
    String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);
    if (StringUtils.isBlank(accessToken)) {
      // return anonym session
      return getCookieValue(request, ANONYMOUS_SESSION_HEADER);
    }
    // return transfer session
    return request.getHeader(USER_SESSION_HEADER);
  }


  default AuthUser getClientAuthUser(String accessToken, String userSession) {
    if (StringUtils.isBlank(accessToken) || StringUtils.isBlank(userSession)) {
      return new AuthUser(userSession).setRole(EnumSecureRole.ANONYMOUS);
    }
    return new AuthUser(accessToken, userSession);
  }
}
