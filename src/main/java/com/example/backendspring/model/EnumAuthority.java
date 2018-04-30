package com.example.backendspring.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Aleksey Popryadukhin on 17/04/2018.
 */
public enum EnumAuthority {
  USER,
  ANONYMOUS;

  public static boolean isSecure(EnumAuthority role) {
    switch (role) {
      case USER:
        return true;
      default:
        return false;
    }
  }

  public static boolean isSecure(Set<EnumAuthority> authorities) {
    return authorities.stream().anyMatch(EnumAuthority::isSecure);
  }

  public static Set<EnumAuthority> parseAuthorities(String authorities) {
    String[] rolesArr = authorities.toUpperCase().split(",");
    Set<EnumAuthority> roleSet = new HashSet<>();
    for (String role : rolesArr) {
      roleSet.add(EnumAuthority.valueOf(role));
    }
    return roleSet;
  }
}
