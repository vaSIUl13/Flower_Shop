package flowers;
import java.time.LocalDate;

public class Tulip extends Flower {
    private String petalShape;
    private boolean isDouble;

    public Tulip(String name, double price, double stemLength, String color, LocalDate freshnessDate, String petalShape, boolean isDouble) {
        super(name, price, stemLength, color, freshnessDate);
        this.petalShape = petalShape;
        this.isDouble = isDouble;
    }

    public String getPetalShape() { return petalShape; }
    public boolean isDouble() { return isDouble; }

    @Override
    public String getTypeName() { return "Тюльпан"; }

    @Override
    public String toString() {
        return "Тюльпан: " + super.toString();
    }
}