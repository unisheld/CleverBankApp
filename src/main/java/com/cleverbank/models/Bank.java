package com.cleverbank.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Класс, представляющий банк.
 */
@Data
@AllArgsConstructor
@Builder
public class Bank {
    /** Уникальный идентификатор банка. */
    private long id;

    /** Название банка. */
    private String name;


}
