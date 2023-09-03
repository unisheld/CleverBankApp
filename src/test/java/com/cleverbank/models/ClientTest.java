package com.cleverbank.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClientTest {

    @Test
    public void testClientConstruction() {
        // Создаем объект клиента с помощью конструктора и проверяем, что поля инициализированы правильно
        Client client = new Client(1, "John Doe");
        assertEquals(1, client.getId());
        assertEquals("John Doe", client.getName());
    }

    @Test
    public void testClientEquality() {
        // Создаем два объекта клиента с одинаковыми значениями полей и проверяем, что они равны
        Client client1 = new Client(1, "John Doe");
        Client client2 = new Client(1, "John Doe");
        assertEquals(client1, client2);
    }

    @Test
    public void testClientInequality() {
        // Создаем два объекта клиента с разными значениями полей и проверяем, что они не равны
        Client client1 = new Client(1, "John Doe");
        Client client2 = new Client(2, "Jane Smith");
        assertNotEquals(client1, client2);
    }
}
