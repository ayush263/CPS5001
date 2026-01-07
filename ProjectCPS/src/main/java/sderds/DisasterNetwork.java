package sderds;

import java.util.*;

public class DisasterNetwork {
    private final Map<String, Zone> zones = new HashMap<>();
    private final Map<String, List<Road>> adjacency = new HashMap<>();

    public void addZone(Zone zone) {
        zones.put(zone.getId(), zone);
        adjacency.putIfAbsent(zone.getId(), new ArrayList<>());
    }

    public void removeZone(String zoneId) {
        zones.remove(zoneId);
        adjacency.remove(zoneId);
        for (List<Road> roads : adjacency.values()) {
            roads.removeIf(r -> r.getTo().getId().equals(zoneId));
        }
    }

    public void addRoad(Zone from, Zone to, double distanceKm, double baseTravelTimeMin) {
        // undirected edges: add both directions
        Road a = new Road(from, to, distanceKm, baseTravelTimeMin, 0.0);
        Road b = new Road(to, from, distanceKm, baseTravelTimeMin, 0.0);
        adjacency.get(from.getId()).add(a);
        adjacency.get(to.getId()).add(b);
    }

    public void updateRoadRisk(String fromId, String toId, double risk) {
        List<Road> list = adjacency.get(fromId);
        if (list != null) {
            for (Road r : list) {
                if (r.getTo().getId().equals(toId)) {
                    r.setRisk(risk);
                }
            }
        }
        List<Road> rev = adjacency.get(toId);
        if (rev != null) {
            for (Road r : rev) {
                if (r.getTo().getId().equals(fromId)) {
                    r.setRisk(risk);
                }
            }
        }
    }

    public List<Road> getNeighbours(Zone zone) {
        return adjacency.getOrDefault(zone.getId(), Collections.emptyList());
    }

    public Zone getZoneById(String id) {
        return zones.get(id);
    }

    public Collection<Zone> getAllZones() {
        return zones.values();
    }

    public List<Zone> getZonesByType(ZoneType type) {
        List<Zone> out = new ArrayList<>();
        for (Zone z : zones.values()) {
            if (z.getType() == type) out.add(z);
        }
        return out;
    }
}
