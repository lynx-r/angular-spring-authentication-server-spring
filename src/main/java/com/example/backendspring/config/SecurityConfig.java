package com.example.backendspring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final AppProperties appProperties;

  @Autowired
  public SecurityConfig(AppProperties appProperties) {
    this.appProperties = appProperties;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .addFilterBefore(corsFilter(), SessionManagementFilter.class) //adds your custom CorsFilter
        .authorizeRequests()
        .antMatchers("/**")
        .permitAll()
        .and()
        .csrf()
        .disable();
  }

  private CorsFilter corsFilter() {
    return new CorsFilterAdapter(
        appProperties.getOriginUrl(),
        appProperties.getHeaders(),
        appProperties.getMethods())
        .corsFilter();
  }
}