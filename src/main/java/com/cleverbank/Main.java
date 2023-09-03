package com.cleverbank;

import com.cleverbank.database.*;
import com.cleverbank.models.*;
import com.cleverbank.services.*;
import com.cleverbank.utils.AppConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
public class Main {
    public static void main(String[] args) {
        try {
            // Инициализация соединения с базой данных и другие необходимые объекты
            // Здесь инициализируем соединение с базой данных
            AppConfig appConfig = new AppConfig();
            appConfig.loadConfig("src/main/resources/config.yml");

            String dbUrl = appConfig.getDbUrl();
            String dbUsername = appConfig.getDbUsername();
            String dbPassword = appConfig.getDbPassword();
            int clientId = appConfig.getClientId();

            // Инициализируем соединение с базой данных
            DatabaseConnector.initialize(dbUrl, dbUsername, dbPassword);

            // Создание экземпляра DatabaseReader
            Connection connection = DatabaseConnector.getConnection();
            DatabaseReader databaseReader = new DatabaseReader(connection);

            // Инициализация всех сервисов
            TransactionDAO transactionDAO = new TransactionDAO();
            TransactionSaver transactionSaver = new TransactionSaver(transactionDAO);
            DatabaseUpdater databaseUpdater = new DatabaseUpdater(connection);

            CheckGeneratorService checkGeneratorService = new CheckGeneratorService();
            DepositService depositService = new DepositService(transactionSaver, checkGeneratorService, databaseUpdater);
            WithdrawService withdrawService = new WithdrawService(transactionSaver, checkGeneratorService, databaseUpdater);
            TransferService transferService = new TransferService(transactionSaver, checkGeneratorService, databaseUpdater);

            // Инициализация сервисов для выписки и расчета процентов
            StatementGeneratorService statementGeneratorService = new StatementGeneratorService(databaseReader);
            InterestCalculatorService interestCalculatorService = new InterestCalculatorService(appConfig, databaseUpdater, databaseReader);

            // Создание объекта для ввода с клавиатуры
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // Основной цикл программы
            boolean isRunning = true;

            while (isRunning) {
                System.out.println("Выберите действие:");
                System.out.println("1. Пополнить счет");
                System.out.println("2. Снять со счета");
                System.out.println("3. Перевести на счет");
                System.out.println("4. Сделать выписку со счета");
                System.out.println("5. Рассчитать проценты");
                System.out.println("0. Выход");

                String choice = reader.readLine();

                switch (choice) {
                    case "1":
                        // Пополнение счета
                        List<Account> clientAccounts1 = databaseReader.getAccountsForClient(clientId);
                        displayAccounts(clientAccounts1); // Выводим список счетов перед операцией
                        int depositAccountNumber = readAccountNumber(reader, clientAccounts1.size());
                        if (depositAccountNumber >= 1 && depositAccountNumber <= clientAccounts1.size()) {
                            double depositAmount = readAmount(reader);
                            depositService.execute(clientAccounts1.get(depositAccountNumber - 1), depositAmount);
                        } else {
                            System.out.println("Некорректный номер счета. Пожалуйста, выберите существующий счет.");
                        }
                        isRunning = false;
                        break;
                    case "2":
                        // Снятие со счета
                        List<Account> clientAccounts2 = databaseReader.getAccountsForClient(clientId);
                        displayAccounts(clientAccounts2); // Выводим список счетов перед операцией
                        int withdrawAccountNumber = readAccountNumber(reader, clientAccounts2.size());
                        if (withdrawAccountNumber >= 1 && withdrawAccountNumber <= clientAccounts2.size()) {
                            double withdrawAmount = readAmount(reader);
                            withdrawService.execute(clientAccounts2.get(withdrawAccountNumber - 1), withdrawAmount);
                        } else {
                            System.out.println("Некорректный номер счета. Пожалуйста, выберите существующий счет.");
                        }
                        isRunning = false;
                        break;
                    case "3":
                        // Перевод на счет
                        List<Account> allAccounts = databaseReader.getAllAccounts(); // Получить все счета из базы данных
                        // Выводим список всех счетов
                        System.out.println("Список всех счетов:");
                        for (int i = 0; i < allAccounts.size(); i++) {
                            Account account = allAccounts.get(i);
                            System.out.println(i + 1 + ". " + account.getId() + " Баланс: " + account.getBalance() + " BYN. Банк: " + account.getBank().getName());
                        }
                        int senderAccountNumber = readAccountNumber(reader, allAccounts.size());
                        int recipientAccountNumber = readAccountNumber(reader, allAccounts.size());

                        if (senderAccountNumber >= 1 && senderAccountNumber <= allAccounts.size() &&
                                recipientAccountNumber >= 1 && recipientAccountNumber <= allAccounts.size() &&
                                senderAccountNumber != recipientAccountNumber) { // Убедиться, что счета разные
                            double transferAmount = readAmount(reader);

                            Account senderAccount = allAccounts.get(senderAccountNumber - 1);
                            Account recipientAccount = allAccounts.get(recipientAccountNumber - 1);

                            transferService.transfer(senderAccount, recipientAccount, transferAmount);
                        } else {
                            System.out.println("Некорректные номера счетов. Пожалуйста, выберите разные счета.");
                        }
                        isRunning = false;
                        break;
                    case "4":
                        // Выписка со счета
                        List<Account> clientAccounts4 = databaseReader.getAccountsForClient(clientId);
                        displayAccounts(clientAccounts4); // Выводим список счетов перед операцией
                        int statementAccountNumber = readAccountNumber(reader, -1);
                        LocalDate statementDate = readDate(reader);
                        String outputFileName = "statement.pdf"; // Можно указать путь к файлу

                        // Получение счета по его номеру
                        Account statementAccount = databaseReader.getAccountByNumber(statementAccountNumber);

                        if (statementAccount != null) {
                            // Преобразовываем statementDate в LocalDate
                            // Получение текущей даты и времени для имени файла
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                            String currentDateTime = LocalDateTime.now().format(formatter);

                            String fileName = "statement/statement_" + currentDateTime + ".pdf";
                            statementGeneratorService.generateStatementPDF(clientAccounts4.get(statementAccountNumber - 1), statementDate, fileName);
                            System.out.println("Выписка сохранена в файл: " + fileName);
                        } else {
                            System.out.println("Счет с таким номером не найден.");
                        }
                        isRunning = false;
                        break;
                    case "5":
                        // Рассчет процентов
                        interestCalculatorService.calculateInterest();
                        System.out.println("Проценты начислены.");
                        isRunning = false;
                        break;
                    case "0":
                        // Выход из программы
                        System.out.println("Выход из программы.");
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Некорректный выбор. Попробуйте еще раз.");
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка ввода/вывода: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Ошибка формата числа: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Необработанная ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnector.closeConnection();
        }
    }

    // Метод для вывода списка счетов на экран
    private static void displayAccounts(List<Account> accounts) {
        System.out.println("Доступные счета:");
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            System.out.println(i + 1 + ". " + account.getId() + " Баланс: " + account.getBalance() + " BYN. Банк: " + account.getBank().getName());
        }
    }

    // Метод для чтения номера счета с проверкой
    private static int readAccountNumber(BufferedReader reader, int maxAccountNumber) throws IOException {
        int accountNumber;
        while (true) {
            System.out.println("Введите номер счета:");
            String input = reader.readLine();
            try {
                accountNumber = Integer.parseInt(input);
                if (accountNumber < 1 || (maxAccountNumber != -1 && accountNumber > maxAccountNumber)) {
                    System.out.println("Некорректный номер счета. Пожалуйста, введите существующий номер.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Некорректный номер счета. Пожалуйста, введите число.");
            }
        }
        return accountNumber;
    }

    // Метод для чтения суммы с проверкой
    private static double readAmount(BufferedReader reader) throws IOException {
        double amount;
        while (true) {
            System.out.println("Введите сумму:");
            String input = reader.readLine();
            try {
                amount = Double.parseDouble(input);
                if (amount <= 0) {
                    System.out.println("Сумма должна быть положительным числом.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Некорректная сумма. Пожалуйста, введите число.");
            }
        }
        return amount;
    }

    // Метод для проверки корректности формата даты
    private static LocalDate readDate(BufferedReader reader) throws IOException {
        LocalDate date;
        while (true) {
            System.out.println("Введите дату в формате дд мм гггг (например, 01 09 2023):");
            String dateString = reader.readLine();
            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MM yyyy");
                date = LocalDate.parse(dateString, dateFormatter);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Некорректный формат даты. Пожалуйста, введите дату в указанном формате.");
            }
        }
        return date;
    }
}
