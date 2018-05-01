package com.example.backendspring;

import com.example.backendspring.matcher.ModelMatcher;
import com.example.backendspring.model.UserCredentials;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.UnsupportedEncodingException;

public class TestUtil {

    public static ResultActions print(ResultActions action) throws UnsupportedEncodingException {
        System.out.println(getContent(action));
        return action;
    }

    public static String getContent(ResultActions action) throws UnsupportedEncodingException {
        return action.andReturn().getResponse().getContentAsString();
    }

    public static RequestPostProcessor userHttpBasic(UserCredentials user) {
        return SecurityMockMvcRequestPostProcessors.httpBasic(user.getUsername(), user.getPassword());
    }

    /**
     * Compare entities using toString
     */
    public static class ToStringModelMatcher<T> extends ModelMatcher<T, String> {
        public ToStringModelMatcher(Class<T> entityClass) {
            super(Object::toString, entityClass);
        }
    }
}