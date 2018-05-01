package com.example.backendspring.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by Aleksey Popryaduhin on 16:13 01/10/2017.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = PongPayload.class, name = "PongPayload"),
    @JsonSubTypes.Type(value = PingPayload.class, name = "PingPayload"),

    @JsonSubTypes.Type(value = UserCredentials.class, name = "UserCredentials"),
    @JsonSubTypes.Type(value = AuthUser.class, name = "AuthUser"),
})
public interface Payload {
}
