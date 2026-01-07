package sderds;

public class ReliefHub extends Zone {
    private int foodUnits;
    private int waterUnits;
    private int medicalKits;

    public ReliefHub(String id, String name, int foodUnits, int waterUnits, int medicalKits, double riskLevel) {
        super(id, name, ZoneType.HUB, 0, riskLevel);
        this.foodUnits = Math.max(0, foodUnits);
        this.waterUnits = Math.max(0, waterUnits);
        this.medicalKits = Math.max(0, medicalKits);
    }

    public boolean hasResources() {
        return foodUnits > 0 || waterUnits > 0 || medicalKits > 0;
    }

    public void allocate(ReliefRequest req) {
        if (req == null) return;
        int f = Math.min(foodUnits, req.getFoodUnits());
        int w = Math.min(waterUnits, req.getWaterUnits());
        int m = Math.min(medicalKits, req.getMedicalKits());
        foodUnits -= f;
        waterUnits -= w;
        medicalKits -= m;
    }

    public void restock(int food, int water, int kits) {
        foodUnits += Math.max(0, food);
        waterUnits += Math.max(0, water);
        medicalKits += Math.max(0, kits);
    }

    public int getFoodUnits() { return foodUnits; }
    public int getWaterUnits() { return waterUnits; }
    public int getMedicalKits() { return medicalKits; }

    @Override
    public String toString() {
        return "ReliefHub{" +
                "name=" + getName() +
                ", food=" + foodUnits +
                ", water=" + waterUnits +
                ", kits=" + medicalKits +
                ", risk=" + String.format("%.2f", getRiskLevel()) +
                ", unsafe=" + isUnsafe() +
                '}';
    }
}
