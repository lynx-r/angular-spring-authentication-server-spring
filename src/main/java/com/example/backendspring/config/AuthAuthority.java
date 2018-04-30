package com.example.backendspring.config;

import com.example.backendspring.model.EnumAuthority;

import java.util.HashSet;
import java.util.Set;

public enum AuthAuthority implements IAuthority {
  REGISTER,
  AUTHORIZE,
  AUTHENTICATE,
  LOGOUT;

  private Set<EnumAuthority> authorities;

  AuthAuthority() {
    this.authorities = new HashSet<>();
  }

  @Override
  public Set<EnumAuthority> getAuthorities() {
    return authorities;
  }
}
