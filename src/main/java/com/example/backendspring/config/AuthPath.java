package com.example.backendspring.config;

import com.example.backendspring.model.EnumSecureRole;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum AuthPath implements IPath {
  REGISTER("/register", false, new HashSet<>()),
  AUTHORIZE("/authorize", false, new HashSet<>()),
  AUTHENTICATE("/authenticate", false, new HashSet<>()),
  LOGOUT("/logout", false, new HashSet<>());

  private String path;
  private boolean secure;
  private Set<EnumSecureRole> roles;

  AuthPath(String path, boolean secure, Set<EnumSecureRole> roles) {
    this.path = path;
    this.secure = secure;
    this.roles = roles;
  }

  @Override
  public String getPath() {
    return path;
  }

  public AuthPath setPath(String path) {
    this.path = path;
    return this;
  }

  @Override
  public boolean isSecure() {
    return secure;
  }

  public AuthPath setSecure(boolean secure) {
    this.secure = secure;
    return this;
  }

  @Override
  public Set<EnumSecureRole> getRoles() {
    return roles;
  }

  public AuthPath setRoles(Set<EnumSecureRole> roles) {
    this.roles = roles;
    return this;
  }

  public static class Constants {
    public static final HashSet<EnumSecureRole> SECURE_USER_ROLES = new HashSet<>(Collections.singletonList(EnumSecureRole.USER));
  }
}
