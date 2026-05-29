package flowers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Bouquet implements Serializable {
    private int id = -1;
    private String name;
    private List<Flower> flowers = new ArrayList<>();
    private List<Accessory> accessories = new ArrayList<>();

    public Bouquet(String name) {
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public void addFlower(Flower flower) { flowers.add(flower); }
    public void addAccessory(Accessory acc) { accessories.add(acc); }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Flower> getFlowers() { return flowers; }
    public List<Accessory> getAccessories() { return accessories; }

    public double calculateTotalPrice() {
        double flowersPrice = flowers.stream().mapToDouble(Flower::getPrice).sum();
        double accPrice = accessories.stream().mapToDouble(Accessory::getPrice).sum();
        return flowersPrice + accPrice;
    }

    public void sortFlowersByFreshness() {
        flowers.sort(Comparator.comparing(Flower::getFreshnessDate).reversed());
    }

    public List<Flower> findFlowersByStemLength(double min, double max) {
        return flowers.stream()
                .filter(f -> f.getStemLength() >= min && f.getStemLength() <= max)
                .collect(Collectors.toList());
    }

    public void removeFlower(int index) {
        if (index >= 0 && index < flowers.size()) {
            flowers.remove(index);
        }
    }

    public void removeAccessory(int index) {
        if (index >= 0 && index < accessories.size()) {
            accessories.remove(index);
        }
    }

    @Override
    public String toString() {
        return "Букет '" + name + "' (Квітів: " + flowers.size() + ", Ціна: " + calculateTotalPrice() + ")";
    }
}