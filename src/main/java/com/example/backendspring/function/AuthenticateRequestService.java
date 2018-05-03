package com.example.backendspring.function;

import com.example.backendspring.config.IAuthority;
import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.EnumAuthority;
import com.example.backendspring.service.SecureUserService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;

import static com.example.backendspring.config.RequestConstants.ACCESS_TOKEN_HEADER;
import static com.example.backendspring.config.RequestConstants.USER_SESSION_HEADER;

/**
 * Created by Aleksey Popryaduhin on 16:37 01/10/2017.
 */
@Service
public class AuthenticateRequestService {

  private SecureUserService secureUserService;

  public AuthenticateRequestService(SecureUserService secureUserService) {
    this.secureUserService = secureUserService;
  }

  public Optional<AuthUser> getAuthenticatedUser(HttpServletRequest request, IAuthority authority) {
    AuthUser clientAuthUser = getAuthUserFromRequest(request);
    return secureUserService
        .authenticate(clientAuthUser)
        .map(authUser -> {
          if (!hasAuthorities(authUser.getAuthorities(), authority.getAuthorities())) {
            return null;
          }
          return authUser;
        });
  }

  private boolean hasAuthorities(Set<EnumAuthority> clientAuthorities, Set<EnumAuthority> allowedAuthorities) {
    return allowedAuthorities.isEmpty() || EnumAuthority.hasAuthorities(clientAuthorities, allowedAuthorities);
  }

  private AuthUser getAuthUserFromRequest(HttpServletRequest request) {
    String userSession = request.getHeader(USER_SESSION_HEADER);
    String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);
    return new AuthUser(accessToken, userSession);
  }
}
