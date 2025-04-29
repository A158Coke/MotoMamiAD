package com.motomami.constant;

import lombok.Getter;

@Getter
public enum ApiEndPoint {

    USER_ENDPOINT("/user");
    private final String endpoint;
    ApiEndPoint(final String endpoint) {
        this.endpoint = endpoint;
    }

}
