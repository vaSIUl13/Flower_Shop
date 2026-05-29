package flowers;

import java.io.Serializable;
import java.time.LocalDate;

public abstract class Flower implements Serializable {
    private int id = -1;
    private String name;
    private double price;
    private double stemLength;
    private String color;
    private LocalDate freshnessDate;

    public Flower(String name, double price, double stemLength, String color, LocalDate freshnessDate) {
        this.name = name;
        this.price = price;
        this.stemLength = stemLength;
        this.color = color;
        this.freshnessDate = freshnessDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getStemLength() { return stemLength; }
    public String getColor() { return color; }
    public LocalDate getFreshnessDate() { return freshnessDate; }

    public abstract String getTypeName();

    @Override
    public String toString() {
        return String.format("%s (%.2f грн, %s, %.0f см)", name, price, color, stemLength);
    }
}