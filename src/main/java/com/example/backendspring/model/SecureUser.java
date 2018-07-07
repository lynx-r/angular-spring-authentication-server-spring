package com.example.backendspring.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Aleksey Popryadukhin on 16/04/2018.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SecureUser extends BaseDomain {
  private String id;

  private String email;

  private Set<EnumAuthority> authorities = new HashSet<>();

  /**
   * hash of user:passwordHash:salt
   */
  private String sigma;

  private int cost;

  private int misc;

  /**
   * random string
   */
  private String salt;

  /**
   * key for encryption
   */
  private String key;

  /**
   * key for encryption
   */
  private String passwordHash;

  /**
   * init vector for encryption
   */
  private String initVector;

  /**
   * Random token length
   */
  private int tokenLength;

  /**
   * unecrypted user token
   */
  private String secureToken;

  /**
   * encrypted user token goes through wires
   */
  private String accessToken;

  /**
   * user userSession acquire from Spark
   */
  private String userSession;

  public void addAuthority(EnumAuthority authority) {
    authorities.add(authority);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", id)
        .append("email", email)
        .append("authorities", authorities)
        .append("accessToken", accessToken)
        .append("userSession", userSession)
        .toString();
  }
}
