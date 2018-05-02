package com.example.backendspring.controller;

import com.example.backendspring.exception.AuthException;
import com.example.backendspring.model.Answer;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

/**
 * Код взят отсюда http://www.springboottutorial.com/spring-boot-validation-for-rest-services
 */
@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  public final ResponseEntity<Object> handleAllExceptions(Exception ex) {
    return new ResponseEntity<>(Answer.error(HTTP_INTERNAL_ERROR, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(AuthException.class)
  public final ResponseEntity<Answer> handleUserNotFoundException(AuthException ex) {
    return new ResponseEntity<>(Answer.error(ex.getStatus(), ex.getMessage()), HttpStatus.resolve(ex.getStatus()));
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                HttpHeaders headers, HttpStatus status,
                                                                WebRequest request) {
    String errors = ex.getBindingResult().getAllErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining(";"));
    return new ResponseEntity<>(Answer.error(HTTP_BAD_REQUEST, errors), HttpStatus.BAD_REQUEST);
  }
}
