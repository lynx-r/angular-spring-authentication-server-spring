package com.example.backendspring.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Aleksey Popryadukhin on 17/04/2018.
 */
public enum EnumSecureRole {
  USER,
  ANONYMOUS;

  public static boolean isSecure(EnumSecureRole role) {
    switch (role) {
      case USER:
        return true;
      default:
        return false;
    }
  }

  public static boolean isSecure(Set<EnumSecureRole> roles) {
    return roles.stream().anyMatch(EnumSecureRole::isSecure);
  }

  public static Set<EnumSecureRole> parseRoles(String roles) {
    String[] rolesArr = roles.toUpperCase().split(",");
    Set<EnumSecureRole> roleSet = new HashSet<>();
    for (String role : rolesArr) {
      roleSet.add(EnumSecureRole.valueOf(role));
    }
    return roleSet;
  }
}
