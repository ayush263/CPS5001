package sderds;

public class Road {
    private final Zone from;
    private final Zone to;
    private final double distanceKm;
    private final double baseTravelTimeMin;
    private double risk; // 0.0 - 1.0

    public Road(Zone from, Zone to, double distanceKm, double baseTravelTimeMin, double risk) {
        this.from = from;
        this.to = to;
        this.distanceKm = Math.max(0.0, distanceKm);
        this.baseTravelTimeMin = Math.max(0.0, baseTravelTimeMin);
        this.risk = clamp(risk);
    }

    public Zone getFrom() { return from; }
    public Zone getTo() { return to; }

    public double getDistanceKm() { return distanceKm; }
    public double getBaseTravelTimeMin() { return baseTravelTimeMin; }

    public double getRisk() { return risk; }
    public void setRisk(double risk) { this.risk = clamp(risk); }

    public double effectiveCost(OptimizationMetric metric) {
        return switch (metric) {
            case DISTANCE -> distanceKm;
            case TRAVEL_TIME -> baseTravelTimeMin;
            case RISK_AWARE -> baseTravelTimeMin * (1.0 + risk * 3.0); // risk penalty
        };
    }

    private double clamp(double v) {
        if (v < 0.0) return 0.0;
        if (v > 1.0) return 1.0;
        return v;
    }

    @Override
    public String toString() {
        return from.getName() + "->" + to.getName() +
                " dist=" + String.format("%.1f", distanceKm) +
                " time=" + String.format("%.1f", baseTravelTimeMin) +
                " risk=" + String.format("%.2f", risk);
    }
}
