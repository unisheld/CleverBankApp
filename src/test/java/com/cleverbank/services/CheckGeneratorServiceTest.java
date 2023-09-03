package com.cleverbank.services;

import com.cleverbank.models.Account;
import com.cleverbank.models.Bank;
import com.cleverbank.models.Client;
import com.cleverbank.models.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

import com.cleverbank.models.Account;
import com.cleverbank.models.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckGeneratorServiceTest {

    private CheckGeneratorService checkGeneratorService;

    @BeforeEach
    public void setUp() {
        checkGeneratorService = new CheckGeneratorService();
    }

    @Test
    public void testGenerateTransferCheck() {
        // Создаем фиктивные данные для теста
        TransactionType transactionType = TransactionType.TRANSFER;
            // Создаем фиктивные банки и клиентов
        Bank bank1 = new Bank(1, "Clever-Bank");
        Client client1 = new Client(1, "Джейсон Момоа");
        Account senderAccount = new Account(1, 1000.0, client1, bank1, LocalDateTime.now());

        Bank bank2 = new Bank(2, "Открытие");
        Client client2 = new Client(2, "Малькольм Х");
        Account receiverAccount = new Account(2, 500.0, client2, bank2, LocalDateTime.now());
        double amount = 200.0;



        // Вызываем метод генерации чека
        checkGeneratorService.generateTransferCheck(transactionType, senderAccount.getBank().toString(), receiverAccount.getBank().toString(), senderAccount, receiverAccount, amount);

        // Проверяем, что чек был создан и файл существует
        String fileName = getLatestCheckFileName();
        assertTrue(new File(fileName).exists());


    }

    // Метод для получения имени последнего созданного чека (файла)
    private String getLatestCheckFileName() {
        File folder = new File("check");
        File[] files = folder.listFiles();

        // Находим последний созданный файл в директории "check"
        if (files != null && files.length > 0) {
            File latestFile = files[0];
            for (File file : files) {
                if (file.lastModified() > latestFile.lastModified()) {
                    latestFile = file;
                }
            }
            return latestFile.getPath();
        }

        return null;
    }
}



