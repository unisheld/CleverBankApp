package com.cleverbank.services;

import com.cleverbank.database.DatabaseReader;
import com.cleverbank.models.Account;
import com.cleverbank.models.Bank;
import com.cleverbank.models.Client;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class StatementGeneratorServiceTest {

    @Test
    void testGenerateStatementPDF() throws FileNotFoundException, SQLException {
        // Создаем заглушку для DatabaseReader
        DatabaseReader databaseReader = mock(DatabaseReader.class);

        // Создаем фейковый банк
        Bank fakeBank = new Bank(1, "Fake Bank");

        // Создаем фейкового клиента
        Client fakeClient = new Client(1, "Fake Client");

        // Создаем фейковый счет с установленным фейковым банком
        Account fakeAccount = new Account(1, 1000.0, fakeClient, fakeBank, LocalDateTime.now());

        // Создаем объект StatementGeneratorService с заглушкой
        StatementGeneratorService service = new StatementGeneratorService(databaseReader);

        // Замокаем вызовы для избежания фактического доступа к базе данных
        when(databaseReader.readClientTransactions(eq(fakeAccount.getId()), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>()); // Фейковые транзакции

        // Вызываем метод, который мы хотим протестировать
        service.generateStatementPDF(fakeAccount, LocalDate.now(), "test.pdf");

        // Проверяем, что нужные методы были вызваны
        verify(databaseReader).readClientTransactions(eq(fakeAccount.getId()), any(LocalDateTime.class));
        // Другие проверки, если необходимо
    }


}
