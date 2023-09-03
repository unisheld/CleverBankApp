package com.cleverbank.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * Класс для загрузки конфигурационных данных из YAML файла.
 */
public class AppConfig {
    private double interestRate;
    private double checkIntervalMinutes;
    private int interestCalculationMonth;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;

    public int clientId;

    /**
     * Получить процентную ставку.
     *
     * @return Процентная ставка.
     */
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * Получить интервал проверки для начисления процентов в минутах.
     *
     * @return Интервал проверки в минутах.
     */
    public double getCheckIntervalMinutes() {
        return checkIntervalMinutes;
    }

    /**
     * Получить месяц для начисления процентов.
     *
     * @return Месяц для начисления процентов.
     */
    public int getInterestCalculationMonth() {
        return interestCalculationMonth;
    }

    /**
     * Получить URL базы данных.
     *
     * @return URL базы данных.
     */
    public String getDbUrl() {
        return dbUrl;
    }

    /**
     * Получить имя пользователя для подключения к базе данных.
     *
     * @return Имя пользователя для подключения к базе данных.
     */
    public String getDbUsername() {
        return dbUsername;
    }

    /**
     * Получить пароль для подключения к базе данных.
     *
     * @return Пароль для подключения к базе данных.
     */
    public String getDbPassword() {
        return dbPassword;
    }

    /**
     * Получить идентификатор клиента.
     *
     * @return Идентификатор клиента.
     */
    public int getClientId() {
        return clientId;
    }

    /**
     * Загружает конфигурацию из YAML файла.
     *
     * @param configFile Путь к конфигурационному файлу YAML.
     */
    public void loadConfig(String configFile) {
        try (FileInputStream fis = new FileInputStream(configFile)) {
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(fis);

            Map<String, Object> bankConfig = (Map<String, Object>) config.get("bank");
            clientId = getIntValue(bankConfig, "clientId");
            interestRate = getDoubleValue(bankConfig, "interestRate");
            interestCalculationMonth = getIntValue(bankConfig, "interestCalculationMonth");

            Map<String, Object> scheduleConfig = (Map<String, Object>) bankConfig.get("schedule");
            checkIntervalMinutes = getDoubleValue(scheduleConfig, "checkIntervalMinutes");

            Map<String, Object> dbConfig = (Map<String, Object>) bankConfig.get("database");
            dbUrl = getStringValue(dbConfig, "url");
            dbUsername = getStringValue(dbConfig, "username");
            dbPassword = getStringValue(dbConfig, "password");
        } catch (FileNotFoundException e) {
            System.err.println("Config file not found: " + configFile);
        } catch (IOException e) {
            System.err.println("Error reading config file: " + e.getMessage());
        }
    }

    // Вспомогательные методы для извлечения значений из Map

    private double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return (value instanceof Number) ? ((Number) value).doubleValue() : 0.0;
    }

    private int getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return (value instanceof Number) ? ((Number) value).intValue() : 0;
    }

    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return (value instanceof String) ? (String) value : "";
    }
}
