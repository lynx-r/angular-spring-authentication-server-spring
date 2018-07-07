package com.example.backendspring.exception;

import com.example.backendspring.config.ErrorMessages;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

/**
 * Created by Aleksey Popryadukhin on 29/04/2018.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
@Getter
public class RequestException extends RuntimeException {

  private final int status;

  public RequestException(int status, String message) {
    super(message);
    this.status = status;
  }

  public static RequestException badRequest() {
    return new RequestException(HTTP_BAD_REQUEST, ErrorMessages.getString(ErrorMessages.BAD_REQUEST));
  }

  public static RequestException badRequest(String message) {
    return new RequestException(HTTP_BAD_REQUEST, message);
  }

  public static RequestException forbidden() {
    return new RequestException(HTTP_FORBIDDEN, ErrorMessages.getString(ErrorMessages.ACCESS_DENIED));
  }
}
