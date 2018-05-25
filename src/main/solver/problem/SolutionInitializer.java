package main.solver.problem;

import main.geom.Point;

@FunctionalInterface
public interface SolutionInitializer {
    double[] valueAt(Point p);
}