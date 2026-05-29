import javafx.application.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui.FlowerShopApp;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("=== Flower Shop Application — старт ===");
        try {
            Application.launch(FlowerShopApp.class, args);
        } catch (Exception e) {
            logger.fatal("Критична помилка при запуску додатку!", e);
            throw e;
        } finally {
            logger.info("=== Flower Shop Application — завершення ===");
        }
    }
}