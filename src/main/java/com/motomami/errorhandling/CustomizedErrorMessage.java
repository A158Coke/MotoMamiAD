package com.motomami.errorhandling;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springdoc.api.ErrorMessage;

@EqualsAndHashCode(callSuper = true)
@Value
public class CustomizedErrorMessage extends ErrorMessage {

    @JsonCreator
    public CustomizedErrorMessage(@JsonProperty("message") String message,
                                  @JsonProperty("errorCode") ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    ErrorCode errorCode;
}

