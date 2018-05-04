package com.example.backendspring.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Aleksey Popryadukhin on 17/04/2018.
 */
public enum EnumAuthority {
  USER,
  ANONYMOUS;

  public static boolean hasAuthorities(Set<EnumAuthority> clientAuthorities, Set<EnumAuthority> allowedAuthorities) {
    // находим пересечение множеств доступов, так чтобы разрешенные доступы содержали
    // в себе все клиентские
    Set<EnumAuthority> intersection = new HashSet<>(allowedAuthorities);
    intersection.retainAll(clientAuthorities);
    return intersection.containsAll(clientAuthorities);
  }
}
