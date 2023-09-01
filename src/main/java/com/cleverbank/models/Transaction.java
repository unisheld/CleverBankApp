package com.cleverbank.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Модель транзакции.
 */
@Data
@Builder
public class Transaction {
    private long id;
    private long senderAccountId; // Foreign Key к таблице Account
    private long receiverAccountId; // Foreign Key к таблице Account
    private double amount;
    private LocalDateTime timestamp;
    private String transactionType;
}
