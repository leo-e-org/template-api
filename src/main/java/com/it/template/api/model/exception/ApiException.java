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

    public ApiException(HttpStatus httpStatus) {
        this.code = httpStatus.value();
        this.exceptionMessage = httpStatus.getReasonPhrase();
    }

    public ApiException(HttpStatus httpStatus, String exceptionMessage) {
        this.code = httpStatus.value();
        this.exceptionMessage = exceptionMessage;
    }

    public ApiException(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public ApiException(String message, String exceptionMessage) {
        super(message);
        this.exceptionMessage = exceptionMessage;
    }

    public ApiException(String message, Throwable cause, String exceptionMessage) {
        super(message, cause);
        this.exceptionMessage = exceptionMessage;
    }

    public ApiException(Throwable cause, String exceptionMessage) {
        super(cause);
        this.exceptionMessage = exceptionMessage;
    }

    public ApiException(String message,
                        Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace,
                        String exceptionMessage) {

        super(message, cause, enableSuppression, writableStackTrace);
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    @SneakyThrows
    public String toString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
