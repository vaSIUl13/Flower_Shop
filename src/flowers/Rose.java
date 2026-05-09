package flowers;
import java.time.LocalDate;

public class Rose extends Flower {
    private boolean hasThorns;
    private String budShape;

    public Rose(String name, double price, double stemLength, String color, LocalDate freshnessDate, boolean hasThorns, String budShape) {
        super(name, price, stemLength, color, freshnessDate);
        this.hasThorns = hasThorns;
        this.budShape = budShape;
    }

    public boolean isHasThorns() { return hasThorns; }
    public String getBudShape() { return budShape; }

    @Override
    public String getTypeName() { return "Троянда"; }

    @Override
    public String toString() {
        return "Троянда: " + super.toString() + (hasThorns ? ", з шипами" : "");
    }
}