package com.cleverbank.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Модель клиента банка.
 */
@Data
@AllArgsConstructor
public class Client {
    private long id;
    private String name;


}
