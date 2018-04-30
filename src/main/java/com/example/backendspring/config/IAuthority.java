package com.example.backendspring.config;

import com.example.backendspring.model.EnumAuthority;

import java.util.Set;

/**
 * Created by Aleksey Popryadukhin on 24/04/2018.
 */
public interface IAuthority {

  Set<EnumAuthority> getAuthorities();
}
