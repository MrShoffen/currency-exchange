package org.mrshoffen.exchange.utils.exchange.dto.response;

import lombok.Value;

@Value(staticConstructor = "of")
public class ErrorResponseDto {
    String message;
}
