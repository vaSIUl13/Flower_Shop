package flowers;
import java.time.LocalDate;

public class Chamomile extends Flower {
    private double coreSize;
    private int petalCount;

    public Chamomile(String name, double price, double stemLength, String color, LocalDate freshnessDate, double coreSize, int petalCount) {
        super(name, price, stemLength, color, freshnessDate);
        this.coreSize = coreSize;
        this.petalCount = petalCount;
    }

    public double getCoreSize() { return coreSize; }
    public int getPetalCount() { return petalCount; }

    @Override
    public String getTypeName() { return "Ромашка"; }

    @Override
    public String toString() {
        return "Ромашка: " + super.toString();
    }
}