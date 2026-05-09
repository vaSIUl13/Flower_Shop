package flowers;
import java.time.LocalDate;

/**
 * Півонія — пишна квітка з різними стадіями цвітіння.
 */
public class Peony extends Flower {
    private String bloomStage;
    private boolean isFragrant;

    public Peony(String name, double price, double stemLength, String color, LocalDate freshnessDate, String bloomStage, boolean isFragrant) {
        super(name, price, stemLength, color, freshnessDate);
        this.bloomStage = bloomStage;
        this.isFragrant = isFragrant;
    }

    public String getBloomStage() { return bloomStage; }
    public boolean isFragrant() { return isFragrant; }

    @Override
    public String getTypeName() { return "Півонія"; }

    @Override
    public String toString() {
        return "Півонія: " + super.toString() + " [" + bloomStage + "]";
    }
}
