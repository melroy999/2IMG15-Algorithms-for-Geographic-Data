package agd.data.sweepline2;

import agd.data.outline.OutlineEdge;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Find intersections between line segments using the Bentley-Ottmann sweep line algorithm.
 */
public class BentleyOttmann {
    public static void solve() {
        // The list of events that occur within the sweep.
        PriorityQueue<AbstractEvent> events = new PriorityQueue<>();

        // The status of the sweep.
        SweepStatus status = new SweepStatus();

        // The resulting intersections.
        ArrayList<Pair<OutlineEdge, OutlineEdge>> intersections = new ArrayList<>();

        // Continue until the queue is empty.
        while(!events.isEmpty()) {
            AbstractEvent event = events.poll();
            event.resolve(events, status, intersections);
        }
    }
}
