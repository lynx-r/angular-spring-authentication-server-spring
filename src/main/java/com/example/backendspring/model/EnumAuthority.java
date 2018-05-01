package com.example.backendspring.model;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Aleksey Popryadukhin on 17/04/2018.
 */
public enum EnumAuthority {
  USER,
  BAN,
  ANONYMOUS;

  public static boolean hasAuthority(Set<EnumAuthority> clientAuthorities, Set<EnumAuthority> allowedAuthorities) {
    // находим пересечение множеств доступов, так чтобы разрешенные доступы содержали
    // в себе все клиентские
    Set<EnumAuthority> intersection = new HashSet<>(allowedAuthorities);
    intersection.retainAll(clientAuthorities);
    return intersection.containsAll(clientAuthorities);
  }

  public static Set<EnumAuthority> parseAuthorities(String authorities) {
    if (StringUtils.isBlank(authorities)) {
      return new HashSet<>();
    }
    String[] rolesArr = authorities.toUpperCase().split(",");
    Set<EnumAuthority> roleSet = new HashSet<>();
    for (String role : rolesArr) {
      roleSet.add(EnumAuthority.valueOf(role));
    }
    return roleSet;
  }
}
