package com.cleverbank.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BankTest {

    @Test
    public void testBankConstruction() {
        // Создаем объект банка с помощью конструктора и проверяем, что поля инициализированы правильно
        Bank bank = new Bank(1, "Fake Bank");
        assertEquals(1, bank.getId());
        assertEquals("Fake Bank", bank.getName());
    }

    @Test
    public void testBankBuilder() {
        // Используем билдер для создания объекта банка и проверяем, что поля инициализированы правильно
        Bank bank = Bank.builder()
                .id(2)
                .name("Another Bank")
                .build();
        assertEquals(2, bank.getId());
        assertEquals("Another Bank", bank.getName());
    }

    @Test
    public void testBankEquality() {
        // Создаем два объекта банка с одинаковыми значениями полей и проверяем, что они равны
        Bank bank1 = new Bank(1, "Bank One");
        Bank bank2 = new Bank(1, "Bank One");
        assertEquals(bank1, bank2);
    }

    @Test
    public void testBankInequality() {
        // Создаем два объекта банка с разными значениями полей и проверяем, что они не равны
        Bank bank1 = new Bank(1, "Bank One");
        Bank bank2 = new Bank(2, "Bank Two");
        assertNotEquals(bank1, bank2);
    }
}

