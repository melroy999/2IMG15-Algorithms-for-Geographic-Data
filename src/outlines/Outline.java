package outlines;

import agd.math.Point2d;

import java.util.*;
import java.util.stream.Collectors;

public class Outline {
    // The id of the outline.
    private static int ID_COUNTER = 0;
    protected final int id = ID_COUNTER++;

    // The rectangles within this outline.
    private TreeSet<OutlineRectangle> rectangles = new TreeSet<>();

    public Outline(OutlineRectangle rectangle) {
        insert(rectangle);
    }

    public void insert(OutlineRectangle rectangle) {
        rectangles.add(rectangle);
        rectangle.setOutline(this);
    }

    public void merge(Outline... outlines) {
        // Merge the other outlines into this outline.
        for(Outline outline : outlines) {
            rectangles.addAll(outline.rectangles);
            outline.rectangles.forEach(r -> r.setOutline(this));
        }
    }

    /**
     * Project the point p onto the outline and find the position that has the smallest Euclidean distance.
     *
     * @param p The point to find the closest position on the outline to.
     * @return A point on the line segments of the outline such that the distance is minimal.
     */
    public Point2d projectAndSelect(Point2d p, double weight) {
        // For each of the edges, find the best position and the associated score.
        double min = Double.MAX_VALUE;
        Point2d position = null;

        // Find the edges for the buffered outline.
        List<Edge> edges = createOutline(rectangles, weight);

        for(Edge e : edges) {
            Point2d projection = e.project(p);
            double distance = projection.distance2(p);
            if(distance < min) {
                position = projection;
                min = distance;
            }
        }

        return position;
    }

    public static List<Edge> createOutline(TreeSet<OutlineRectangle> rectangles, double weight) {
        // First, grow all the outlines. We know that doubling the weight will always give an integer.
        // Thus, we multiply our entire plane such that the problem stays in the integer plane.
        int iWeight = (int) Math.round(2 * weight);

        // Increase the size of the plane, and create the buffered rectangles.
        TreeSet<OutlineRectangle> bRectangles = new TreeSet<>();
        for(OutlineRectangle rectangle : rectangles) {
            bRectangles.add(BufferedRectangle.getRectangle(rectangle, iWeight));
        }

        List<Edge> bEdges = new ArrayList<>();
        createOutlineHorizontal(bRectangles, bEdges);
        createOutlineVertical(bRectangles, bEdges);

        // Translate the edges back to the original positions.
        return bEdges.stream().map(Edge::rescale).collect(Collectors.toList());
    }

    private static void createOutlineHorizontal(TreeSet<OutlineRectangle> rectangles, List<Edge> edges) {
        // Since the collection is a tree set, we know that the rectangles are already sorted on x succeeded y.
        LinkedList<AbstractEvent> events = new LinkedList<>();
        for(OutlineRectangle r : rectangles) {
            Interval i = new Interval(r.x, r.x + r.width);
            events.add(new StartEvent(r.y, i));
            events.add(new EndEvent(r.y + r.height, i));
        }
        Collections.sort(events);

        // Get the x-value that we start at.
        int v = rectangles.first().y;

        // The line segments that start and end at the current x-value. Also keep the ones that are active.
        List<Interval> starting = new ArrayList<>();
        List<Interval> ending = new ArrayList<>();
        TreeSet<Interval> active = new TreeSet<>();

        while(!events.isEmpty()) {
            // Find the current event.
            AbstractEvent event = events.poll();

            if(event.v != v) {
                // The x-value has changed. We have to flush the edges that we have found.
                List<Interval> intervals = flush(starting, ending, active);
                for(Interval i : intervals) {
                    edges.add(new Edge(new Point2d(i.start, v), new Point2d(i.end, v)));
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
        List<Interval> intervals = flush(starting, ending, active);
        for(Interval i : intervals) {
            edges.add(new Edge(new Point2d(i.start, v), new Point2d(i.end, v)));
        }
    }

    private static void createOutlineVertical(TreeSet<OutlineRectangle> rectangles, List<Edge> edges) {
        // Since the collection is a tree set, we know that the rectangles are already sorted on x succeeded y.
        LinkedList<AbstractEvent> events = new LinkedList<>();
        for(OutlineRectangle r : rectangles) {
            Interval i = new Interval(r.y, r.y + r.height);
            events.add(new StartEvent(r.x, i));
            events.add(new EndEvent(r.x + r.width, i));
        }
        Collections.sort(events);

        // Get the x-value that we start at.
        int v = rectangles.first().x;

        // The line segments that start and end at the current x-value. Also keep the ones that are active.
        List<Interval> starting = new ArrayList<>();
        List<Interval> ending = new ArrayList<>();
        TreeSet<Interval> active = new TreeSet<>();

        while(!events.isEmpty()) {
            // Find the current event.
            AbstractEvent event = events.poll();

            if(event.v != v) {
                // The x-value has changed. We have to flush the edges that we have found.
                List<Interval> intervals = flush(starting, ending, active);
                for(Interval i : intervals) {
                    edges.add(new Edge(new Point2d(v, i.start), new Point2d(v, i.end)));
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
        List<Interval> intervals = flush(starting, ending, active);
        for(Interval i : intervals) {
            edges.add(new Edge(new Point2d(v, i.start), new Point2d(v, i.end)));
        }
    }

    private static List<Interval> flush(List<Interval> starting, List<Interval> ending, TreeSet<Interval> active) {
        // Merge consecutive intervals.
        List<Interval> startingMerged = merge(starting);
        List<Interval> endingMerged = merge(ending);
        List<Interval> activeMerged = merge(active);

        // Find the ending line segments that are not overshadowed by the currently active line segments.
        List<Interval> finals = setMinus(endingMerged, activeMerged);

        // Find the starting line segments that are not overshadowed by
        // the currently active line segments minus the starting line segments.
        TreeSet<Interval> fullActive = new TreeSet<>(active);
        fullActive.removeAll(starting);
        fullActive.addAll(ending);
        List<Interval> fullActiveMerged = merge(fullActive);
        List<Interval> starters = setMinus(startingMerged, fullActiveMerged);

        // Merge the results.
        finals.addAll(starters);
        return finals;
    }

    private static List<Interval> merge(Collection<Interval> intervals) {
        List<Interval> result = new ArrayList<>();
        if(intervals.isEmpty()) return result;

        // Merge consecutive intervals.
        int min = intervals.iterator().next().start;
        int max = intervals.iterator().next().end;

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

    public static List<Interval> setMinus(List<Interval> source, List<Interval> minus) {
        List<Interval> result = new ArrayList<>();
        if(source.isEmpty()) return result;
        if(minus.isEmpty()) return new ArrayList<>(source);

        // The current minus interval.
        Interval sub = minus.get(0);
        int subi = 1;

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
                        if(subi < minus.size()) {
                            sub = minus.get(subi++);
                        } else {
                            sub = null;
                        }
                    }
                }
            }
        }

        return result;
    }

    public TreeSet<OutlineRectangle> getRectangles() {
        return rectangles;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Outline o2 = (Outline) o;

        return id == o2.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
