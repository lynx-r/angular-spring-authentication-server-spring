package com.example.backendspring.config;


import com.example.backendspring.model.EnumSecureRole;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum SecuredPath implements IPath {
  PING("ping", true, Constants.SECURE_PING_PONG_ROLES);

  private String path;
  private boolean secure;
  private Set<EnumSecureRole> roles;

  SecuredPath(String path, boolean secure, Set<EnumSecureRole> roles) {
    this.path = path;
    this.secure = secure;
    this.roles = roles;
  }

  @Override
  public String getPath() {
    return path;
  }

  public SecuredPath setPath(String path) {
    this.path = path;
    return this;
  }

  @Override
  public boolean isSecure() {
    return secure;
  }

  public SecuredPath setSecure(boolean secure) {
    this.secure = secure;
    return this;
  }

  @Override
  public Set<EnumSecureRole> getRoles() {
    return roles;
  }

  public SecuredPath setRoles(Set<EnumSecureRole> roles) {
    this.roles = roles;
    return this;
  }

  public static class Constants {
    public static final HashSet<EnumSecureRole> SECURE_PING_PONG_ROLES = new HashSet<>(Collections.singletonList(EnumSecureRole.USER));
  }
}
