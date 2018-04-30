package com.example.backendspring.controller;


import com.example.backendspring.config.IAuthority;
import com.example.backendspring.model.Answer;
import com.example.backendspring.model.Payload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Aleksey Popryaduhin on 10:52 29/09/2017.
 */
@FunctionalInterface
public interface ModelHandlerFunc<T extends Payload> extends BaseHandlerFunc<T> {

  default Answer handleRequest(HttpServletRequest request, HttpServletResponse response,
                               IAuthority path, T body) {
    Answer answer = getAnswer(request, response, path, body);
    response.setStatus(answer.getStatusCode());
    return answer;
  }

}
