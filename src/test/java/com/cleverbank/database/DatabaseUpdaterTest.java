package com.cleverbank.database;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DatabaseUpdaterTest {

    private DatabaseUpdater databaseUpdater;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @BeforeEach
    public void setUp() {
        // Создаем моки для Connection, PreparedStatement и ResultSet
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        try {
            // Настроим моки для создания корректных объектов PreparedStatement и ResultSet
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);

            // Создаем объект DatabaseUpdater с мок-соединением
            databaseUpdater = new DatabaseUpdater(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAccountExists_WhenAccountExists() throws SQLException {
        // Заглушка: счет существует
        when(resultSet.next()).thenReturn(true);

        // Вызываем метод, который мы тестируем
        boolean exists = databaseUpdater.accountExists(1);

        // Проверяем, что метод возвращает true
        assertTrue(exists);
    }

    @Test
    public void testAccountExists_WhenAccountDoesNotExist() throws SQLException {
        // Заглушка: счет не существует
        when(resultSet.next()).thenReturn(false);

        // Вызываем метод, который мы тестируем
        boolean exists = databaseUpdater.accountExists(2);

        // Проверяем, что метод возвращает false
        assertFalse(exists);
    }

    @Test
    public void testUpdateAccountBalance_WhenAccountExists() throws SQLException {
        // Заглушка: счет существует
        when(resultSet.next()).thenReturn(true);

        // Вызываем метод, который мы тестируем
        databaseUpdater.updateAccountBalance(1, 1000.0);

        // Проверяем, что был вызван метод executeUpdate
        verify(preparedStatement).executeUpdate();
    }

    @Test
    public void testUpdateAccountBalance_WhenAccountDoesNotExist() throws SQLException {
        // Заглушка: счет не существует
        when(resultSet.next()).thenReturn(false);

        // Вызываем метод, который мы тестируем
        databaseUpdater.updateAccountBalance(2, 1000.0);

        // Проверяем, что executeUpdate не был вызван
        verify(preparedStatement, never()).executeUpdate();
    }


}


