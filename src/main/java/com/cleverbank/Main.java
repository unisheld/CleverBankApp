package com.cleverbank;

import com.cleverbank.database.TransactionDAO;
import com.cleverbank.database.DatabaseConnector;
import com.cleverbank.database.TransactionSaver;
import com.cleverbank.models.*;
import com.cleverbank.services.TransferService;
import com.cleverbank.utils.AppConfig;
import com.cleverbank.services.CheckGeneratorService;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Главный класс приложения.
 */
@Slf4j
public class Main {
    public static void main(String[] args) {
        // Создание экземпляра AppConfig и загрузка конфигурации
        AppConfig appConfig = new AppConfig();
        appConfig.loadConfig("src/main/resources/config.yml");

        // Получение значений dbUrl, dbUsername и dbPassword из AppConfig
        String dbUrl = appConfig.getDbUrl();
        String dbUsername = appConfig.getDbUsername();
        String dbPassword = appConfig.getDbPassword();

        DatabaseConnector.initialize(dbUrl, dbUsername, dbPassword);

        // Создание банков и клиентов
        Bank bank1 = new Bank(1, "Clever-Bank");
        Client client1 = new Client(1, "Джейсон Момоа");
        Account account1 = new Account(1, 1000.0, client1, bank1, LocalDateTime.now());

        Bank bank2 = new Bank(2, "Открытие");
        Client client2 = new Client(2, "Малькольм Х");
        Account account2 = new Account(2, 1000.0, client2, bank2, LocalDateTime.now());

        // Создание экземпляра TransactionDAO (вам нужно убедиться, что у вас есть реализация этого DAO)
        TransactionDAO transactionDAO = new TransactionDAO();

        // Создание экземпляра TransactionSaver с передачей TransactionDAO
        TransactionSaver transactionSave = new TransactionSaver(transactionDAO);
        CheckGeneratorService checkGeneratorService = new CheckGeneratorService();

        // Создание экземпляра TransferService с передачей TransactionSaver и CheckGenerator
        TransferService transferService = new TransferService(transactionSave, checkGeneratorService);

        // Вызов метода transfer
        boolean transferSuccessful = transferService.transfer(account1, account2, 111.0);

        if (transferSuccessful) {
            System.out.println("Перевод успешно выполнен.");
        } else {
            System.out.println("Не удалось выполнить перевод.");
        }

        // Завершение приложения
        try {
            DatabaseConnector.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
