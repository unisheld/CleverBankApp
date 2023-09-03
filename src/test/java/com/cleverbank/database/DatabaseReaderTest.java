package com.cleverbank.database;

import com.cleverbank.models.Account;
import com.cleverbank.models.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class DatabaseReaderTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);

        // Инициализация моков и их поведение
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @Test
    public void testReadClientTransactions() throws SQLException {
        int clientId = 1;
        LocalDateTime endDate = LocalDateTime.now();

        // Создаем ожидаемую транзакцию
        Transaction expectedTransaction = new Transaction(1L, 2L, 3L, 100.0, LocalDateTime.now(), "Deposit");

        // Мокируем resultSet для возвращения ожидаемой транзакции
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getLong("id")).thenReturn(expectedTransaction.getId());
        when(resultSet.getLong("sender_account_id")).thenReturn(expectedTransaction.getSenderAccountId());
        when(resultSet.getLong("receiver_account_id")).thenReturn(expectedTransaction.getReceiverAccountId());
        when(resultSet.getDouble("amount")).thenReturn(expectedTransaction.getAmount());
        when(resultSet.getTimestamp("timestamp")).thenReturn(Timestamp.valueOf(expectedTransaction.getTimestamp()));
        when(resultSet.getString("trans_type")).thenReturn(expectedTransaction.getTransactionType());

        DatabaseReader databaseReader = new DatabaseReader(connection);
        List<Transaction> transactions = databaseReader.readClientTransactions(clientId, endDate);

        assertEquals(1, transactions.size());
        assertEquals(expectedTransaction, transactions.get(0));

        // Проверяем, что SQL запрос был выполнен с правильными аргументами
        verify(preparedStatement).setLong(1, clientId);
        verify(preparedStatement).setLong(2, clientId);
        verify(preparedStatement).setTimestamp(3, Timestamp.valueOf(endDate));
    }

    @Test
    public void testGetAccountsForClient() throws SQLException {
        long clientId = 1L;

        // Создаем ожидаемый аккаунт
        Account expectedAccount = new Account(1, 1000.0, clientId, "ClientName", 2L, "BankName", LocalDateTime.now());

        // Мокируем resultSet для возвращения ожидаемого аккаунта
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("id")).thenReturn(expectedAccount.getId());
        when(resultSet.getDouble("balance")).thenReturn(expectedAccount.getBalance());
        when(resultSet.getString("clientName")).thenReturn(expectedAccount.getClient().getName());
        when(resultSet.getLong("bank_id")).thenReturn(expectedAccount.getBank().getId());
        when(resultSet.getString("bankName")).thenReturn(expectedAccount.getBank().getName());
        when(resultSet.getTimestamp("date_open")).thenReturn(Timestamp.valueOf(expectedAccount.getDateOpen()));

        DatabaseReader databaseReader = new DatabaseReader(connection);
        List<Account> accounts = databaseReader.getAccountsForClient(clientId);

        assertEquals(1, accounts.size());
        assertEquals(expectedAccount, accounts.get(0));

        // Проверяем, что SQL запрос был выполнен с правильными аргументами
        verify(preparedStatement).setLong(1, clientId);
    }

    @Test
    public void testGetAccountByNumber() throws SQLException {
        int accountNumber = 1;

        // Создаем ожидаемый аккаунт
        Account expectedAccount = new Account(1, 1000.0, 2L, "ClientName", 3L, "BankName", LocalDateTime.now());

        // Мокируем resultSet для возвращения ожидаемого аккаунта
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("id")).thenReturn(expectedAccount.getId());
        when(resultSet.getDouble("balance")).thenReturn(expectedAccount.getBalance());
        when(resultSet.getLong("client_id")).thenReturn(expectedAccount.getClient().getId());
        when(resultSet.getString("clientName")).thenReturn(expectedAccount.getClient().getName());
        when(resultSet.getLong("bank_id")).thenReturn(expectedAccount.getBank().getId());
        when(resultSet.getString("bankName")).thenReturn(expectedAccount.getBank().getName());
        when(resultSet.getTimestamp("date_open")).thenReturn(Timestamp.valueOf(expectedAccount.getDateOpen()));

        DatabaseReader databaseReader = new DatabaseReader(connection);
        Account account = databaseReader.getAccountByNumber(accountNumber);

        assertEquals(expectedAccount, account);

        // Проверяем, что SQL запрос был выполнен с правильными аргументами
        verify(preparedStatement).setInt(1, accountNumber);
    }

    @Test
    public void testGetAccountByNumberNotFound() throws SQLException {
        int accountNumber = 1;

        // Мокируем resultSet для возвращения null (аккаунт не найден)
        when(resultSet.next()).thenReturn(false);

        DatabaseReader databaseReader = new DatabaseReader(connection);
        Account account = databaseReader.getAccountByNumber(accountNumber);

        assertNull(account);

        // Проверяем, что SQL запрос был выполнен с правильными аргументами
        verify(preparedStatement).setInt(1, accountNumber);
    }

    @Test
    public void testGetAllAccounts() throws SQLException {
        // Создаем ожидаемый аккаунт
        Account expectedAccount = new Account(1, 1000.0, 2L, "ClientName", 3L, "BankName", LocalDateTime.now());

        // Мокируем resultSet для возвращения ожидаемого аккаунта
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("id")).thenReturn(expectedAccount.getId());
        when(resultSet.getDouble("balance")).thenReturn(expectedAccount.getBalance());
        when(resultSet.getLong("client_id")).thenReturn(expectedAccount.getClient().getId());
        when(resultSet.getString("clientName")).thenReturn(expectedAccount.getClient().getName());
        when(resultSet.getLong("bank_id")).thenReturn(expectedAccount.getBank().getId());
        when(resultSet.getString("bankName")).thenReturn(expectedAccount.getBank().getName());
        when(resultSet.getTimestamp("date_open")).thenReturn(Timestamp.valueOf(expectedAccount.getDateOpen()));

        DatabaseReader databaseReader = new DatabaseReader(connection);
        List<Account> accounts = databaseReader.getAllAccounts();

        assertEquals(1, accounts.size());
        assertEquals(expectedAccount, accounts.get(0));
    }
}

