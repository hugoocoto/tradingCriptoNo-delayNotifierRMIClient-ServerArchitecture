import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;

public class Parser {
    private String BOLSA_URL = "https://www.bolsasymercados.es/bme-exchange/es/Mercados-y-Cotizaciones/Acciones/Mercado-Continuo/Precios/ibex-35-ES0SI0000005";
    private HashMap<String, Float> acciones = new HashMap<>();
    private WebDriver driver;

    public void close() {
        driver.close();
    }

    public HashMap<String, Float> parse() {
        // Conecta a la página o carga HTML desde un String
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            driver = new ChromeDriver(options);
            driver.get(BOLSA_URL);
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement tabla = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("/html/body/main/div/div/div/div[2]/div[5]/table")));

        for (WebElement fila : tabla.findElements(By.xpath(".//tbody/tr"))) {
            List<WebElement> celdas = fila.findElements(By.tagName("td"));
            try {
                acciones.put(
                        celdas.get(0).getText(),
                        Float.parseFloat(celdas.get(1).getText().replace(",", ".")));
            } catch (Exception e) {
                System.out.println("Can not parse `" + celdas.get(1).getText() + "` as Float");
            }
        }

        return acciones;
    }
}
