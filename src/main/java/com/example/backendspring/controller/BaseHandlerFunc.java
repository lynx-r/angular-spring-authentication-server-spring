package com.example.backendspring.controller;


import com.example.backendspring.config.ErrorMessages;
import com.example.backendspring.config.IPath;
import com.example.backendspring.model.Answer;
import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.EnumSecureRole;
import com.example.backendspring.model.Payload;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.example.backendspring.config.RequestConstants.USER_ROLE_HEADER;
import static com.example.backendspring.service.SecureUtils.getSessionAndSetCookieInResponse;
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
        return getForbiddenAnswer(response);
      }
      return processed;
    } else {
      return getInsecureAnswer(data);
    }
  }

  default Answer getForbiddenAnswer(HttpServletResponse response) {
    String anonymousSession = getSessionAndSetCookieInResponse(response);
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
}
