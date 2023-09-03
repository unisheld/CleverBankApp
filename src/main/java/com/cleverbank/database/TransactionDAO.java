package com.cleverbank.database;

import com.cleverbank.models.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Класс для сохранения транзакций в базу данных.
 */
public class TransactionDAO {
    /**
     * Сохраняет транзакцию в базе данных.
     *
     * @param transaction Транзакция для сохранения.
     * @throws SQLException Если произошла ошибка при сохранении транзакции.
     */
    public void save(Transaction transaction) throws SQLException {
        String insertQuery = "INSERT INTO transactions (sender_account_id, receiver_account_id, amount, timestamp, trans_type) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setLong(1, transaction.getSenderAccountId());
            preparedStatement.setLong(2, transaction.getReceiverAccountId());
            preparedStatement.setDouble(3, transaction.getAmount());
            preparedStatement.setObject(4, transaction.getTimestamp());
            preparedStatement.setString(5, transaction.getTransactionType());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении транзакции: " + e.getMessage());
            throw new SQLException("Ошибка при сохранении транзакции", e);
        }
    }
}
