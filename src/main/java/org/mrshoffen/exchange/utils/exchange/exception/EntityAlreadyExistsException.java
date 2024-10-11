package org.mrshoffen.exchange.utils.exchange.exception;

public class EntityAlreadyExistsException extends RuntimeException{
    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}
