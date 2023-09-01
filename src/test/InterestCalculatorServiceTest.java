import com.cleverbank.services.InterestCalculatorService;
import com.cleverbank.utils.AppConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class InterestCalculatorServiceTest {

    private InterestCalculatorService interestCalculatorService;
    private AppConfig appConfig;

    @BeforeEach
    void setUp() {
        // Создайте mock-объект AppConfig
        appConfig = mock(AppConfig.class);
        // Установите конфигурацию, которую ожидает ваш сервис
        when(appConfig.getInterestRate()).thenReturn(0.01); // Например, 1% годовых

        // Создайте экземпляр сервиса с mock-конфигурацией
        interestCalculatorService = new InterestCalculatorService(appConfig);
    }

    @Test
    void testCalculateInterest() {
        // Для теста calculateInterest нужно обеспечить доступ к базе данных,
        // поэтому здесь можно использовать интеграционный тест.
        // Тем не менее, вы можете проверить, что метод вызывается без исключений.
        interestCalculatorService.calculateInterest();
    }
}
