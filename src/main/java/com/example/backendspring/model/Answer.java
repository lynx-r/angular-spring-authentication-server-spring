package com.example.backendspring.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.net.HttpURLConnection.*;

@NoArgsConstructor
@Data
public class Answer {

  private int statusCode;
  private Payload body;
  private AuthUser authUser;
  private MessageResponse message;

  @JsonCreator
  private Answer(@JsonProperty("body") Payload body, @JsonProperty("message") MessageResponse message) {
    this.body = body;
    this.message = message;
  }

  public static Answer ok(Payload body) {
    return new Answer(body, MessageResponse.ok())
        .statusCode(HTTP_OK);
  }

  public static Answer created(Payload body) {
    return new Answer(body, MessageResponse.created())
        .statusCode(HTTP_CREATED);
  }

  public static Answer error(int statusCode, String message) {
    return new Answer(null, MessageResponse.error(statusCode, message))
        .statusCode(statusCode);
  }

  public static Answer empty() {
    return new Answer(null, null)
        .statusCode(HTTP_OK);
  }

  public Answer statusCode(int statusCode) {
    setStatusCode(statusCode);
    return this;
  }

  public Answer message(int code, String message) {
    setMessage(new MessageResponse(code, message));
    return this;
  }

  public static Answer forbidden() {
    return Answer.error(HTTP_FORBIDDEN, "Access denied");
  }
}