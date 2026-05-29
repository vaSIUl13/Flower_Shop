package services;

import database.DatabaseManager;
import flowers.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Сервіс квіткового магазину.
 * Зберігає каталог квітів та букети у базі даних SQLite.
 */
public class FlowerShop {
    private static final Logger logger = LogManager.getLogger(FlowerShop.class);

    private final DatabaseManager db;
    private List<Flower> catalog = new ArrayList<>();
    private List<Bouquet> bouquets = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    public FlowerShop() {
        db = new DatabaseManager();
        loadFromDatabase();
        if (catalog.isEmpty()) {
            initRichCatalog();
        }
    }

    private void loadFromDatabase() {
        catalog = db.getAllFlowers();
        bouquets = db.getAllBouquetsWithContents();
        logger.info("Завантажено з БД: квітів=" + catalog.size() + ", букетів=" + bouquets.size());
    }

    /**
     * Ініціалізує багатий каталог з 22 квітками різних типів.
     */
    private void initRichCatalog() {
        LocalDate today = LocalDate.now();

        // === ТРОЯНДИ ===
        addFlowerSilent(new Rose("Троянда Гран-Прі", 80, 60, "Червоний", today, true, "Келих"));
        addFlowerSilent(new Rose("Троянда Фрідом", 95, 65, "Бордовий", today, true, "Класичний"));
        addFlowerSilent(new Rose("Троянда Аваланч", 90, 55, "Білий", today.minusDays(1), false, "Півонієвидний"));
        addFlowerSilent(new Rose("Троянда Мондіаль", 85, 50, "Кремовий", today, false, "Келих"));
        addFlowerSilent(new Rose("Троянда Пінк Флойд", 88, 55, "Рожевий", today, true, "Бокаловидний"));

        // === ТЮЛЬПАНИ ===
        addFlowerSilent(new Tulip("Тюльпан Голд Парад", 40, 35, "Жовтий", today, "Овальна", false));
        addFlowerSilent(new Tulip("Тюльпан Ред Імпрешн", 45, 40, "Червоний", today, "Витягнута", false));
        addFlowerSilent(new Tulip("Тюльпан Перпл Прінс", 50, 38, "Фіолетовий", today.minusDays(1), "Округла", true));
        addFlowerSilent(new Tulip("Тюльпан Уайт Дрім", 42, 36, "Білий", today, "Овальна", false));

        // === РОМАШКИ ===
        addFlowerSilent(new Chamomile("Ромашка Польова", 25, 25, "Білий", today.minusDays(1), 1.5, 18));
        addFlowerSilent(new Chamomile("Ромашка Садова", 30, 35, "Білий", today, 2.0, 24));
        addFlowerSilent(new Chamomile("Ромашка Гігантська", 35, 45, "Білий", today, 3.0, 30));

        // === ЛІЛІЇ ===
        addFlowerSilent(new Lily("Лілія Касабланка", 120, 70, "Білий", today, "Інтенсивний", 6));
        addFlowerSilent(new Lily("Лілія Стар Гейзер", 130, 75, "Рожевий", today, "Солодкий", 6));
        addFlowerSilent(new Lily("Лілія Азіатська", 95, 60, "Помаранчевий", today.minusDays(1), "Легкий", 6));
        addFlowerSilent(new Lily("Лілія Тигрова", 85, 55, "Помаранчевий", today, "Ніжний", 6));

        // === ПІВОНІЇ ===
        addFlowerSilent(new Peony("Півонія Сара Бернар", 150, 50, "Рожевий", today, "Відкритий", true));
        addFlowerSilent(new Peony("Півонія Фестіва Максіма", 160, 55, "Білий", today, "Напіввідкритий", true));
        addFlowerSilent(new Peony("Півонія Ред Чарм", 155, 48, "Червоний", today.minusDays(1), "Бутон", true));

        // === ОРХІДЕЇ ===
        addFlowerSilent(new Orchid("Орхідея Фаленопсіс", 200, 45, "Білий", today, "Фаленопсіс", true));
        addFlowerSilent(new Orchid("Орхідея Дендробіум", 180, 50, "Фіолетовий", today, "Дендробіум", true));
        addFlowerSilent(new Orchid("Орхідея Цимбідіум", 220, 60, "Жовтий", today, "Цимбідіум", false));

        logger.info("Ініціалізовано багатий каталог: " + catalog.size() + " квітів.");
    }

    /** Додає квітку без логування (для ініціалізації) */
    private void addFlowerSilent(Flower flower) {
        db.insertFlower(flower);
        catalog.add(flower);
    }

    // ==================== Консольні методи ====================

    public void viewCatalog() {
        System.out.println("=== Каталог квітів ===");
        if (catalog.isEmpty()) {
            System.out.println("Каталог порожній.");
        } else {
            for (int i = 0; i < catalog.size(); i++) {
                System.out.println((i + 1) + ". " + catalog.get(i));
            }
        }
    }

    public void addFlowerToCatalog() {
        System.out.println("Оберіть тип: 1.Троянда 2.Тюльпан 3.Ромашка 4.Лілія 5.Півонія 6.Орхідея");
        int type = readInt("Тип: ");
        String name = readString("Назва: ");
        double price = readDouble("Ціна: ");
        double length = readDouble("Стебло: ");
        String color = readString("Колір: ");

        Flower flower = switch (type) {
            case 1 -> new Rose(name, price, length, color, LocalDate.now(), true, "Келих");
            case 2 -> new Tulip(name, price, length, color, LocalDate.now(), "Овальна", false);
            case 3 -> new Chamomile(name, price, length, color, LocalDate.now(), 1.5, 18);
            case 4 -> new Lily(name, price, length, color, LocalDate.now(), "Легкий", 6);
            case 5 -> new Peony(name, price, length, color, LocalDate.now(), "Відкритий", true);
            case 6 -> new Orchid(name, price, length, color, LocalDate.now(), "Фаленопсіс", true);
            default -> new Rose(name, price, length, color, LocalDate.now(), true, "Келих");
        };
        addFlower(flower);
        System.out.println("Квітку додано.");
    }

