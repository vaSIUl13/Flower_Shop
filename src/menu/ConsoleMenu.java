package menu;

import services.FlowerShop;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class ConsoleMenu {
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
            System.exit(0);
        });
    }

    public void run() {
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
                    menuItems.get(key).execute();
                } else {
                    System.out.println("Невірний вибір.");
                }
            } else {
                scanner.next();
            }
        }
    }
}