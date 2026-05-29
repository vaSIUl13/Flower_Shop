package database;

import flowers.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final Logger logger = LogManager.getLogger(DatabaseManager.class);
    protected String getDbUrl() {
        return "jdbc:sqlite:flower_shop.db";
    }

    public DatabaseManager() {
        initDatabase();
    }

    protected Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(getDbUrl());
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    public void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS flowers ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "type TEXT NOT NULL, "
                    + "name TEXT NOT NULL, "
                    + "price REAL NOT NULL, "
                    + "stem_length REAL NOT NULL, "
                    + "color TEXT NOT NULL, "
                    + "freshness_date TEXT NOT NULL, "
                    + "has_thorns INTEGER, "
                    + "bud_shape TEXT, "
                    + "petal_shape TEXT, "
                    + "is_double INTEGER, "
                    + "core_size REAL, "
                    + "petal_count INTEGER, "
                    + "fragrance TEXT, "
                    + "bloom_stage TEXT, "
                    + "is_fragrant INTEGER, "
                    + "orchid_variety TEXT, "
                    + "is_epiphytic INTEGER)");

            stmt.execute("CREATE TABLE IF NOT EXISTS bouquets ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS bouquet_flowers ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "bouquet_id INTEGER NOT NULL, "
                    + "flower_id INTEGER NOT NULL, "
                    + "FOREIGN KEY (bouquet_id) REFERENCES bouquets(id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (flower_id) REFERENCES flowers(id) ON DELETE CASCADE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS accessories ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "bouquet_id INTEGER NOT NULL, "
                    + "name TEXT NOT NULL, "
                    + "price REAL NOT NULL, "
                    + "color TEXT DEFAULT '', "
                    + "FOREIGN KEY (bouquet_id) REFERENCES bouquets(id) ON DELETE CASCADE)");

            addColumnIfMissing(stmt, "flowers", "fragrance", "TEXT");
            addColumnIfMissing(stmt, "flowers", "bloom_stage", "TEXT");
            addColumnIfMissing(stmt, "flowers", "is_fragrant", "INTEGER");
            addColumnIfMissing(stmt, "flowers", "orchid_variety", "TEXT");
            addColumnIfMissing(stmt, "flowers", "is_epiphytic", "INTEGER");
            addColumnIfMissing(stmt, "accessories", "color", "TEXT DEFAULT ''");

            logger.info("База даних ініціалізована.");
        } catch (SQLException e) {
            logger.fatal("КРИТИЧНА ПОМИЛКА: не вдалося ініціалізувати базу даних!", e);
        }
    }

    private void addColumnIfMissing(Statement stmt, String table, String column, String type) {
        try {
            stmt.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
        } catch (SQLException ignored) {

        }
    }


    public int insertFlower(Flower flower) {
        String sql = "INSERT INTO flowers (type, name, price, stem_length, color, freshness_date, "
                + "has_thorns, bud_shape, petal_shape, is_double, core_size, petal_count, "
                + "fragrance, bloom_stage, is_fragrant, orchid_variety, is_epiphytic) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String type;
            if (flower instanceof Rose) type = "ROSE";
            else if (flower instanceof Tulip) type = "TULIP";
            else if (flower instanceof Chamomile) type = "CHAMOMILE";
            else if (flower instanceof Lily) type = "LILY";
            else if (flower instanceof Peony) type = "PEONY";
            else if (flower instanceof Orchid) type = "ORCHID";
            else type = "UNKNOWN";

            ps.setString(1, type);
            ps.setString(2, flower.getName());
            ps.setDouble(3, flower.getPrice());
            ps.setDouble(4, flower.getStemLength());
            ps.setString(5, flower.getColor());
            ps.setString(6, flower.getFreshnessDate().toString());

            for (int i = 7; i <= 17; i++) ps.setNull(i, Types.VARCHAR);

            if (flower instanceof Rose r) {
                ps.setInt(7, r.isHasThorns() ? 1 : 0);
                ps.setString(8, r.getBudShape());
            } else if (flower instanceof Tulip t) {
                ps.setString(9, t.getPetalShape());
                ps.setInt(10, t.isDouble() ? 1 : 0);
            } else if (flower instanceof Chamomile c) {
                ps.setDouble(11, c.getCoreSize());
                ps.setInt(12, c.getPetalCount());
            } else if (flower instanceof Lily l) {
                ps.setString(13, l.getFragrance());
                ps.setInt(12, l.getPetalCount());
            } else if (flower instanceof Peony p) {
                ps.setString(14, p.getBloomStage());
                ps.setInt(15, p.isFragrant() ? 1 : 0);
            } else if (flower instanceof Orchid o) {
                ps.setString(16, o.getVariety());
                ps.setInt(17, o.isEpiphytic() ? 1 : 0);
            }

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    flower.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            logger.error("Помилка вставки квітки '" + flower.getName() + "' в БД!", e);
        }
        return -1;
    }

    public List<Flower> getAllFlowers() {
        List<Flower> list = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM flowers")) {
            while (rs.next()) {
                Flower f = buildFlowerFromResultSet(rs);
                if (f != null) list.add(f);
            }
        } catch (SQLException e) {
            logger.error("Помилка читання квітів з БД!", e);
        }
        return list;
    }

    public void deleteFlower(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM flowers WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            logger.debug("Квітку з id=" + id + " видалено з БД.");
        } catch (SQLException e) {
            logger.error("Помилка видалення квітки id=" + id + " з БД!", e);
        }
    }


    public int insertBouquet(Bouquet bouquet) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO bouquets (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, bouquet.getName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    bouquet.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            logger.error("Помилка вставки букету!", e);
        }
        return -1;
    }

    public void deleteBouquet(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM bouquets WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            logger.debug("Букет з id=" + id + " видалено з БД.");
        } catch (SQLException e) {
            logger.error("Помилка видалення букету id=" + id + "!", e);
        }
    }

    public void updateBouquetName(int bouquetId, String newName) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE bouquets SET name = ? WHERE id = ?")) {
            ps.setString(1, newName);
            ps.setInt(2, bouquetId);
            ps.executeUpdate();
            logger.debug("Букет id=" + bouquetId + " перейменовано на '" + newName + "' в БД.");
        } catch (SQLException e) {
            logger.error("Помилка перейменування букету id=" + bouquetId + "!", e);
        }
    }

    public List<Bouquet> getAllBouquetsWithContents() {
        List<Bouquet> bouquets = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bouquets")) {
            while (rs.next()) {
                Bouquet b = new Bouquet(rs.getString("name"));
                b.setId(rs.getInt("id"));
                loadBouquetFlowers(conn, b);
                loadBouquetAccessories(conn, b);
                bouquets.add(b);
            }
        } catch (SQLException e) {
            logger.error("Помилка читання букетів!", e);
        }
        return bouquets;
    }

    private void loadBouquetFlowers(Connection conn, Bouquet b) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT f.* FROM flowers f JOIN bouquet_flowers bf ON f.id = bf.flower_id WHERE bf.bouquet_id = ? ORDER BY bf.id")) {
            ps.setInt(1, b.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Flower f = buildFlowerFromResultSet(rs);
                    if (f != null) b.addFlower(f);
                }
            }
        }
    }

    private void loadBouquetAccessories(Connection conn, Bouquet b) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM accessories WHERE bouquet_id = ?")) {
            ps.setInt(1, b.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String color = rs.getString("color");
                    Accessory acc = new Accessory(rs.getString("name"), rs.getDouble("price"),
                            color != null ? color : "");
                    acc.setId(rs.getInt("id"));
                    b.addAccessory(acc);
                }
            }
        }
    }


    public void addFlowerToBouquet(int bouquetId, int flowerId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO bouquet_flowers (bouquet_id, flower_id) VALUES (?, ?)")) {
            ps.setInt(1, bouquetId);
            ps.setInt(2, flowerId);
            ps.executeUpdate();
            logger.debug("Зв'язок квітка id=" + flowerId + " → букет id=" + bouquetId + " додано в БД.");
        } catch (SQLException e) {
            logger.error("Помилка додавання квітки id=" + flowerId + " до букету id=" + bouquetId + "!", e);
        }
    }

    public void removeOneFlowerFromBouquet(int bouquetId, int flowerId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM bouquet_flowers WHERE id = (SELECT id FROM bouquet_flowers WHERE bouquet_id = ? AND flower_id = ? LIMIT 1)")) {
            ps.setInt(1, bouquetId);
            ps.setInt(2, flowerId);
            ps.executeUpdate();
            logger.debug("Зв'язок квітка id=" + flowerId + " → букет id=" + bouquetId + " видалено з БД.");
        } catch (SQLException e) {
            logger.error("Помилка видалення квітки id=" + flowerId + " з букету id=" + bouquetId + "!", e);
        }
    }


    public int insertAccessory(int bouquetId, Accessory accessory) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO accessories (bouquet_id, name, price, color) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bouquetId);
            ps.setString(2, accessory.getName());
            ps.setDouble(3, accessory.getPrice());
            ps.setString(4, accessory.getColor() != null ? accessory.getColor() : "");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    accessory.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            logger.error("Помилка вставки аксесуару!", e);
        }
        return -1;
    }

    public void deleteAccessory(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM accessories WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            logger.debug("Аксесуар id=" + id + " видалено з БД.");
        } catch (SQLException e) {
            logger.error("Помилка видалення аксесуару id=" + id + "!", e);
        }
    }


    private Flower buildFlowerFromResultSet(ResultSet rs) throws SQLException {
        String type = rs.getString("type");
        String name = rs.getString("name");
        double price = rs.getDouble("price");
        double stem = rs.getDouble("stem_length");
        String color = rs.getString("color");
        LocalDate fresh = LocalDate.parse(rs.getString("freshness_date"));
        int id = rs.getInt("id");

        Flower flower;
        switch (type) {
            case "ROSE":
                flower = new Rose(name, price, stem, color, fresh,
                        rs.getInt("has_thorns") == 1,
                        rs.getString("bud_shape") != null ? rs.getString("bud_shape") : "Келих");
                break;
            case "TULIP":
                flower = new Tulip(name, price, stem, color, fresh,
                        rs.getString("petal_shape") != null ? rs.getString("petal_shape") : "Овальна",
                        rs.getInt("is_double") == 1);
                break;
            case "CHAMOMILE":
                flower = new Chamomile(name, price, stem, color, fresh,
                        rs.getDouble("core_size"),
                        rs.getInt("petal_count"));
                break;
            case "LILY":
                flower = new Lily(name, price, stem, color, fresh,
                        rs.getString("fragrance") != null ? rs.getString("fragrance") : "Легкий",
                        rs.getInt("petal_count"));
                break;
            case "PEONY":
                flower = new Peony(name, price, stem, color, fresh,
                        rs.getString("bloom_stage") != null ? rs.getString("bloom_stage") : "Відкритий",
                        rs.getInt("is_fragrant") == 1);
                break;
            case "ORCHID":
                flower = new Orchid(name, price, stem, color, fresh,
                        rs.getString("orchid_variety") != null ? rs.getString("orchid_variety") : "Фаленопсіс",
                        rs.getInt("is_epiphytic") == 1);
                break;
            default:
                logger.warn("Невідомий тип квітки: " + type);
                return null;
        }
        flower.setId(id);
        return flower;
    }
}
