package com._98point6.droptoken.exception;

public class DropTokenInvalidInputException extends Throwable {
    private int code;

    public DropTokenInvalidInputException() { this(500); }
    public DropTokenInvalidInputException(int code) { this.code = code; }
    public DropTokenInvalidInputException(int code, String message) {
        this(code, message, null);
    }

    public DropTokenInvalidInputException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() { return code; }
}
