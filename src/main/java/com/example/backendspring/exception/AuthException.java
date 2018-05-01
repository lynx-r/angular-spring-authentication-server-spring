package com.example.backendspring.exception;

import com.example.backendspring.config.ErrorMessages;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

/**
 * Created by Aleksey Popryadukhin on 29/04/2018.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
@Getter
public class AuthException extends RuntimeException {

  private final int status;

  public AuthException(int status, String message) {
    super(message);
    this.status = status;
  }

  public static AuthException forbidden() {
    return new AuthException(HTTP_FORBIDDEN, ErrorMessages.getString(ErrorMessages.ACCESS_DENIED));
  }
}
