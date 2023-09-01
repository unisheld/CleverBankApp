package com.cleverbank.database;

import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * Класс для чтения данных из базы данных.
 */
public class DatabaseReader {

    /**
     * Читает информацию о счетах из базы данных и выводит ее в консоль.
     *
     * @param connection Соединение с базой данных.
     * @throws SQLException Если произошла ошибка при чтении данных.
     */
    public static void readAccounts(Connection connection) throws SQLException {
        String query = "SELECT a.id, a.balance, c.name AS client_name, b.name AS bank_name, date_open " +
                "FROM accounts a " +
                "JOIN clients c ON a.client_id = c.id " +
                "JOIN banks b ON a.bank_id = b.id";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        System.out.println("Список счетов:");

        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            double balance = resultSet.getDouble("balance");
            String clientName = resultSet.getString("client_name");
            String bankName = resultSet.getString("bank_name");
            Timestamp dateOpen = resultSet.getTimestamp("date_open");

            System.out.println("ID: " + id + ", Баланс: " + balance + ", Клиент: " + clientName + ", Банк: " + bankName + ", Дата открытия счета: " + dateOpen);
        }

        resultSet.close();
        preparedStatement.close();
    }

    /**
     * Читает информацию о банках из базы данных и выводит ее в консоль.
     *
     * @param connection Соединение с базой данных.
     * @throws SQLException Если произошла ошибка при чтении данных.
     */
    public static void readBanks(Connection connection) throws SQLException {
        String query = "SELECT * FROM banks";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        System.out.println("Список банков:");

        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");

            System.out.println("ID: " + id + ", Название банка: " + name);
        }

        resultSet.close();
        preparedStatement.close();
    }

    /**
     * Читает информацию о транзакциях из базы данных и выводит ее в консоль.
     *
     * @param connection Соединение с базой данных.
     * @throws SQLException Если произошла ошибка при чтении данных.
     */
    public static void readTransactions(Connection connection) throws SQLException {
        String query = "SELECT * FROM transactions";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        // Определите ширину каждого столбца для выравнивания
        int idWidth = 5; // Ширина столбца ID
        int senderAccountIdWidth = 20; // Ширина столбца "Счет отправителя ID"
        int receiverAccountIdWidth = 20; // Ширина столбца "Счет получателя"
        int amountWidth = 10; // Ширина столбца "Сумма"
        int timestampWidth = 20; // Ширина столбца "Дата"
        int transTypeWidth = 15; // Ширина столбца "Тип транзакции"

        // Форматируйте заголовок таблицы
        String headerFormat = "%-" + idWidth + "s%-" + senderAccountIdWidth + "s%-" + receiverAccountIdWidth + "s%-" + amountWidth + "s%-" + timestampWidth + "s%-" + transTypeWidth + "s";
        System.out.printf(headerFormat, "ID", "Счет отправителя ID", "Счет получателя", "Сумма", "Дата", "Тип транзакции");
        System.out.println();

        // Создайте форматтер для даты и времени
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            long senderAccountId = resultSet.getLong("sender_account_id");
            long receiverAccountId = resultSet.getLong("receiver_account_id");
            double amount = resultSet.getDouble("amount");
            Timestamp timestamp = resultSet.getTimestamp("timestamp");
            String transType = resultSet.getString("trans_type");

            // Форматируйте данные для каждой строки
            String formattedTimestamp = dateFormat.format(timestamp);
            String rowFormat = "%-" + idWidth + "d%-" + senderAccountIdWidth + "d%-" + receiverAccountIdWidth + "d%-" + amountWidth + ".2f%-" + timestampWidth + "s%-" + transTypeWidth + "s";
            System.out.printf(rowFormat, id, senderAccountId, receiverAccountId, amount, formattedTimestamp, transType);
            System.out.println();
        }

        resultSet.close();
        preparedStatement.close();
    }

    /**
     * Читает информацию о клиентах из базы данных и выводит ее в консоль.
     *
     * @param connection Соединение с базой данных.
     * @throws SQLException Если произошла ошибка при чтении данных.
     */
    public static void readClients(Connection connection) throws SQLException {
        String query = "SELECT * FROM clients";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        System.out.println("Список клиентов:");

        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");

            System.out.println("ID: " + id + ", Имя: " + name);
        }

        resultSet.close();
        preparedStatement.close();
    }
}