    public void createBouquet() {
        String name = readString("Назва букету: ");
        createNewBouquet(name);
        System.out.println("Букет створено.");
    }

    public void editBouquet() {
        if (bouquets.isEmpty()) { System.out.println("Немає букетів."); return; }
        for (int i = 0; i < bouquets.size(); i++)
            System.out.println((i + 1) + ". " + bouquets.get(i).getName());
        int bi = readInt("Номер: ") - 1;
        if (bi < 0 || bi >= bouquets.size()) return;
        Bouquet b = bouquets.get(bi);

        boolean adding = true;
        while (adding) {
            viewCatalog();
            System.out.println("0 -> Завершити");
            int fi = readInt("Квітка: ");
            if (fi == 0) adding = false;
            else if (fi > 0 && fi <= catalog.size()) {
                int qty = readInt("Кількість: ");
                for (int k = 0; k < qty; k++) addFlowerToBouquet(b, catalog.get(fi - 1));
            }
        }
        while (true) {
            System.out.println("1.Аксесуар 0.Вихід");
            if (readInt("") == 0) break;
            addAccessoryToBouquet(b, new Accessory(readString("Назва: "), readDouble("Ціна: ")));
        }
    }

    public void viewAllBouquets() {
        for (Bouquet b : bouquets) {
            System.out.println(b);
            b.getFlowers().forEach(f -> System.out.println(" - " + f));
            b.getAccessories().forEach(a -> System.out.println(" + " + a));
        }
    }

    public void sortBouquet() {
        if (bouquets.isEmpty()) return;
        int idx = readInt("Букет (1-" + bouquets.size() + "): ") - 1;
        if (idx >= 0 && idx < bouquets.size()) {
            bouquets.get(idx).sortFlowersByFreshness();
            System.out.println("Відсортовано.");
        }
    }

    public void saveCatalogToFile() {
        logger.info("Дані зберігаються у БД автоматично.");
    }

    // ==================== GUI-сумісні методи ====================

    public List<Flower> getCatalog() { return catalog; }
    public List<Bouquet> getBouquets() { return bouquets; }

    public void addFlower(Flower flower) {
        db.insertFlower(flower);
        catalog.add(flower);
        logger.info("Додано квітку: " + flower.getName());
    }

    public void removeFlower(int index) {
        if (index >= 0 && index < catalog.size()) {
            Flower f = catalog.remove(index);
            db.deleteFlower(f.getId());
            logger.info("Видалено квітку: " + f.getName());
        }
    }

    public Bouquet createNewBouquet(String name) {
        Bouquet b = new Bouquet(name);
        db.insertBouquet(b);
        bouquets.add(b);
        logger.info("Створено букет: " + name);
        return b;
    }

    public void removeBouquet(int index) {
        if (index >= 0 && index < bouquets.size()) {
            Bouquet b = bouquets.remove(index);
            db.deleteBouquet(b.getId());
            logger.info("Видалено букет: " + b.getName());
        }
    }

    public void renameBouquet(Bouquet bouquet, String newName) {
        String oldName = bouquet.getName();
        bouquet.setName(newName);
        db.updateBouquetName(bouquet.getId(), newName);
        logger.info("Перейменовано букет: \"" + oldName + "\" → \"" + newName + "\"");
    }

    public void addFlowerToBouquet(Bouquet bouquet, Flower flower) {
        db.addFlowerToBouquet(bouquet.getId(), flower.getId());
        bouquet.addFlower(flower);
        logger.info("Додано \"" + flower.getName() + "\" → \"" + bouquet.getName() + "\"");
    }

    public void removeFlowerFromBouquet(Bouquet bouquet, int idx) {
        if (idx >= 0 && idx < bouquet.getFlowers().size()) {
            Flower f = bouquet.getFlowers().get(idx);
            db.removeOneFlowerFromBouquet(bouquet.getId(), f.getId());
            bouquet.removeFlower(idx);
        }
    }

    public void addAccessoryToBouquet(Bouquet bouquet, Accessory acc) {
        db.insertAccessory(bouquet.getId(), acc);
        bouquet.addAccessory(acc);
        logger.info("Додано аксесуар \"" + acc.getName() + "\" → \"" + bouquet.getName() + "\"");
    }

    public void removeAccessoryFromBouquet(Bouquet bouquet, int idx) {
        if (idx >= 0 && idx < bouquet.getAccessories().size()) {
            Accessory a = bouquet.getAccessories().get(idx);
            db.deleteAccessory(a.getId());
            bouquet.removeAccessory(idx);
        }
    }

    // ==================== Допоміжні ====================

    private int readInt(String p) {
        System.out.print(p);
        while (!scanner.hasNextInt()) scanner.next();
        int r = scanner.nextInt(); scanner.nextLine(); return r;
    }
    private double readDouble(String p) {
        System.out.print(p);
        while (!scanner.hasNextDouble()) scanner.next();
        double r = scanner.nextDouble(); scanner.nextLine(); return r;
    }
    private String readString(String p) {
        System.out.print(p);
        return scanner.nextLine();
    }
}