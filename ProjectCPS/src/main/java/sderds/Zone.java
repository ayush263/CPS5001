package sderds;

public class Zone {
    private final String id;
    private final String name;
    private final ZoneType type;
    private int population;         
    private double riskLevel;       // 0.0 - 1.0

    public Zone(String id, String name, ZoneType type, int population, double riskLevel) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.population = population;
        this.riskLevel = clamp(riskLevel);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public ZoneType getType() { return type; }

    public int getPopulation() { return population; }
    public void setPopulation(int population) { this.population = Math.max(0, population); }

    public double getRiskLevel() { return riskLevel; }

    public void updateRisk(double risk) {
        this.riskLevel = clamp(risk);
    }

    public boolean isUnsafe() {
        return riskLevel >= 0.70;
    }

    private double clamp(double v) {
        if (v < 0.0) return 0.0;
        if (v > 1.0) return 1.0;
        return v;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", population=" + population +
                ", risk=" + String.format("%.2f", riskLevel) +
                ", unsafe=" + isUnsafe() +
                '}';
    }
}
