package com.example.backendspring.config;

/**
 * Created by Aleksey Popryaduhin on 16:49 01/10/2017.
 */
public class RequestConstants {
  public static final String LIMIT = "limit";
  public static final String ID = ":id";
  public static final String SIGN = "sign";
  public static final String SIGN_REQUEST = "sign-request";
  public static final int SESSION_LENGTH = 20;
  public static final int COOKIE_AGE = 31 * 24 * 60 * 60;
  public static final String ANONYMOUS_SESSION_HEADER = "anonymous-session";
  public static final String USER_SESSION_HEADER = "user-session";
  public static final String USER_ROLE_HEADER = "user-roles";
  public static final String FILTER_EXPRESSION_HEADER = "filter-expression";
  public static final String FILTER_VALUES_HEADER = "filter-values";
  public static final String AUTH_COUNTER_HEADER = "auth-counter";
  public static final String ACCESS_TOKEN_HEADER = "access-token";
}