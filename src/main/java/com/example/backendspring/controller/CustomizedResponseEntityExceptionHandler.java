package com.example.backendspring.controller;

import com.example.backendspring.exception.AuthException;
import com.example.backendspring.model.MessageResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static java.net.HttpURLConnection.*;

/**
 * Код взят отсюда http://www.springboottutorial.com/spring-boot-validation-for-rest-services
 */
@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
    MessageResponse errorDetails = new MessageResponse(HTTP_INTERNAL_ERROR, request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(AuthException.class)
  public final ResponseEntity<MessageResponse> handleUserNotFoundException(AuthException ex, WebRequest request) {
    MessageResponse errorDetails = new MessageResponse(ex.getStatus(), ex.getMessage());
    return new ResponseEntity<>(errorDetails, HttpStatus.resolve(ex.getStatus()));
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                HttpHeaders headers, HttpStatus status, WebRequest request) {
    MessageResponse errorDetails = new MessageResponse(HTTP_BAD_REQUEST, ex.getBindingResult().toString());
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }
}
