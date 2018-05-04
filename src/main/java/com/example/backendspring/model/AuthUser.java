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
  private Set<EnumAuthority> authorities = new HashSet<>();

  public AuthUser(String userSession) {
    this.userSession = userSession;
  }

  public AuthUser(String accessToken, String userSession) {
    this.accessToken = accessToken;
    this.userSession = userSession;
  }

  public static AuthUser anonymous() {
    return new AuthUser(null, null, null, null,
        Collections.singleton(EnumAuthority.ANONYMOUS));
  }

  public static AuthUser simpleUser(String userId, String username, String accessToken, String userSession,
                                    Set<EnumAuthority> authorities) {
    return new AuthUser(userId, username, accessToken, userSession, authorities);
  }

  public AuthUser setAuthority(EnumAuthority authority) {
    this.authorities = Collections.singleton(authority);
    return this;
  }

  public void setAuthorities(Set<EnumAuthority> authorities) {
    this.authorities = authorities;
  }
}
