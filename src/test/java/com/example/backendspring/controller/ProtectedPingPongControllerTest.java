package com.example.backendspring.controller;

import com.example.backendspring.JsonUtil;
import com.example.backendspring.config.RequestConstants;
import com.example.backendspring.model.*;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static com.example.backendspring.controller.AuthControllerTest.SECURITY_REST_URL;
import static com.example.backendspring.service.Utils.getRandomString20;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Aleksey Popryadukhin on 01/05/2018.
 */
public class ProtectedPingPongControllerTest extends WebTest {

  private final static String DEFENDED_REST_URL = "/defended";

  @Test
  public void ping() throws Exception {
    UserCredentials userCredentials = new UserCredentials(getRandomString20(), getRandomString20());
    MvcResult registerResponse = mockMvc
        .perform(
            post(SECURITY_REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    String answerAsString = registerResponse.getResponse().getContentAsString();
    Answer answer = JsonUtil.readValue(answerAsString, Answer.class);
    AuthUser authUser = answer.getAuthUser();
    PingPayload ping = new PingPayload(getRandomString20());
    mockMvc
        .perform(
            post(DEFENDED_REST_URL + "/ping")
                .contentType(MediaType.APPLICATION_JSON)
                .header(RequestConstants.ACCESS_TOKEN_HEADER, authUser.getAccessToken())
                .header(RequestConstants.USER_SESSION_HEADER, authUser.getUserSession())
                .content(JsonUtil.writeValue(ping))
        )
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void ping_twice() throws Exception {
    UserCredentials userCredentials = new UserCredentials(getRandomString20(), getRandomString20());
    MvcResult registerResponse = mockMvc
        .perform(
            post(SECURITY_REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    String answerAsString = registerResponse.getResponse().getContentAsString();
    Answer answer = JsonUtil.readValue(answerAsString, Answer.class);
    AuthUser authUser = answer.getAuthUser();
    PingPayload ping = new PingPayload(getRandomString20());
    MvcResult pingResponse = mockMvc
        .perform(
            post(DEFENDED_REST_URL + "/ping")
                .contentType(MediaType.APPLICATION_JSON)
                .header(RequestConstants.ACCESS_TOKEN_HEADER, authUser.getAccessToken())
                .header(RequestConstants.USER_SESSION_HEADER, authUser.getUserSession())
                .content(JsonUtil.writeValue(ping))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    answerAsString = pingResponse.getResponse().getContentAsString();
    answer = JsonUtil.readValue(answerAsString, Answer.class);
    authUser = answer.getAuthUser();
    ping = new PingPayload(getRandomString20());
    MvcResult secondPingResponse = mockMvc
        .perform(
            post(DEFENDED_REST_URL + "/ping")
                .contentType(MediaType.APPLICATION_JSON)
                .header(RequestConstants.ACCESS_TOKEN_HEADER, authUser.getAccessToken())
                .header(RequestConstants.USER_SESSION_HEADER, authUser.getUserSession())
                .content(JsonUtil.writeValue(ping))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    Answer answerPingSecond = JsonUtil.readValue(secondPingResponse.getResponse().getContentAsString(), Answer.class);
    assertEquals(getPong(ping, answerPingSecond), ((PongPayload) answerPingSecond.getBody()).getPong());
  }

  @Test
  public void ping_unregistered() throws Exception {
    PingPayload ping = new PingPayload(getRandomString20());
    mockMvc
        .perform(
            post(DEFENDED_REST_URL + "/ping")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(ping))
        )
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  @Test
  public void ping_registered_but_logout() throws Exception {
    UserCredentials userCredentials = new UserCredentials(getRandomString20(), getRandomString20());
    MvcResult registerResponse = mockMvc
        .perform(
            post(SECURITY_REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    String answerAsString = registerResponse.getResponse().getContentAsString();
    Answer answerAuthorize = JsonUtil.readValue(answerAsString, Answer.class);
    AuthUser authUser = answerAuthorize.getAuthUser();
    mockMvc
        .perform(
            get(SECURITY_REST_URL + "/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(RequestConstants.ACCESS_TOKEN_HEADER, authUser.getAccessToken())
                .header(RequestConstants.USER_SESSION_HEADER, authUser.getUserSession())
        )
        .andDo(print())
        .andExpect(status().isOk());

    answerAsString = registerResponse.getResponse().getContentAsString();
    Answer answer = JsonUtil.readValue(answerAsString, Answer.class);
    authUser = answer.getAuthUser();
    PingPayload ping = new PingPayload(getRandomString20());
    mockMvc
        .perform(
            post(DEFENDED_REST_URL + "/ping")
                .contentType(MediaType.APPLICATION_JSON)
                .header(RequestConstants.ACCESS_TOKEN_HEADER, authUser.getAccessToken())
                .header(RequestConstants.USER_SESSION_HEADER, authUser.getUserSession())
                .content(JsonUtil.writeValue(ping))
        )
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  private String getPong(PingPayload ping, Answer answerPing) {
    String pong = ((PongPayload) answerPing.getBody()).getPong();
    String substring = pong.substring(pong.indexOf(" "));
    return ping.getPing() + substring;
  }
}