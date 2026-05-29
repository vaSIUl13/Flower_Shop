package flowers;
import java.io.Serializable;

public class Accessory implements Serializable {
    private int id = -1;
    private String name;
    private double price;
    private String color;

    public Accessory(String name, double price, String color) {
        this.name = name;
        this.price = price;
        this.color = color;
    }

    public Accessory(String name, double price) {
        this(name, price, "");
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getPrice() { return price; }
    public String getName() { return name; }
    public String getColor() { return color; }

    @Override
    public String toString() {
        String colorPart = (color != null && !color.isEmpty()) ? ", " + color : "";
        return name + colorPart + " (" + price + " грн)";
    }
}