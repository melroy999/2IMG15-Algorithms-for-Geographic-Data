package agd.solver;

import agd.data.outlines.OutlineRectangle;

import java.util.PriorityQueue;
import java.util.TreeSet;

public class OutlineTest {

    public static void createOutline(TreeSet<OutlineRectangle> rectangles) {
        createOutlineHorizontal(rectangles);
        createOutlineVertical(rectangles);
    }

    public static void createOutlineHorizontal(TreeSet<OutlineRectangle> rectangles) {
        // Since the collection is a tree set, we know that the rectangles are already sorted on x succeeded y.
        PriorityQueue<AbstractEvent> events = new PriorityQueue<>();
        rectangles.forEach(r -> {
            Interval i = new Interval(r.x, r.x + r.width);
            events.add(new StartEvent(r.y, i));
            events.add(new EndEvent(r.y + r.height, i));
        });

        // Get the x-value that we start at.
        int v = rectangles.first().x;

        // The line segments that start and end at the current x-value. Also keep the ones that are active.
        TreeSet<Interval> starting = new TreeSet<>();
        TreeSet<Interval> ending = new TreeSet<>();
        TreeSet<Interval> active = new TreeSet<>();

        while(!events.isEmpty()) {
            // Find the current event.
            AbstractEvent event = events.poll();

            if(event.v != v) {
                // The x-value has changed. We have to flush the edges that we have found.
                TreeSet<Interval> intervals = flush(starting, ending, active);
                for(Interval i : intervals) {
                    System.out.println("\\draw[red] (" + i.start + ", " + v + ") -- (" + i.end  + ", " + v + ");");
                }

                // Clear the current starting and ending states.
                starting.clear();
                ending.clear();

                // Set the new x.
                v = event.v;
            }

            // Check which type of event we have encountered and add the value to the corresponding list.
            if(event instanceof StartEvent) {
                starting.add(event.i);
                active.add(event.i);
            } else {
                ending.add(event.i);
                active.remove(event.i);
            }
        }

        // Flush the remaining edges.
        TreeSet<Interval> intervals = flush(starting, ending, active);
        for(Interval i : intervals) {
            System.out.println("\\draw[red] (" + i.start + ", " + v + ") -- (" + i.end + ", " + v + ");");
        }
    }

    public static void createOutlineVertical(TreeSet<OutlineRectangle> rectangles) {
        // Since the collection is a tree set, we know that the rectangles are already sorted on x succeeded y.
        PriorityQueue<AbstractEvent> events = new PriorityQueue<>();
        rectangles.forEach(r -> {
            Interval i = new Interval(r.y, r.y + r.height);
            events.add(new StartEvent(r.x, i));
            events.add(new EndEvent(r.x + r.width, i));
        });

        // Get the x-value that we start at.
        int v = rectangles.first().x;

        // The line segments that start and end at the current x-value. Also keep the ones that are active.
        TreeSet<Interval> starting = new TreeSet<>();
        TreeSet<Interval> ending = new TreeSet<>();
        TreeSet<Interval> active = new TreeSet<>();

        while(!events.isEmpty()) {
            // Find the current event.
            AbstractEvent event = events.poll();

            if(event.v != v) {
                // The x-value has changed. We have to flush the edges that we have found.
                TreeSet<Interval> intervals = flush(starting, ending, active);
                for(Interval i : intervals) {
                    System.out.println("\\draw[red] (" + v + ", " + i.start + ") -- (" + v + ", " + i.end + ");");
                }

                // Clear the current starting and ending states.
                starting.clear();
                ending.clear();

                // Set the new x.
                v = event.v;
            }

            // Check which type of event we have encountered and add the value to the corresponding list.
            if(event instanceof StartEvent) {
                starting.add(event.i);
                active.add(event.i);
            } else {
                ending.add(event.i);
                active.remove(event.i);
            }
        }

        // Flush the remaining edges.
        TreeSet<Interval> intervals = flush(starting, ending, active);
        for(Interval i : intervals) {
            System.out.println("\\draw[red] (" + v + ", " + i.start + ") -- (" + v + ", " + i.end + ");");
        }
    }



