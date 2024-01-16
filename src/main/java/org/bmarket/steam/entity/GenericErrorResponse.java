package org.bmarket.steam.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class GenericErrorResponse {

    private String message;
    private String error;
    private int status;
    private String path;
    private String method;
    private OffsetDateTime timestamp;

}
