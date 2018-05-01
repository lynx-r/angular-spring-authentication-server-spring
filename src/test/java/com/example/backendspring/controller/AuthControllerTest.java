package com.example.backendspring.controller;

import com.example.backendspring.JsonUtil;
import com.example.backendspring.config.RequestConstants;
import com.example.backendspring.model.Answer;
import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.UserCredentials;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static com.example.backendspring.service.Utils.getRandomString20;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Aleksey Popryadukhin on 01/05/2018.
 */
public class AuthControllerTest extends WebTest {

  public final static String SECURITY_REST_URL = "/security";

  @Test
  public void empty_credentials_register() throws Exception {
    UserCredentials userCredentials = new UserCredentials();
    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  public void register() throws Exception {
    UserCredentials userCredentials = new UserCredentials(getRandomString20(), getRandomString20());
    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void register_twice_with_same_credentials() throws Exception {
    UserCredentials userCredentials = new UserCredentials(getRandomString20(), getRandomString20());
    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  @Test
  public void authorize_not_registered() throws Exception {
    UserCredentials userCredentials = new UserCredentials(getRandomString20(), getRandomString20());
    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  @Test
  public void authorize() throws Exception {
    UserCredentials userCredentials = new UserCredentials(getRandomString20(), getRandomString20());
    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void authorize_after_logout() throws Exception {
    UserCredentials userCredentials = new UserCredentials(getRandomString20(), getRandomString20());
    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    MvcResult authorizeResponse = mockMvc
        .perform(
            post(SECURITY_REST_URL + "/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    String answerAsString = authorizeResponse.getResponse().getContentAsString();
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

    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();
  }

  @Test
  public void authenticate_anonymous() throws Exception {
    AuthUser anonymous = AuthUser.anonymous();
    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(anonymous))
        )
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticate_after_registration() throws Exception {
    UserCredentials userCredentials = new UserCredentials(getRandomString20(), getRandomString20());
    MvcResult registerResult = mockMvc
        .perform(
            post(SECURITY_REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    String answerAsString = registerResult.getResponse().getContentAsString();
    Answer answerAuthorize = JsonUtil.readValue(answerAsString, Answer.class);
    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(answerAuthorize.getAuthUser()))
        )
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void authenticate() throws Exception {
    UserCredentials userCredentials = new UserCredentials(getRandomString20(), getRandomString20());
    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    MvcResult authorizeResult = mockMvc
        .perform(
            post(SECURITY_REST_URL + "/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    String answerAsString = authorizeResult.getResponse().getContentAsString();
    Answer answerAuthorize = JsonUtil.readValue(answerAsString, Answer.class);
    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(answerAuthorize.getAuthUser()))
        )
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void authenticate_after_logout() throws Exception {
    UserCredentials userCredentials = new UserCredentials(getRandomString20(), getRandomString20());
    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    MvcResult authorizeResult = mockMvc
        .perform(
            post(SECURITY_REST_URL + "/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    String answerAsString = authorizeResult.getResponse().getContentAsString();
    Answer answerAuthorize = JsonUtil.readValue(answerAsString, Answer.class);
    MvcResult authenticateResult = mockMvc
        .perform(
            post(SECURITY_REST_URL + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(answerAuthorize.getAuthUser()))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    answerAsString = authenticateResult.getResponse().getContentAsString();
    answerAuthorize = JsonUtil.readValue(answerAsString, Answer.class);
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

    answerAsString = authorizeResult.getResponse().getContentAsString();
    answerAuthorize = JsonUtil.readValue(answerAsString, Answer.class);
    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(answerAuthorize.getAuthUser()))
        )
        .andDo(print())
        .andExpect(status().isForbidden())
        .andReturn();
  }

  @Test
  public void authenticate_after_registration_twice() throws Exception {
    UserCredentials userCredentials = new UserCredentials(getRandomString20(), getRandomString20());
    MvcResult registerResult = mockMvc
        .perform(
            post(SECURITY_REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    String answerAsString = registerResult.getResponse().getContentAsString();
    Answer answerAuthorize = JsonUtil.readValue(answerAsString, Answer.class);
    MvcResult authenticateResult = mockMvc
        .perform(
            post(SECURITY_REST_URL + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(answerAuthorize.getAuthUser()))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    answerAsString = authenticateResult.getResponse().getContentAsString();
    answerAuthorize = JsonUtil.readValue(answerAsString, Answer.class);
    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(answerAuthorize.getAuthUser()))
        )
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void logout() throws Exception {
    UserCredentials userCredentials = new UserCredentials(getRandomString20(), getRandomString20());
    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    MvcResult authorizeResult = mockMvc
        .perform(
            post(SECURITY_REST_URL + "/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    String answerAsString = authorizeResult.getResponse().getContentAsString();
    Answer answerAuthorize = JsonUtil.readValue(answerAsString, Answer.class);
    MvcResult authenticateResult = mockMvc
        .perform(
            post(SECURITY_REST_URL + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(answerAuthorize.getAuthUser()))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    answerAsString = authenticateResult.getResponse().getContentAsString();
    answerAuthorize = JsonUtil.readValue(answerAsString, Answer.class);
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
  }

  @Test
  public void logout_after_logout() throws Exception {
    UserCredentials userCredentials = new UserCredentials(getRandomString20(), getRandomString20());
    mockMvc
        .perform(
            post(SECURITY_REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    MvcResult authorizeResult = mockMvc
        .perform(
            post(SECURITY_REST_URL + "/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(userCredentials))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    String answerAsString = authorizeResult.getResponse().getContentAsString();
    Answer answerAuthorize = JsonUtil.readValue(answerAsString, Answer.class);
    MvcResult authenticateResult = mockMvc
        .perform(
            post(SECURITY_REST_URL + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(answerAuthorize.getAuthUser()))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    answerAsString = authenticateResult.getResponse().getContentAsString();
    answerAuthorize = JsonUtil.readValue(answerAsString, Answer.class);
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

    answerAsString = authenticateResult.getResponse().getContentAsString();
    answerAuthorize = JsonUtil.readValue(answerAsString, Answer.class);
    authUser = answerAuthorize.getAuthUser();
    mockMvc
        .perform(
            get(SECURITY_REST_URL + "/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(RequestConstants.ACCESS_TOKEN_HEADER, authUser.getAccessToken())
                .header(RequestConstants.USER_SESSION_HEADER, authUser.getUserSession())
        )
        .andDo(print())
        .andExpect(status().isForbidden());
  }
}