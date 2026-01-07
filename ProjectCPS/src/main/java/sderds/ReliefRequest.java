package sderds;

public class ReliefRequest {
    private final int foodUnits;
    private final int waterUnits;
    private final int medicalKits;

    public ReliefRequest(int foodUnits, int waterUnits, int medicalKits) {
        this.foodUnits = Math.max(0, foodUnits);
        this.waterUnits = Math.max(0, waterUnits);
        this.medicalKits = Math.max(0, medicalKits);
    }

    public int getFoodUnits() { return foodUnits; }
    public int getWaterUnits() { return waterUnits; }
    public int getMedicalKits() { return medicalKits; }
}
