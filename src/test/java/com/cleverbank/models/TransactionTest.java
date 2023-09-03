package com.cleverbank.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    @Test
    public void testTransactionConstruction() {
        // Создаем объект транзакции с помощью конструктора и проверяем, что поля инициализированы правильно
        LocalDateTime timestamp = LocalDateTime.now();
        Transaction transaction = Transaction.builder()
                .id(1)
                .senderAccountId(2)
                .receiverAccountId(3)
                .amount(100.0)
                .timestamp(timestamp)
                .transactionType("Transfer")
                .build();

        assertEquals(1, transaction.getId());
        assertEquals(2, transaction.getSenderAccountId());
        assertEquals(3, transaction.getReceiverAccountId());
        assertEquals(100.0, transaction.getAmount(), 0.001);
        assertEquals(timestamp, transaction.getTimestamp());
        assertEquals("Transfer", transaction.getTransactionType());
    }

    @Test
    public void testTransactionEquality() {
        // Создаем два объекта транзакции с одинаковыми значениями полей и проверяем, что они равны
        LocalDateTime timestamp = LocalDateTime.now();
        Transaction transaction1 = Transaction.builder()
                .id(1)
                .senderAccountId(2)
                .receiverAccountId(3)
                .amount(100.0)
                .timestamp(timestamp)
                .transactionType("Transfer")
                .build();

        Transaction transaction2 = Transaction.builder()
                .id(1)
                .senderAccountId(2)
                .receiverAccountId(3)
                .amount(100.0)
                .timestamp(timestamp)
                .transactionType("Transfer")
                .build();

        assertEquals(transaction1, transaction2);
    }

    @Test
    public void testTransactionInequality() {
        // Создаем два объекта транзакции с разными значениями полей и проверяем, что они не равны
        LocalDateTime timestamp1 = LocalDateTime.now();
        LocalDateTime timestamp2 = LocalDateTime.now().plusMinutes(1);

        Transaction transaction1 = Transaction.builder()
                .id(1)
                .senderAccountId(2)
                .receiverAccountId(3)
                .amount(100.0)
                .timestamp(timestamp1)
                .transactionType("Transfer")
                .build();

        Transaction transaction2 = Transaction.builder()
                .id(2)
                .senderAccountId(3)
                .receiverAccountId(4)
                .amount(200.0)
                .timestamp(timestamp2)
                .transactionType("Withdraw")
                .build();

        assertNotEquals(transaction1, transaction2);
    }
}

