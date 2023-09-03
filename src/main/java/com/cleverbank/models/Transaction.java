package com.cleverbank.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Модель транзакции.
 */
@Data
@Builder
@AllArgsConstructor
public class Transaction {
    private long id;
    private long senderAccountId; // Foreign Key к таблице Account
    private long receiverAccountId; // Foreign Key к таблице Account
    private double amount;
    private LocalDateTime timestamp;
    private String transactionType;




}
