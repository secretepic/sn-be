package com.boyu.snbe.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SnException extends Exception {

    private Integer code;

    public SnException() {
        super();
    }

    public SnException(String message) {
        super(message);
    }

    public SnException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public SnException(String message, Throwable cause) {
        super(message, cause);
    }

    public SnException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public SnException(Throwable cause) {
        super(cause);
    }

}
