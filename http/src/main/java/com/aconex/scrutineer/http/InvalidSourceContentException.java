package com.aconex.scrutineer.http;

public class InvalidSourceContentException extends RuntimeException{
    public InvalidSourceContentException(String message) {
        super(message);
    }

    public InvalidSourceContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
