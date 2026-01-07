package sderds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Route {
    private final List<Zone> path;
    private final double totalDistanceKm;
    private final double totalTravelTimeMin;
    private final double cumulativeRisk;

    public Route(List<Zone> path, double totalDistanceKm, double totalTravelTimeMin, double cumulativeRisk) {
        this.path = new ArrayList<>(path);
        this.totalDistanceKm = totalDistanceKm;
        this.totalTravelTimeMin = totalTravelTimeMin;
        this.cumulativeRisk = cumulativeRisk;
    }

    public List<Zone> getPath() { return Collections.unmodifiableList(path); }
    public double getTotalDistanceKm() { return totalDistanceKm; }
    public double getTotalTravelTimeMin() { return totalTravelTimeMin; }
    public double getCumulativeRisk() { return cumulativeRisk; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Route: ");
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).getName());
            if (i < path.size() - 1) sb.append(" -> ");
        }
        sb.append(String.format(" [dist=%.1fkm, time=%.1fmin, risk=%.2f]",
                totalDistanceKm, totalTravelTimeMin, cumulativeRisk));
        return sb.toString();
    }
}
