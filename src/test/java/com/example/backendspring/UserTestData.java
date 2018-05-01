package com.example.backendspring;

import com.example.backendspring.matcher.ModelMatcher;
import com.example.backendspring.model.DeepClone;
import com.example.backendspring.model.UserCredentials;

public class UserTestData {

    public static final UserCredentials USER_CREDENTIALS = new UserCredentials("user@yandex.ru", "password");

    public static final ModelMatcher<UserCredentials, UserCredentials> MATCHER = new ModelMatcher<>(
        DeepClone::deepClone, UserCredentials.class);
}