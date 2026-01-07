package sderds;

public class Main {
    public static void main(String[] args) {
        SimulationController sim = new SimulationController();
        sim.setupScenario();
        sim.runSimulation(6);
    }
}
