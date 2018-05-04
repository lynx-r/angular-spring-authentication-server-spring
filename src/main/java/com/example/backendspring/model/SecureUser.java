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

  private String username;

  private Set<EnumAuthority> authorities = new HashSet<>();

  /**
   * hash of user:password:salt
   */
  private String digest;

  /**
   * random string
   */
  private String salt;

  /**
   * key for encryption
   */
  private String key;

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
        .append("username", username)
        .append("authorities", authorities)
        .append("accessToken", accessToken)
        .append("userSession", userSession)
        .toString();
  }
}
