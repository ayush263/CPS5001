package sderds;

import java.util.List;

public class ReliefManager {
    private final DisasterNetwork network;
    private final RoutePlanner routePlanner;

    public ReliefManager(DisasterNetwork network, RoutePlanner routePlanner) {
        this.network = network;
        this.routePlanner = routePlanner;
    }

    public void distributeRelief() {
        List<Zone> residentials = network.getZonesByType(ZoneType.RESIDENTIAL);
        for (Zone z : residentials) {
            // only serve unsafe or near-unsafe zones
            if (z.getRiskLevel() < 0.60) continue;

            ReliefRequest req = buildRequest(z);
            Zone hubZone = findNearestHubWithResources(z);
            if (hubZone == null) {
                System.out.println("[RELIEF] No hub resources available for " + z.getName());
                continue;
            }

            ReliefHub hub = (ReliefHub) hubZone;
            Route route = routePlanner.findSafestPath(hub, z, OptimizationMetric.RISK_AWARE);
            if (route == null) {
                System.out.println("[RELIEF] No safe route from hub " + hub.getName() + " to " + z.getName());
                continue;
            }

            hub.allocate(req);
            System.out.println("[RELIEF] Delivered to " + z.getName() + " from " + hub.getName()
                    + " via " + route);
        }
    }

    public void requestRelief(Zone zone, ReliefRequest req) {
        Zone hubZone = findNearestHubWithResources(zone);
        if (hubZone == null) return;
        ((ReliefHub) hubZone).allocate(req);
    }

    public Zone findNearestHubWithResources(Zone zone) {
        List<Zone> hubs = network.getZonesByType(ZoneType.HUB);
        Zone best = null;
        double bestTime = Double.POSITIVE_INFINITY;

        for (Zone h : hubs) {
            ReliefHub hub = (ReliefHub) h;
            if (!hub.hasResources()) continue;

            Route r = routePlanner.findSafestPath(hub, zone, OptimizationMetric.RISK_AWARE);
            if (r == null) continue;
            double t = routePlanner.estimateTravelTime(r);
            if (t < bestTime) {
                bestTime = t;
                best = hub;
            }
        }
        return best;
    }

    private ReliefRequest buildRequest(Zone z) {
        // proportional request based on population and risk
        int pop = z.getPopulation();
        int food = Math.max(5, pop / 50);
        int water = Math.max(5, pop / 60);
        int kits = Math.max(1, pop / 200);
        return new ReliefRequest(food, water, kits);
    }
}
