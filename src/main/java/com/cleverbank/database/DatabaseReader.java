package com.cleverbank.database;

import com.cleverbank.models.Account;

import com.cleverbank.models.Transaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для чтения данных из базы данных.
 */
public class DatabaseReader {
    private Connection connection;

    /**
     * Конструктор для инициализации объекта DatabaseReader.
     *
     * @param connection Объект соединения с базой данных.
     */
    public DatabaseReader(Connection connection) {
        this.connection = connection;
    }

    /**
     * Чтение транзакций для указанного клиента до заданной даты.
     *
     * @param clientId Идентификатор клиента.
     * @param endDate  Дата окончания периода.
     * @return Список транзакций клиента.
     * @throws SQLException Если произошла ошибка при чтении из базы данных.
     */
    public List<Transaction> readClientTransactions(int clientId, LocalDateTime endDate) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();

        String query = "SELECT * FROM transactions " +
                "WHERE (sender_account_id IN (SELECT id FROM accounts WHERE client_id = ?) " +
                "       OR receiver_account_id IN (SELECT id FROM accounts WHERE client_id = ?))" +
                "AND timestamp >= (SELECT date_open FROM accounts WHERE id = sender_account_id) " +
                "AND timestamp <= ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, clientId);
            preparedStatement.setLong(2, clientId);
            preparedStatement.setTimestamp(3, Timestamp.valueOf(endDate));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    long senderAccountId = resultSet.getLong("sender_account_id");
                    long receiverAccountId = resultSet.getLong("receiver_account_id");
                    double amount = resultSet.getDouble("amount");
                    LocalDateTime timestamp = resultSet.getTimestamp("timestamp").toLocalDateTime();
                    String transactionType = resultSet.getString("trans_type");
                    transactions.add(new Transaction(id, senderAccountId, receiverAccountId, amount, timestamp, transactionType));
                }
            }
        }

        return transactions;
    }

    /**
     * Получение списка счетов для указанного клиента.
     *
     * @param clientId Идентификатор клиента.
     * @return Список счетов клиента.
     * @throws SQLException Если произошла ошибка при чтении из базы данных.
     */
    public List<Account> getAccountsForClient(long clientId) throws SQLException {
        List<Account> accounts = new ArrayList<>();

        String query = "SELECT accounts.id AS id, accounts.balance, clients.name AS clientName, " +
                "accounts.bank_id, banks.name AS bankName, accounts.date_open " +
                "FROM accounts " +
                "INNER JOIN clients ON accounts.client_id = clients.id " +
                "INNER JOIN banks ON accounts.bank_id = banks.id " +
                "WHERE accounts.client_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, clientId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    double balance = resultSet.getDouble("balance");
                    String clientName = resultSet.getString("clientName");
                    long bankId = resultSet.getLong("bank_id");
                    String bankName = resultSet.getString("bankName");
                    LocalDateTime dateOpen = resultSet.getTimestamp("date_open").toLocalDateTime();
                    accounts.add(new Account(id, balance, clientId, clientName, bankId, bankName, dateOpen));
                }
            }
        }

        return accounts;
    }

    /**
     * Получение счета по его номеру.
     *
     * @param accountNumber Номер счета.
     * @return Объект счета или null, если счет не найден.
     * @throws SQLException Если произошла ошибка при чтении из базы данных.
     */
    public Account getAccountByNumber(int accountNumber) throws SQLException {
        String query = "SELECT accounts.id AS id, accounts.balance, clients.name AS clientName, " +
                "accounts.client_id AS client_id, accounts.bank_id, banks.name AS bankName, accounts.date_open " +
                "FROM accounts " +
                "INNER JOIN clients ON accounts.client_id = clients.id " +
                "INNER JOIN banks ON accounts.bank_id = banks.id " +
                "WHERE accounts.id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, accountNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    double balance = resultSet.getDouble("balance");
                    long clientId = resultSet.getLong("client_id");
                    String clientName = resultSet.getString("clientName");
                    long bankId = resultSet.getLong("bank_id");
                    String bankName = resultSet.getString("bankName");
                    LocalDateTime dateOpen = resultSet.getTimestamp("date_open").toLocalDateTime();
                    return new Account(id, balance, clientId, clientName, bankId, bankName, dateOpen);
                } else {
                    return null; // Счет с указанным номером не найден
                }
            }
        }
    }

    /**
     * Получение списка всех счетов.
     *
     * @return Список всех счетов.
     * @throws SQLException Если произошла ошибка при чтении из базы данных.
     */
    public List<Account> getAllAccounts() throws SQLException {
        List<Account> accounts = new ArrayList<>();

        String query = "SELECT accounts.id AS id, accounts.balance, clients.name AS clientName, " +
                "accounts.client_id AS client_id, accounts.bank_id, banks.name AS bankName, accounts.date_open " +
                "FROM accounts " +
                "INNER JOIN clients ON accounts.client_id = clients.id " +
                "INNER JOIN banks ON accounts.bank_id = banks.id";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                double balance = resultSet.getDouble("balance");
                long clientId = resultSet.getLong("client_id");
                String clientName = resultSet.getString("clientName");
                long bankId = resultSet.getLong("bank_id");
                String bankName = resultSet.getString("bankName");
                LocalDateTime dateOpen = resultSet.getTimestamp("date_open").toLocalDateTime();

                // Создаем объект Account и добавляем его в список
                Account account = new Account(id, balance, clientId, clientName, bankId, bankName, dateOpen);
                accounts.add(account);
            }
        }

        return accounts;
    }
}
