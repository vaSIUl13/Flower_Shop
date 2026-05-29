package menu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.FlowerShop;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class ConsoleMenu {
    private static final Logger logger = LogManager.getLogger(ConsoleMenu.class);

    private Map<String, Command> menuItems = new LinkedHashMap<>();
    private FlowerShop shop;
    private Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(FlowerShop shop) {
        this.shop = shop;

        // Реєстрація команд
        menuItems.put("Переглянути каталог", shop::viewCatalog);
        menuItems.put("Додати квітку", shop::addFlowerToCatalog);
        menuItems.put("Створити букет", shop::createBouquet);
        menuItems.put("Переглянути букети", shop::viewAllBouquets);
        menuItems.put("Редагувати букет", shop::editBouquet);
        menuItems.put("Сортувати букет", shop::sortBouquet);
        menuItems.put("Зберегти і вийти", () -> {
            shop.saveCatalogToFile();
            logger.info("Користувач завершив роботу через консольне меню.");
            System.exit(0);
        });

        logger.info("Консольне меню ініціалізовано з " + menuItems.size() + " командами.");
    }

    public void run() {
        logger.info("Запущено консольний режим роботи.");
        while (true) {
            System.out.println("\n=== МЕНЮ МАГАЗИНУ ===");
            int i = 1;
            for (String key : menuItems.keySet()) {
                System.out.println(i++ + ". " + key);
            }
            System.out.print("Ваш вибір: ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                if (choice > 0 && choice <= menuItems.size()) {
                    String key = (String) menuItems.keySet().toArray()[choice - 1];
                    logger.info("Користувач обрав пункт меню: " + choice + " (" + key + ")");
                    try {
                        menuItems.get(key).execute();
                    } catch (Exception e) {
                        logger.error("Помилка при виконанні команди '" + key + "'!", e);
                        System.out.println("Сталася помилка: " + e.getMessage());
                    }
                } else {
                    logger.warn("Невірний вибір пункту меню: " + choice);
                    System.out.println("Невірний вибір.");
                }
            } else {
                scanner.next();
            }
        }
    }
}