package com.example.backendspring.function;

import com.example.backendspring.config.IAuthority;
import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.EnumAuthority;
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
public interface SecureHandlerFunc {

  Optional<AuthUser> isAuthenticated(AuthUser authUser);

  default Optional<AuthUser> getAuthUser(HttpServletRequest request, IAuthority authority) {
    AuthUser clientAuthUser = getClientAuthUser(request);
    return
        isAuthenticated(clientAuthUser)
            .map(authUser -> {
              if (!hasRights(authUser.getAuthorities(), authority)) {
                return null;
              }
              return authUser;
            });
  }

  default boolean hasRights(Set<EnumAuthority> authorities, IAuthority authority) {
    return authority.getAuthorities().isEmpty() || EnumAuthority.hasAuthority(authorities, authority.getAuthorities());
  }

  default AuthUser getClientAuthUser(HttpServletRequest request) {
    String userSession = getOrCreateSession(request);
    String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);
    String authorities = request.getHeader(USER_AUTHORITIES_HEADER);

    if (StringUtils.isBlank(accessToken) || StringUtils.isBlank(userSession)) {
      return new AuthUser(userSession).setAuthority(EnumAuthority.ANONYMOUS);
    }
    return new AuthUser(accessToken, userSession)
        .setAuthorities(EnumAuthority.parseAuthorities(authorities));
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
