package com.cleverbank.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Класс, представляющий банк.
 */
@Data
@AllArgsConstructor
public class Bank {
    /** Уникальный идентификатор банка. */
    private Long id;

    /** Название банка. */
    private String name;

    /** Lock (замок) для обеспечения безопасности операций с банком. */
    private Lock lock;

    /**
     * Создает новый экземпляр банка с заданными идентификатором и названием.
     *
     * @param id   Уникальный идентификатор банка.
     * @param name Название банка.
     */
    public Bank(long id, String name) {
        this.id = id;
        this.name = name;
        this.lock = new ReentrantLock(); // Создаем новый замок по умолчанию
    }
}
