package agd.overlap;

import agd.data.input.WeightedPoint;
import agd.data.outlines.Edge;
import agd.data.outlines.OutlineRectangle;
import agd.data.outlines.SimpleOutline;
import agd.solver.AbstractSolver;
import org.junit.jupiter.api.Test;

import java.util.*;

class OverlapSweepTest {
    private static WeightedPoint p = new WeightedPoint(0, 0, 3, 0);

    @Test
    void findIntersections1() {
        OutlineRectangle r1 = new OutlineRectangle(0, 0, 3, p);
        SimpleOutline o1 = new SimpleOutline(r1);

        OutlineRectangle r2 = new OutlineRectangle(1, 1, 3, p);
        SimpleOutline o2 = new SimpleOutline(r2);

        List<Edge> edges = new ArrayList<>();
        edges.addAll(o1.getEdge().toList());
        edges.addAll(o2.getEdge().toList());
        edges.sort(Comparator.comparingInt(Edge::getId));
        Map<Integer, Edge> toEdge = new HashMap<>();
        edges.forEach(e -> toEdge.put(e.getId(), e));

        System.out.println("Sweepline overlaps:");
        TreeMap<Integer, Set<Edge>> overlaps = OverlapSweep.findOverlaps(o1.getEdge(), o2.getEdge());

        for(Map.Entry<Integer, Set<Edge>> entries : overlaps.entrySet()) {
            System.out.print(toEdge.get(entries.getKey()) + " overlaps with: ");
            entries.getValue().forEach(v -> System.out.print(toEdge.get(v.getId()) + " "));
            System.out.println();
        }

        System.out.println();

        AbstractSolver.printSolution(new HashSet<>(Arrays.asList(o1, o2)));
    }

    @Test
    void findIntersections2() {
        OutlineRectangle r1 = new OutlineRectangle(0, 0, 3, p);
        SimpleOutline o1 = new SimpleOutline(r1);

        OutlineRectangle r2 = new OutlineRectangle(0, 1, 3, p);
        SimpleOutline o2 = new SimpleOutline(r2);

        List<Edge> edges = new ArrayList<>();
        edges.addAll(o1.getEdge().toList());
        edges.addAll(o2.getEdge().toList());
        edges.sort(Comparator.comparingInt(Edge::getId));
        Map<Integer, Edge> toEdge = new HashMap<>();
        edges.forEach(e -> toEdge.put(e.getId(), e));

        System.out.println("Sweepline overlaps:");
        TreeMap<Integer, Set<Edge>> overlaps = OverlapSweep.findOverlaps(o1.getEdge(), o2.getEdge());

        for(Map.Entry<Integer, Set<Edge>> entries : overlaps.entrySet()) {
            System.out.print(toEdge.get(entries.getKey()) + " overlaps with: ");
            entries.getValue().forEach(v -> System.out.print(toEdge.get(v.getId()) + " "));
            System.out.println();
        }

        System.out.println();

        AbstractSolver.printSolution(new HashSet<>(Arrays.asList(o1, o2)));
    }

    @Test
    void findIntersections3() {
        OutlineRectangle r1 = new OutlineRectangle(0, 0, 3, p);
        SimpleOutline o1 = new SimpleOutline(r1);

        OutlineRectangle r2 = new OutlineRectangle(1, 0, 3, p);
        SimpleOutline o2 = new SimpleOutline(r2);

        List<Edge> edges = new ArrayList<>();
        edges.addAll(o1.getEdge().toList());
        edges.addAll(o2.getEdge().toList());
        edges.sort(Comparator.comparingInt(Edge::getId));
        Map<Integer, Edge> toEdge = new HashMap<>();
        edges.forEach(e -> toEdge.put(e.getId(), e));

        System.out.println("Sweepline overlaps:");
        TreeMap<Integer, Set<Edge>> overlaps = OverlapSweep.findOverlaps(o1.getEdge(), o2.getEdge());

        for(Map.Entry<Integer, Set<Edge>> entries : overlaps.entrySet()) {
            System.out.print(toEdge.get(entries.getKey()) + " overlaps with: ");
            entries.getValue().forEach(v -> System.out.print(toEdge.get(v.getId()) + " "));
            System.out.println();
        }

        System.out.println();

        AbstractSolver.printSolution(new HashSet<>(Arrays.asList(o1, o2)));
    }

    @Test
    void findIntersections4() {
        OutlineRectangle r1 = new OutlineRectangle(0, 0, 3, p);
        SimpleOutline o1 = new SimpleOutline(r1);

        OutlineRectangle r2 = new OutlineRectangle(0, 3, 3, p);
        SimpleOutline o2 = new SimpleOutline(r2);

        List<Edge> edges = new ArrayList<>();
        edges.addAll(o1.getEdge().toList());
        edges.addAll(o2.getEdge().toList());
        edges.sort(Comparator.comparingInt(Edge::getId));
        Map<Integer, Edge> toEdge = new HashMap<>();
        edges.forEach(e -> toEdge.put(e.getId(), e));

        System.out.println("Sweepline overlaps:");
        TreeMap<Integer, Set<Edge>> overlaps = OverlapSweep.findOverlaps(o1.getEdge(), o2.getEdge());

        for(Map.Entry<Integer, Set<Edge>> entries : overlaps.entrySet()) {
            System.out.print(toEdge.get(entries.getKey()) + " overlaps with: ");
            entries.getValue().forEach(v -> System.out.print(toEdge.get(v.getId()) + " "));
            System.out.println();
        }

        System.out.println();

        AbstractSolver.printSolution(new HashSet<>(Arrays.asList(o1, o2)));
    }

    @Test
    void findIntersections5() {
        OutlineRectangle r1 = new OutlineRectangle(0, 0, 3, p);
        SimpleOutline o1 = new SimpleOutline(r1);

        OutlineRectangle r2 = new OutlineRectangle(3, 0, 3, p);
        SimpleOutline o2 = new SimpleOutline(r2);

        List<Edge> edges = new ArrayList<>();
        edges.addAll(o1.getEdge().toList());
        edges.addAll(o2.getEdge().toList());
        edges.sort(Comparator.comparingInt(Edge::getId));
        Map<Integer, Edge> toEdge = new HashMap<>();
        edges.forEach(e -> toEdge.put(e.getId(), e));

        System.out.println("Sweepline overlaps:");
        TreeMap<Integer, Set<Edge>> overlaps = OverlapSweep.findOverlaps(o1.getEdge(), o2.getEdge());

        for(Map.Entry<Integer, Set<Edge>> entries : overlaps.entrySet()) {
            System.out.print(toEdge.get(entries.getKey()) + " overlaps with: ");
            entries.getValue().forEach(v -> System.out.print(toEdge.get(v.getId()) + " "));
            System.out.println();
        }

        System.out.println();

        AbstractSolver.printSolution(new HashSet<>(Arrays.asList(o1, o2)));
    }
}