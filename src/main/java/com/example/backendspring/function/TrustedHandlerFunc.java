package com.example.backendspring.function;


import com.example.backendspring.model.Answer;
import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.Payload;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Aleksey Popryaduhin on 10:52 29/09/2017.
 */
@FunctionalInterface
public interface TrustedHandlerFunc<T extends Payload> {

  Answer process(T data);

  default Answer handleRequest(HttpServletResponse response, T data, AuthUser token) {
    Answer answer = process(data);
    // Подписываем ответ токеном. Токен должен быть действительным
    answer.setAuthUser(token);
    response.setStatus(answer.getStatusCode());
    return answer;
  }

  default Answer handleAuthRequest(HttpServletResponse response, T data) {
    Answer answer = process(data);
    // Ответ является токеном
    answer.setAuthUser((AuthUser) answer.getBody());
    response.setStatus(answer.getStatusCode());
    return answer;
  }
}
