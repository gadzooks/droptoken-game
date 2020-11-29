package com._98point6.droptoken.exception;

import lombok.Getter;

public class DropTokenException extends Throwable {
    @Getter
    private int code;

    public DropTokenException() { this(500); }
    public DropTokenException(int code) { this.code = code; }
    public DropTokenException(int code, String message) {
        this(code, message, null);
    }

    public DropTokenException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

}
