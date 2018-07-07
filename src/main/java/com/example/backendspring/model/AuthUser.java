package com.example.backendspring.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Aleksey Popryadukhin on 16/04/2018.
 */
@JsonTypeName("AuthUser")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthUser implements Payload {

  private String userId;
  private String email;
  private String accessToken;
  private String userSession;
  private String salt;
  private int cost;
  private int misc;
  private Set<EnumAuthority> authorities = new HashSet<>();

  public AuthUser(String userSession) {
    this.userSession = userSession;
  }

  public AuthUser(String accessToken, String userSession) {
    this.accessToken = accessToken;
    this.userSession = userSession;
  }

  public AuthUser(Set<EnumAuthority> authorities) {
    this.authorities = new HashSet<>(authorities);
  }

  public AuthUser(String userId, String email, String accessToken, String userSession, Set<EnumAuthority> authorities) {
    this.userId = userId;
    this.email = email;
    this.accessToken = accessToken;
    this.userSession = userSession;
    this.authorities = authorities;
  }

  public AuthUser(String salt, int cost, int misc) {
    this.salt = salt;
    this.cost = cost;
    this.misc = misc;
  }

  public static AuthUser anonymous() {
    return new AuthUser(Collections.singleton(EnumAuthority.ANONYMOUS));
  }

  public static AuthUser simpleUser(String userId, String username, String accessToken, String userSession,
                                    Set<EnumAuthority> authorities) {
    return new AuthUser(userId, username, accessToken, userSession, authorities);
  }

  public static AuthUser authRequest(String salt, int cost, int misc) {
    return new AuthUser(salt, cost, misc);
  }

  public AuthUser setAuthority(EnumAuthority authority) {
    this.authorities = Collections.singleton(authority);
    return this;
  }

  public void setAuthorities(Set<EnumAuthority> authorities) {
    this.authorities = authorities;
  }
}
