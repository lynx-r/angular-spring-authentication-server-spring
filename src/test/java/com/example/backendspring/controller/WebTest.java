package com.example.backendspring.controller;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * User: gkislin
 * Date: 10.08.2014
 */
//@ContextConfiguration({
//    "classpath:spring/spring-app.xml",
//    "classpath:spring/spring-mvc.xml",
//    "classpath:spring/spring-db.xml"
//})
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
abstract public class WebTest {

  protected MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @PostConstruct
  void postConstruct() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }
}