    public static TreeSet<Interval> flush(TreeSet<Interval> starting, TreeSet<Interval> ending, TreeSet<Interval> active) {
        // Merge consecutive intervals.
        TreeSet<Interval> startingMerged = merge(starting);
        TreeSet<Interval> endingMerged = merge(ending);
        TreeSet<Interval> activeMerged = merge(active);

        // Find the ending line segments that are not overshadowed by the currently active line segments.
        TreeSet<Interval> finals = setMinus(endingMerged, activeMerged);

        // Find the starting line segments that are not overshadowed by
        // the currently active line segments minus the starting line segments.
        TreeSet<Interval> fullActive = new TreeSet<>(active);
        fullActive.removeAll(starting);
        fullActive.addAll(ending);
        TreeSet<Interval> fullActiveMerged = merge(fullActive);
        TreeSet<Interval> starters = setMinus(startingMerged, fullActiveMerged);

        // Merge the results.
        finals.addAll(starters);
        return finals;
    }

    private static TreeSet<Interval> merge(TreeSet<Interval> intervals) {
        TreeSet<Interval> result = new TreeSet<>();
        if(intervals.isEmpty()) return result;

        // Merge consecutive intervals.
        int min = intervals.first().start;
        int max = intervals.first().end;

        for(Interval interval : intervals) {
            if(max < interval.start) {
                // The interval is outside of the current range.
                result.add(new Interval(min, max));

                min = interval.start;
                max = interval.end;
            } else {
                // Extend the current interval.
                max = Math.max(max, interval.end);
            }
        }

        // Add the remaining interval.
        result.add(new Interval(min, max));

        return result;
    }

    static TreeSet<Interval> setMinus(TreeSet<Interval> source, TreeSet<Interval> minus) {
        TreeSet<Interval> result = new TreeSet<>();
        if(source.isEmpty()) return result;
        if(minus.isEmpty()) return new TreeSet<>(source);

        // The current minus interval.
        Interval sub = minus.first();

        // We assume that both sets have been merged.
        a1: for(Interval i : source) {
            // What is our starting height of the current interval?
            int start = i.start;

            while(true) {
                if(sub == null) {
                    // Add the entire interval.
                    result.add(new Interval(start, i.end));
                    continue a1;
                }

                // If our starting point is below the subtraction, add a line segment.
                if(start < sub.start) {
                    // Draw the edge all the way up to the sub starting point.
                    int end = Math.min(sub.start, i.end);
                    result.add(new Interval(start, end));

                    if(end == i.end) {
                        // proceed.
                        continue a1;
                    }

                    // Set a new starting position.
                    start = Math.max(sub.start, i.start);
                } else {
                    // We have to shift our start.
                    start = Math.max(sub.end, i.start);

                    if(start >= i.end) {
                        // Continue to the next edge.
                        continue a1;
                    } else {
                        // Continue to the next sub.
                        sub = minus.higher(sub);
                    }
                }
            }
        }

        return result;
    }

    public static abstract class AbstractEvent implements Comparable<AbstractEvent> {
        // The integer value at which the event should be triggered.
        private final int v;

        // The associated interval.
        private final Interval i;

        public AbstractEvent(int v, Interval i) {
            this.v = v;
            this.i = i;
        }

        @Override
        public int compareTo(AbstractEvent o) {
            return Integer.compare(v, o.v);
        }
    }

    public static class StartEvent extends AbstractEvent {

        public StartEvent(int v, Interval i) {
            super(v, i);
        }
    }

    public static class EndEvent extends AbstractEvent {

        public EndEvent(int v, Interval i) {
            super(v, i);
        }
    }

    public static class Interval implements Comparable<Interval> {
        final int start, end;

        // An unique id for the interval, such that we will always delete the correct one.
        static int ID_COUNTER = 0;
        private final int id = ID_COUNTER++;

        public Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public int compareTo(Interval o) {
            int start = Integer.compare(this.start, o.start);

            if(start == 0) {
                int end = Integer.compare(this.end, o.end);
                if(end == 0) {
                    return Integer.compare(id, o.id);
                } else {
                    return end;
                }
            } else {
                return start;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Interval interval = (Interval) o;

            return id == interval.id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {
            return "Interval{" +
                    "start=" + start +
                    ", end=" + end +
                    ", id=" + id +
                    '}';
        }
    }
}
