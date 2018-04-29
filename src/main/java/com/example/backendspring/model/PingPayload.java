package com.example.backendspring.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Aleksey Popryadukhin on 29/04/2018.
 */
@JsonTypeName("PingPayload")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PingPayload implements Payload {

  private String ping;
}
