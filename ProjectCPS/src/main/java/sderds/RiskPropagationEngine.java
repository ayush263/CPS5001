package sderds;

import java.util.List;

public class RiskPropagationEngine {
    private final DisasterNetwork network;
    private int timestep = 0;

    public RiskPropagationEngine(DisasterNetwork network) {
        this.network = network;
    }

    public void seedDisaster(Zone origin, double initialRisk) {
        origin.updateRisk(Math.max(origin.getRiskLevel(), initialRisk));
    }

    public void stepSpread() {
        timestep++;

        // 1) Spread risk from unsafe zones to neighbours
        for (Zone z : network.getAllZones()) {
            if (!z.isUnsafe()) continue;

            List<Road> neighbours = network.getNeighbours(z);
            for (Road r : neighbours) {
                Zone nb = r.getTo();

//                // increase neighbour zone risk slightly
//                double newRisk = nb.getRiskLevel() + 0.05;
//                nb.updateRisk(newRisk);
//
//                // increase road risk more strongly
//                r.setRisk(Math.min(1.0, r.getRisk() + 0.08));
                double zoneBoost = 0.07;   // faster spread to zones
                double roadBoost = 0.10;   // faster increase on roads

                double newRisk = nb.getRiskLevel() + zoneBoost;
                nb.updateRisk(newRisk);

                r.setRisk(Math.min(1.0, r.getRisk() + roadBoost));
            }
        }

        // 2) Increase road risk near zones that are close to unsafe
        increaseRoadRiskNearUnsafeZones();
    }

    public void increaseRoadRiskNearUnsafeZones() {
        for (Zone z : network.getAllZones()) {
            if (z.getRiskLevel() < 0.55) continue; 

            for (Road r : network.getNeighbours(z)) {
                r.setRisk(Math.min(1.0, r.getRisk() + 0.05));
            }
        }
    }

    public int getTimestep() {
        return timestep;
    }
}
