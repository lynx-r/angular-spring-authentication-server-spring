package com.example.backendspring.function;


import com.example.backendspring.model.Answer;
import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.Payload;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Aleksey Popryaduhin on 10:52 29/09/2017.
 */
@FunctionalInterface
public interface TrustedHandlerFunc<T extends Payload> extends BaseHandlerFunc<T> {

  default Answer handleRequest(HttpServletResponse response, T data, AuthUser token) {
    Answer answer = getAnswer(response, data);
    if (token != null) {
      answer.setAuthUser(token);
    } else {
      answer.setAuthUser((AuthUser) answer.getBody());
    }
    response.setStatus(answer.getStatusCode());
    return answer;
  }

  default Answer handleProtectedRequest(HttpServletResponse response, T data) {
    return handleRequest(response, data, null);
  }
}
