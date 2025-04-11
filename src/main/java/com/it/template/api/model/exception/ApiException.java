package com.it.template.api.model.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"stackTrace", "suppressed"})
public class ApiException extends RuntimeException {

    private Integer code;
    private String exceptionMessage;

    public ApiException(final HttpStatus httpStatus) {
        this.code = httpStatus.value();
        this.exceptionMessage = httpStatus.getReasonPhrase();
    }

    public ApiException(final HttpStatus httpStatus, final String exceptionMessage) {
        this.code = httpStatus.value();
        this.exceptionMessage = exceptionMessage;
    }

    public ApiException(final String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public ApiException(final String message, final String exceptionMessage) {
        super(message);
        this.exceptionMessage = exceptionMessage;
    }

    public ApiException(final String message, final Throwable cause, final String exceptionMessage) {
        super(message, cause);
        this.exceptionMessage = exceptionMessage;
    }

    public ApiException(final Throwable cause, final String exceptionMessage) {
        super(cause);
        this.exceptionMessage = exceptionMessage;
    }

    public ApiException(final String message,
                        final Throwable cause,
                        final boolean enableSuppression,
                        final boolean writableStackTrace,
                        final String exceptionMessage) {

        super(message, cause, enableSuppression, writableStackTrace);
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    @SneakyThrows
    public String toString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
