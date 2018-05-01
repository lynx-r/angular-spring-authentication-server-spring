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
@JsonTypeName("Usercredentials")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserCredentials implements Payload, DeepClone {

  @NotNull(message = "Не допустимо пустое значение")
  @Size(min = 4, message = "Минимальная длина 4 символа")
  private String username;
  @NotNull(message = "Не допустимо пустое значение")
  @Size(min = 8, message = "Минимальная длина 4 символа")
  private String password;

  @JsonIgnore
  public String getCredentials() {
    return username + ":" + password;
  }
}
