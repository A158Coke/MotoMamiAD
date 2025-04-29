package com.motomami.constant;

import lombok.Getter;

@Getter
public enum KeycloakEndPoint {
    USER_ENDPOINT("auth/admin/realms/motomami/users"),
    CLIENT_ENDPOINT("auth/admin/realms/motomami/clients"),
    ROLE_ENDPOINT("auth/admin/realms/motomami/clients/"),
    GROUP_ENDPOINT("auth/admin/realms/motomami/groups/"),
    USER_ROLE_ENDPOINT("auth/admin/realms/motomami/users/");

    private final String endpoint;
    KeycloakEndPoint(final String endpoint) {
        this.endpoint = endpoint;
    }
}
