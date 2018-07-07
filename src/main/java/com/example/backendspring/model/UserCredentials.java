package com.example.backendspring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
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

  @Email(message = "Не допустимый e-mail")
  private String email;

  @NotNull(message = "Не допустимо пустое значение пароля")
  @Size(min = 64, max = 64, message = "Минимальная длина пароля 64 символов")
  private String passwordHash;

  @JsonIgnore
  public String getCredentials() {
    return email + ":" + passwordHash;
  }
}
