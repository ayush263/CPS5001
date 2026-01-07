package sderds;

public class EvacuationTask implements Comparable<EvacuationTask> {
    private final String id;
    private final Zone sourceZone;
    private final int peopleToMove;
    private final int priority; // higher = more urgent

    public EvacuationTask(String id, Zone sourceZone, int peopleToMove, int priority) {
        this.id = id;
        this.sourceZone = sourceZone;
        this.peopleToMove = Math.max(0, peopleToMove);
        this.priority = priority;
    }

    public String getId() { return id; }
    public Zone getSourceZone() { return sourceZone; }
    public int getPeopleToMove() { return peopleToMove; }
    public int getPriority() { return priority; }

    @Override
    public int compareTo(EvacuationTask other) {
        // higher priority first
        return Integer.compare(other.priority, this.priority);
    }

    @Override
    public String toString() {
        return "EvacTask{" + id +
                ", zone=" + sourceZone.getName() +
                ", people=" + peopleToMove +
                ", prio=" + priority +
                '}';
    }
}
