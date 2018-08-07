package main.solver;

import main.geom.Vector;
import main.mesh.Cell;
import main.mesh.Mesh;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static main.util.DoubleMatrix.invert;
import static main.util.DoubleMatrix.multiply;
import static main.util.DoubleMatrix.transpose;

public class LeastSquareCellGradient implements CellGradientCalculator {

    private final Cell[][] neighbors;
    private final double[][][] matrices;

    public LeastSquareCellGradient(Mesh mesh, NeighborsCalculator neighCalc) {
        int numCells = mesh.cells().size();
        neighbors = new Cell[numCells][];
        matrices = new double[numCells][][];
        mesh.cellStream()
                .forEach(cell -> setup(cell, neighCalc));
    }

    private void setup(Cell cell, NeighborsCalculator neighCalc) {
        Cell[] neighs = neighCalc.calculateFor(cell).toArray(new Cell[0]);
        neighbors[cell.index] = neighs;

        List<Vector> distanceVectors = Arrays.stream(neighs)
                .map(neighCell -> new Vector(cell.shape.centroid, neighCell.shape.centroid))
                .collect(toList());

        double minDistance = distanceVectors.stream()
                .mapToDouble(Vector::mag)
                .min().orElse(1.0);

        Vector shiftBy = new Vector(0, 0, 0);
        boolean xZero = distanceVectors.stream().allMatch(v -> Math.abs(v.x) < 1e-15);
        if (xZero) {
            shiftBy = shiftBy.add(new Vector(minDistance, 0, 0));
        }
        boolean yZero = distanceVectors.stream().allMatch(v -> Math.abs(v.y) < 1e-15);
        if (yZero) {
            shiftBy = shiftBy.add(new Vector(0, minDistance, 0));
        }
        boolean zZero = distanceVectors.stream().allMatch(v -> Math.abs(v.z) < 1e-15);
        if (zZero) {
            shiftBy = shiftBy.add(new Vector(0, 0, minDistance));
        }

        double[][] A = new double[neighs.length][3];
        for (int i = 0; i < A.length; i++) {
            Vector distance = distanceVectors.get(i).add(shiftBy);
            A[i][0] = distance.x;
            A[i][1] = distance.y;
            A[i][2] = distance.z;
        }

        double[][] AT = transpose(A);
        matrices[cell.index] = multiply(invert(multiply(AT, A)), AT);
    }

    @Override
    public Vector[] forCell(Cell cell) {
        int numVars = cell.U.length;
        Vector[] gradients = new Vector[numVars];
        for (int var = 0; var < numVars; var++) {
            gradients[var] = forVar(cell, var);
        }

        return gradients;
    }

    private Vector forVar(Cell cell, int var) {
        double[] deltaU = Arrays.stream(neighbors[cell.index])
                .mapToDouble(neighCell -> neighCell.U[var] - cell.U[var])
                .toArray();
        double[] derivatives = multiply(matrices[cell.index], deltaU);

        return new Vector(derivatives[0], derivatives[1], derivatives[2]);
    }
}