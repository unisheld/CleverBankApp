package com.cleverbank.models;

import lombok.Data;

/**
 * Модель клиента банка.
 */
@Data
public class Client {
    private long id;
    private String name;

    public Client(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
