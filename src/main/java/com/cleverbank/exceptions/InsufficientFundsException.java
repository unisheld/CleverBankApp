package com.cleverbank.exceptions;

/**
 * Исключение, выбрасываемое при недостаточных средствах на счете.
 */
public class InsufficientFundsException extends RuntimeException {
    /**
     * Конструктор с сообщением об ошибке.
     *
     * @param message Сообщение об ошибке.
     */
    public InsufficientFundsException(String message) {
        super(message);
    }
}

