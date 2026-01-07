package sderds;

import java.util.*;

public class EvacuationManager {
    private final DisasterNetwork network;
    private final RoutePlanner routePlanner;
    private final RiskPropagationEngine riskEngine;
    private final ReliefManager reliefManager;

    private final PriorityQueue<EvacuationTask> taskQueue = new PriorityQueue<>();

    public EvacuationManager(DisasterNetwork network,
                             RoutePlanner routePlanner,
                             RiskPropagationEngine riskEngine,
                             ReliefManager reliefManager) {
        this.network = network;
        this.routePlanner = routePlanner;
        this.riskEngine = riskEngine;
        this.reliefManager = reliefManager;
    }

    public void planEvacuation() {
        taskQueue.clear();
        List<Zone> residentials = network.getZonesByType(ZoneType.RESIDENTIAL);

        int idx = 1;
        for (Zone z : residentials) {
            if (!z.isUnsafe()) continue;
            int prio = computePriority(z);
            int people = z.getPopulation();
            taskQueue.add(new EvacuationTask("EV" + (idx++), z, people, prio));
        }
    }

    public void handleRiskUpdate() {
        // rebuild tasks each timestep based on updated risk
        planEvacuation();
    }

    public void executeStep() {
        if (taskQueue.isEmpty()) {
            System.out.println("[EVAC] No evacuation tasks at timestep " + riskEngine.getTimestep());
            return;
        }

        int processed = 0;
        while (!taskQueue.isEmpty() && processed < 2) { // process 2 tasks per timestep
            EvacuationTask task = taskQueue.poll();
            boolean ok = evacuateZone(task.getSourceZone());
            if (!ok) {
                System.out.println("[EVAC] Could not evacuate " + task.getSourceZone().getName()
                        + " (no safe shelter path/capacity)");
            }
            processed++;
        }

        // distribute relief after evacuation attempts
        reliefManager.distributeRelief();
    }

    public boolean evacuateZone(Zone zone) {
        if (zone.getType() != ZoneType.RESIDENTIAL) return false;
        if (!zone.isUnsafe()) return false;
        if (zone.getPopulation() <= 0) {
            System.out.println("[EVAC] " + zone.getName() + " already evacuated.");
            return true;
        }

        Shelter bestShelter = findBestShelter(zone);
        if (bestShelter == null) return false;

        Route route = routePlanner.findSafestPath(zone, bestShelter, OptimizationMetric.RISK_AWARE);
        if (route == null) return false;

        int people = zone.getPopulation();
        int moved = Math.min(people, bestShelter.remainingCapacity());
        if (moved <= 0) return false;

        bestShelter.admit(moved);
        zone.setPopulation(people - moved);

        System.out.println("[EVAC] Moved " + moved + " people from " + zone.getName()
                + " to " + bestShelter.getName() + " via " + route);

        return true;
    }

    public List<Zone> getUnsafeZones() {
        List<Zone> out = new ArrayList<>();
        for (Zone z : network.getAllZones()) {
            if (z.isUnsafe()) out.add(z);
        }
        return out;
    }

    private Shelter findBestShelter(Zone from) {
        List<Zone> shelters = network.getZonesByType(ZoneType.SHELTER);

        Shelter best = null;
        double bestScore = Double.POSITIVE_INFINITY;

        for (Zone s : shelters) {
            Shelter shelter = (Shelter) s;

            // shelter must be safe enough and have capacity
            if (shelter.isUnsafe()) continue;
            if (shelter.remainingCapacity() <= 0) continue;

            Route r = routePlanner.findSafestPath(from, shelter, OptimizationMetric.RISK_AWARE);
            if (r == null) continue;

            // score combines time and risk
            double score = r.getTotalTravelTimeMin() + (r.getCumulativeRisk() * 10.0);
            if (score < bestScore) {
                bestScore = score;
                best = shelter;
            }
        }
        return best;
    }

    private int computePriority(Zone z) {
        // higher risk and higher population = higher priority
        double risk = z.getRiskLevel();
        int pop = z.getPopulation();
        int prio = (int) Math.round(risk * 100.0) + Math.min(100, pop / 10);
        return prio;
    }
}
