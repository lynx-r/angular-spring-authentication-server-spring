package com.example.backendspring;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonObjectMapper extends ObjectMapper {

    private static final ObjectMapper MAPPER = new JacksonObjectMapper();

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    private JacksonObjectMapper() {
//        SimpleModule customModule = new SimpleModule("customModule", new Version(1, 0, 0, null));
//        customModule.addSerializer(new JsonLocalDateTimeConverter.UserSettingSerializer());
//        customModule.addDeserializer(LocalDateTime.class, new JsonLocalDateTimeConverter.UserSettingDeserializer());
//        registerModule(customModule);

        setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}