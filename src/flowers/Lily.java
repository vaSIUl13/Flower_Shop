package flowers;
import java.time.LocalDate;

public class Lily extends Flower {
    private String fragrance;
    private int petalCount;

    public Lily(String name, double price, double stemLength, String color, LocalDate freshnessDate, String fragrance, int petalCount) {
        super(name, price, stemLength, color, freshnessDate);
        this.fragrance = fragrance;
        this.petalCount = petalCount;
    }

    public String getFragrance() { return fragrance; }
    public int getPetalCount() { return petalCount; }

    @Override
    public String getTypeName() { return "Лілія"; }

    @Override
    public String toString() {
        return "Лілія: " + super.toString() + ", аромат: " + fragrance;
    }
}
