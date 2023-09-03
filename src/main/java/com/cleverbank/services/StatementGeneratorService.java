package com.cleverbank.services;

import com.cleverbank.database.DatabaseReader;
import com.cleverbank.models.Account;
import com.cleverbank.models.Transaction;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Сервис для генерации выписки по счету в формате PDF.
 */
@AllArgsConstructor
public class StatementGeneratorService {

    private final DatabaseReader databaseReader;

    /**
     * Генерирует PDF-документ с выпиской по счету.
     *
     * @param account    Счет для которого создается выписка.
     * @param endDate    Дата окончания периода выписки.
     * @param outputFile Путь к файлу, в который будет сохранен PDF-документ.
     */
    public void generateStatementPDF(Account account, LocalDate endDate, String outputFile) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.open();

            // Устанавливаем шрифт с поддержкой кириллицы
            BaseFont baseFont = BaseFont.createFont("src/main/resources/Roboto-light.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            // Устанавливаем шрифт для текста
            Font font = new Font(baseFont, 12);

            // Добавление заголовка
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
            Paragraph title = new Paragraph("Выписка", titleFont);
            document.add(title);

            // Добавление информации о банке
            Paragraph bankInfo = new Paragraph("Банк: " + account.getBank().getName(), font);
            document.add(bankInfo);

            // Добавление информации о клиенте
            Paragraph clientInfo = new Paragraph("Клиент: " + account.getClient().getName(), font);
            document.add(clientInfo);

            // Добавление информации о счете
            Paragraph accountInfo = new Paragraph("Счет: " + account.getId(), font);
            document.add(accountInfo);

            // Добавление информации о валюте и дате открытия счета
            Paragraph currencyAndDate = new Paragraph("Валюта: BYN   Дата открытия: " + formatDateTime(account.getDateOpen()), font);
            document.add(currencyAndDate);

            // Добавление информации о периоде
            Paragraph periodInfo = new Paragraph("Период: " + formatDateTime(account.getDateOpen()) + " - " + formatDateTime(endDate.atStartOfDay()), font);
            document.add(periodInfo);

            // Добавление информации о дате и времени формирования
            Paragraph generationDateTime = new Paragraph("Дата и время формирования: " + formatDateTime(LocalDateTime.now()), font);
            document.add(generationDateTime);

            // Добавление информации об остатке на счете
            Paragraph balanceInfo = new Paragraph("Остаток: " + account.getBalance() + " BYN", font);
            document.add(balanceInfo);

            // Получить информацию о транзакциях для данного счета
            List<Transaction> accountTransactions = databaseReader.readClientTransactions(account.getId(), endDate.atStartOfDay());

            // Добавление таблицы с данными о транзакциях
            addTransactionTable(document, accountTransactions, font);

            document.close();
        } catch (DocumentException | IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Форматирует дату и время в строку в заданном формате "dd.MM.yyyy".
     *
     * @param dateTime Дата и время для форматирования.
     * @return Отформатированная строка.
     */
    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return dateTime.format(formatter);
    }

    /**
     * Добавляет таблицу с данными о транзакциях в документ.
     *
     * @param document     Документ, к которому добавляется таблица.
     * @param transactions Список транзакций для отображения в таблице.
     * @param font         Шрифт для текста в таблице.
     * @throws DocumentException В случае ошибки при работе с документом.
     */
    private void addTransactionTable(Document document, List<Transaction> transactions, Font font) throws DocumentException {
        PdfPTable table = new PdfPTable(3); // 3 столбца для даты, примечания и суммы
        table.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell(new Paragraph("Дата", font));
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("Тип транзакции", font));
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("Сумма", font));
        table.addCell(cell);

        for (Transaction transaction : transactions) {
            table.addCell(new Paragraph(formatDateTime(transaction.getTimestamp()), font));
            table.addCell(new Paragraph(transaction.getTransactionType(), font));
            table.addCell(new Paragraph(String.format("%.2f", transaction.getAmount()), font));
        }

        document.add(table);
    }
}
