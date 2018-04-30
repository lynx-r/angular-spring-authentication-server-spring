package com.example.backendspring.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * Created by Aleksey Popryadukhin on 16/04/2018.
 */
@JsonTypeName("AuthUser")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthUser implements Payload {

  private String userId;
  private String username;
  private String accessToken;
  private String userSession;
  private Set<EnumSecureRole> roles = new HashSet<>();

  public AuthUser(String userSession) {
    this.userSession = userSession;
  }

  public AuthUser(String accessToken, String userSession) {
    this.accessToken = accessToken;
    this.userSession = userSession;
  }

  public static AuthUser anonymous() {
    return new AuthUser(null, null, null, null,
        Collections.singleton(EnumSecureRole.ANONYMOUS));
  }

  public static AuthUser simpleUser(String userId, String username, String accessToken, String userSession,
                                    Set<EnumSecureRole> roles) {
    return new AuthUser(userId, username, accessToken, userSession, roles);
  }

  public AuthUser setRole(EnumSecureRole role) {
    this.roles = Collections.singleton(role);
    return this;
  }

  public AuthUser addRole(EnumSecureRole role) {
    this.roles.add(role);
    return this;
  }
}
