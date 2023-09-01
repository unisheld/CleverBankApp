package com.cleverbank.services;

import com.cleverbank.models.Account;
import com.cleverbank.models.TransactionType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Сервис для генерации и сохранения банковских чеков.
 */
public class CheckGeneratorService {
    private static final String CHECK_FOLDER = "check";

    /**
     * Генерирует и сохраняет чек после операции перевода.
     *
     * @param transactionType  Тип операции (перевод, пополнение, снятие).
     * @param senderBank       Название банка отправителя.
     * @param receiverBank     Название банка получателя.
     * @param senderAccount    Счет отправителя.
     * @param receiverAccount  Счет получателя.
     * @param amount           Сумма операции.
     */
    public void generateTransferCheck(TransactionType transactionType, String senderBank, String receiverBank, Account senderAccount, Account receiverAccount, double amount) {
        generateCheck(TransactionType.TRANSFER, senderBank, receiverBank, senderAccount, receiverAccount, amount);
    }

    /**
     * Генерирует и сохраняет чек после операции пополнения счета.
     *
     * @param bank     Название банка.
     * @param account  Счет, на который произведено пополнение.
     * @param amount   Сумма операции.
     */
    public void generateDepositCheck(String bank, Account account, double amount) {
        generateCheck(TransactionType.DEPOSIT, bank, bank, account, account, amount);
    }

    /**
     * Генерирует и сохраняет чек после операции снятия средств со счета.
     *
     * @param bank     Название банка.
     * @param account  Счет, с которого произведено снятие средств.
     * @param amount   Сумма операции.
     */
    public void generateWithdrawCheck(String bank, Account account, double amount) {
        generateCheck(TransactionType.WITHDRAW, bank, bank, account, account, amount);
    }

    /**
     * Генерирует и сохраняет банковский чек.
     *
     * @param transactionType  Тип операции (перевод, пополнение, снятие).
     * @param senderBank       Название банка отправителя.
     * @param receiverBank     Название банка получателя.
     * @param senderAccount    Счет отправителя.
     * @param receiverAccount  Счет получателя.
     * @param amount           Сумма операции.
     */
    public void generateCheck(TransactionType transactionType, String senderBank, String receiverBank, Account senderAccount, Account receiverAccount, double amount) {
        try {
            Path folderPath = Paths.get(CHECK_FOLDER);
            Files.createDirectories(folderPath);

            long currentTimeMillis = System.currentTimeMillis();
            String fileName = folderPath.resolve("check_" + currentTimeMillis + ".txt").toString();

            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                String checkContent = "";

                if (transactionType == TransactionType.TRANSFER) {
                    checkContent = buildTransferCheckContent(currentTimeMillis, formattedDateTime, senderBank, receiverBank, senderAccount, receiverAccount, amount, transactionType);
                } else {
                    checkContent = buildDepositCheckContent(currentTimeMillis, formattedDateTime, senderBank, senderAccount, amount, transactionType);
                }

                writer.println(checkContent);

                System.out.println("Чек сохранен в файл: " + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Определение методов buildTransferCheckContent, buildDepositCheckContent, rightAlign

    private String buildTransferCheckContent(long checkNumber, String formattedDateTime, String senderBank, String receiverBank, Account senderAccount, Account receiverAccount, double amount, TransactionType transactionType) {
        StringBuilder builder = new StringBuilder();
        String horizontalLine = "-------------------------------------------------\n";

        builder.append(horizontalLine)
                .append("|               Банковский Чек                  |\n")
                .append("|                                               |\n")
                .append("|Чек:             ").append(rightAlign(String.valueOf(checkNumber))).append("|\n")
                .append("|").append(rightAlign(formattedDateTime)).append("|\n")
                .append("|тип транзакции:  ").append(rightAlign(transactionType.toString())).append("|\n")
                .append("|Банк отправителя:").append(rightAlign(senderBank)).append("|\n")
                .append("|Банк получателя: ").append(rightAlign(receiverBank)).append("|\n")
                .append("|Cчет отправителя:").append(rightAlign(String.valueOf(senderAccount.getId()))).append("|\n")
                .append("|Cчет получателя: ").append(rightAlign(String.valueOf(receiverAccount.getId()))).append("|\n")
                .append("|Сумма:           ").append(rightAlign(String.format("%.2f", amount) + " BYN")).append("|\n")
                .append(horizontalLine);
        return builder.toString();
    }


    private String buildDepositCheckContent(long checkNumber, String formattedDateTime, String bank, Account account, double amount,TransactionType transactionType) {
        StringBuilder builder = new StringBuilder();
        String horizontalLine = "-------------------------------------------------\n";

        builder.append(horizontalLine)
                .append("|               Банковский Чек                  |\n")
                .append("|                                               |\n")
                .append("|Чек:             ").append(rightAlign(String.valueOf(checkNumber))).append("|\n")
                .append("|").append(rightAlign(formattedDateTime)).append("|\n")
                .append("|тип транзакции:  ").append(rightAlign(transactionType.toString())).append("|\n")
                .append("|Банк:            ").append(rightAlign(bank)).append("|\n")
                .append("|Cчет:            ").append(rightAlign(String.valueOf(account.getId()))).append("|\n")
                .append("|Сумма:           ").append(rightAlign(String.format("%.2f", amount) + " BYN")).append("|\n")
                .append(horizontalLine);
        return builder.toString();
    }


    private String rightAlign(String text) {
        int lineLength = 30; // Длина строки в чеке (без учета символа новой строки)
        int textLength = text.length();

        // Проверяем, нужно ли выравнивать строку
        if (textLength >= lineLength) {
            return text;
        }

        int spacesCount = lineLength - textLength;
        return " ".repeat(spacesCount) + text;
    }



}


