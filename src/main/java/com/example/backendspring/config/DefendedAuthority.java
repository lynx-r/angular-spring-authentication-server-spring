package com.example.backendspring.config;


import com.example.backendspring.model.EnumAuthority;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum DefendedAuthority implements IAuthority {
  PING(Constants.SECURE_PING_PONG_ROLES);

  private Set<EnumAuthority> authorities;

  DefendedAuthority(Set<EnumAuthority> authorities) {
    this.authorities = authorities;
  }

  @Override
  public Set<EnumAuthority> getAuthorities() {
    return authorities;
  }

  public DefendedAuthority setAuthorities(Set<EnumAuthority> authorities) {
    this.authorities = authorities;
    return this;
  }

  public static class Constants {
    public static final HashSet<EnumAuthority> SECURE_PING_PONG_ROLES = new HashSet<>(Collections.singletonList(EnumAuthority.USER));
  }
}
