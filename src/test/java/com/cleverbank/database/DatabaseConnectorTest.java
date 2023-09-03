package com.cleverbank.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectorTest {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/cleverbank";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "";

    @BeforeEach
    public void setUp() throws SQLException {
        // Вызывается перед каждым тестом для инициализации соединения
        DatabaseConnector.initialize(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    @AfterEach
    public void tearDown() {
        // Вызывается после каждого теста для закрытия соединения
        DatabaseConnector.closeConnection();
    }

    @Test
    public void testInitialize() throws SQLException {
        // Проверяем, что соединение успешно инициализируется
        assertNotNull(DatabaseConnector.getConnection());
    }

    @Test
    public void testInitializeWithInvalidCredentials() {
        // Попытка инициализации соединения с неверными учетными данными
        assertThrows(SQLException.class, () -> {
            DatabaseConnector.initialize(DB_URL, "invalidUsername", "invalidPassword");
        });
    }

    @Test
    public void testGetConnection() throws SQLException {
        // Проверяем, что получение соединения возвращает не null
        assertNotNull(DatabaseConnector.getConnection());
    }

    @Test
    public void testGetConnectionWhenNotInitialized() {
        // Попытка получения соединения до инициализации должна вызвать SQLException
        assertThrows(SQLException.class, () -> {
            DatabaseConnector.closeConnection(); // Закрываем соединение
            DatabaseConnector.getConnection(); // Пытаемся получить соединение
        });
    }

    @Test
    public void testCloseConnection() {
        // Проверяем, что соединение закрывается без ошибок
        DatabaseConnector.closeConnection();
        assertTrue(true); // Если не было ошибок при закрытии, тест считается успешным
    }
}

