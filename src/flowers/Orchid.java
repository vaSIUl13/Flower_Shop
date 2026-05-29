package flowers;
import java.time.LocalDate;

public class Orchid extends Flower {
    private String variety;
    private boolean isEpiphytic;

    public Orchid(String name, double price, double stemLength, String color, LocalDate freshnessDate, String variety, boolean isEpiphytic) {
        super(name, price, stemLength, color, freshnessDate);
        this.variety = variety;
        this.isEpiphytic = isEpiphytic;
    }

    public String getVariety() { return variety; }
    public boolean isEpiphytic() { return isEpiphytic; }

    @Override
    public String getTypeName() { return "Орхідея"; }

    @Override
    public String toString() {
        return "Орхідея: " + super.toString() + " (" + variety + ")";
    }
}
