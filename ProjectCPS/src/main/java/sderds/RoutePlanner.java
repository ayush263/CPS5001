package sderds;

import java.util.*;

public class RoutePlanner {
    private final DisasterNetwork network;

    public RoutePlanner(DisasterNetwork network) {
        this.network = network;
    }

    public Route findSafestPath(Zone from, Zone to, OptimizationMetric metric) {
        if (from == null || to == null) return null;

        Map<String, Double> best = new HashMap<>();
        Map<String, String> prev = new HashMap<>();

        for (Zone z : network.getAllZones()) {
            best.put(z.getId(), Double.POSITIVE_INFINITY);
        }
        best.put(from.getId(), 0.0);

        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(best::get));
        pq.add(from.getId());

        while (!pq.isEmpty()) {
            String curId = pq.poll();
            if (curId.equals(to.getId())) break;

            Zone cur = network.getZoneById(curId);
            for (Road r : network.getNeighbours(cur)) {
                // Hard block: avoid extremely risky roads
                if (r.getRisk() >= 0.95) continue;

                double alt = best.get(curId) + r.effectiveCost(metric);
                String nb = r.getTo().getId();

                if (alt < best.get(nb)) {
                    best.put(nb, alt);
                    prev.put(nb, curId);
                    pq.remove(nb);
                    pq.add(nb);
                }
            }
        }

        if (!from.getId().equals(to.getId()) && !prev.containsKey(to.getId())) return null;

        List<Zone> path = new ArrayList<>();
        String cur = to.getId();
        path.add(network.getZoneById(cur));
        while (prev.containsKey(cur)) {
            cur = prev.get(cur);
            path.add(network.getZoneById(cur));
        }
        Collections.reverse(path);

        double dist = 0.0, time = 0.0, risk = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Zone a = path.get(i);
            Zone b = path.get(i + 1);
            for (Road rd : network.getNeighbours(a)) {
                if (rd.getTo().getId().equals(b.getId())) {
                    dist += rd.getDistanceKm();
                    time += rd.getBaseTravelTimeMin();
                    risk += rd.getRisk();
                    break;
                }
            }
        }

        return new Route(path, dist, time, risk);
    }

    public double estimateTravelTime(Route route) {
        if (route == null) return Double.POSITIVE_INFINITY;
        return route.getTotalTravelTimeMin();
    }
}
