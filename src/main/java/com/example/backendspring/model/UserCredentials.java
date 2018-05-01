package com.example.backendspring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Aleksey Popryadukhin on 16/04/2018.
 */
@JsonTypeName("UserCredentials")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserCredentials implements Payload, DeepClone {

  @NotNull(message = "Не допустимо пустое значение имени пользователя")
  @Size(min = 3, message = "Минимальная длина имени пользователя 3 символа")
  private String username;
  @NotNull(message = "Не допустимо пустое значение пароля")
  @Size(min = 3, message = "Минимальная длина пароля 3 символов")
  private String password;

  @JsonIgnore
  public String getCredentials() {
    return username + ":" + password;
  }
}
