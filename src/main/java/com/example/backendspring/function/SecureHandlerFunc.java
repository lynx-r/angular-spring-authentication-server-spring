package com.example.backendspring.function;

import com.example.backendspring.config.IAuthority;
import com.example.backendspring.exception.AuthException;
import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.EnumAuthority;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;

import static com.example.backendspring.config.RequestConstants.*;
import static com.example.backendspring.service.Utils.getCookieValue;

/**
 * Created by Aleksey Popryaduhin on 16:37 01/10/2017.
 */
@FunctionalInterface
public interface SecureHandlerFunc {

  Optional<AuthUser> isAuthenticated(AuthUser authUser);

  default Optional<AuthUser> getAuthUser(HttpServletRequest request, IAuthority authority) {
    AuthUser clientAuthUser = getClientAuthUser(request);
    return
        Optional.of(
            isAuthenticated(clientAuthUser)
                .map(authUser -> {
                  if (!hasRights(authUser.getAuthorities(), authority.getAuthorities())) {
                    return null;
                  }
                  return authUser;
                })
                .orElseThrow(AuthException::forbidden)
        );
  }

  default boolean hasRights(Set<EnumAuthority> clientAuthorities, Set<EnumAuthority> allowedAuthorities) {
    return allowedAuthorities.isEmpty() || EnumAuthority.hasAuthority(clientAuthorities, allowedAuthorities);
  }

  default AuthUser getClientAuthUser(HttpServletRequest request) {
    String userSession = getOrCreateSession(request);
    String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);

    if (StringUtils.isBlank(accessToken) || StringUtils.isBlank(userSession)) {
      return new AuthUser(userSession).setAuthority(EnumAuthority.ANONYMOUS);
    }
    return new AuthUser(accessToken, userSession);
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
}
