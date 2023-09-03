package com.cleverbank.exceptions;

import com.cleverbank.exceptions.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsufficientFundsExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String errorMessage = "Недостаточно средств на счете.";
        InsufficientFundsException exception = new InsufficientFundsException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testDefaultConstructor() {
        InsufficientFundsException exception = new InsufficientFundsException("");

        // Теперь передаем пустую строку в качестве сообщения
        assertEquals("", exception.getMessage());
    }
}


