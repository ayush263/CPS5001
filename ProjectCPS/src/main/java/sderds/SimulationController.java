package sderds;

import java.util.Random;

public class SimulationController {
    private final DisasterNetwork network = new DisasterNetwork();
    private final RoutePlanner routePlanner = new RoutePlanner(network);
    private final RiskPropagationEngine riskEngine = new RiskPropagationEngine(network);
    private final ReliefManager reliefManager = new ReliefManager(network, routePlanner);
    private final EvacuationManager evacuationManager =
            new EvacuationManager(network, routePlanner, riskEngine, reliefManager);

    private final Random random = new Random();

    public void setupScenario() {
        // Residential zones (populated)
        Zone r1 = new Zone("R1", "Riverside District", ZoneType.RESIDENTIAL, 420, 0.20);
        Zone r2 = new Zone("R2", "Hillview Suburb", ZoneType.RESIDENTIAL, 280, 0.15);
        Zone r3 = new Zone("R3", "Old Town", ZoneType.RESIDENTIAL, 350, 0.25);

        // Shelters
        Shelter s1 = new Shelter("S1", "Community Shelter A", 500, 0.10);
        Shelter s2 = new Shelter("S2", "School Shelter B", 300, 0.12);

        // Relief hubs
        ReliefHub h1 = new ReliefHub("H1", "Relief Hub North", 500, 500, 80, 0.10);
        ReliefHub h2 = new ReliefHub("H2", "Relief Hub South", 350, 350, 50, 0.12);

        network.addZone(r1);
        network.addZone(r2);
        network.addZone(r3);
        network.addZone(s1);
        network.addZone(s2);
        network.addZone(h1);
        network.addZone(h2);

        // Roads (undirected)
        network.addRoad(r1, r2, 6, 10);
        network.addRoad(r2, r3, 5, 9);
        network.addRoad(r1, r3, 7, 12);

        network.addRoad(r1, s1, 4, 7);
        network.addRoad(r2, s1, 3, 6);
        network.addRoad(r3, s2, 4, 7);

        network.addRoad(h1, r1, 5, 9);
        network.addRoad(h1, r2, 6, 10);
        network.addRoad(h2, r3, 5, 9);
        network.addRoad(h2, r2, 7, 12);

        // Seed a disaster near Riverside
        riskEngine.seedDisaster(r1, 0.75);
    }

    public void runSimulation(int steps) {
        System.out.println("=== SDERDS: Disaster Evacuation & Relief Simulation ===");
        for (int i = 1; i <= steps; i++) {
            System.out.println("\n--- TIMESTEP " + i + " ---");

            // risk spread
            riskEngine.stepSpread();

            // optional dynamic events
            injectDynamicEvent(i);

            // evacuation decisions based on updated risk
            evacuationManager.handleRiskUpdate();
            evacuationManager.executeStep();

            // summary snapshot
            printSnapshot();
        }
        System.out.println("\n=== Simulation complete ===");
    }

    public void injectDynamicEvent() {
        injectDynamicEvent(riskEngine.getTimestep());
    }

    public void injectDynamicEvent(int t) {
        // Every 3 timesteps: randomly spike road risk (simulated blockage)
        if (t % 3 != 0) return;

        // pick a deterministic road to demonstrate dynamic update
        network.updateRoadRisk("R2", "R3", 0.98);
        System.out.println("[EVENT] Road blockage detected: Hillview Suburb <-> Old Town (risk set to 0.98)");
    }

    private void printSnapshot() {
        System.out.println("\n[SNAPSHOT] Zones:");
        for (Zone z : network.getAllZones()) {
            System.out.println("  " + z);
        }
    }
}
