package sderds;

public class Shelter extends Zone {
    private final int capacity;
    private int occupied;

    public Shelter(String id, String name, int capacity, double riskLevel) {
        super(id, name, ZoneType.SHELTER, 0, riskLevel);
        this.capacity = Math.max(0, capacity);
        this.occupied = 0;
    }

    public int getCapacity() { return capacity; }
    public int getOccupied() { return occupied; }

    public int remainingCapacity() {
        return Math.max(0, capacity - occupied);
    }

    public boolean canAccept(int people) {
        return people >= 0 && remainingCapacity() >= people;
    }

    public void admit(int people) {
        if (people <= 0) return;
        int admitted = Math.min(people, remainingCapacity());
        occupied += admitted;
    }

    @Override
    public String toString() {
        return "Shelter{" +
                "name=" + getName() +
                ", cap=" + capacity +
                ", occ=" + occupied +
                ", rem=" + remainingCapacity() +
                ", risk=" + String.format("%.2f", getRiskLevel()) +
                ", unsafe=" + isUnsafe() +
                '}';
    }
}
