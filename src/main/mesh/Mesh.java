package main.mesh;

import main.geom.Point;
import main.geom.VTKType;
import main.geom.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public interface Mesh {
    /**
     * @return List of Cell objects inside the domain (does not include ghost cells).
     */
    List<Cell> cells();

    default Stream<Cell> cellStream() {
        return cells().parallelStream();
    }

    /**
     * @return List of internal faces (does not include boundary faces)
     */
    List<Face> internalFaces();

    default Stream<Face> internalFaceStream() {
        return internalFaces().parallelStream();
    }

    /**
     * @return Node objects which are on the boundary and inside the domain
     * (does not include the nodes outside the domain - the outside nodes of the ghost cells)
     */
    List<Node> nodes();

    default Stream<Node> nodeStream() {
        return nodes().parallelStream();
    }

    /**
     * @return Boundary objects, which contains the boundary condition and faces which make up the boundary.
     */
    List<Boundary> boundaries();

    default Stream<Boundary> boundaryStream() {
        return boundaries().parallelStream();
    }

    default void setAllCellIndices() {
        for (int i = 0; i < cells().size(); i++) {
            cells().get(i).setIndex(i);
        }
    }

    default void setAllFaceIndices() {
        // set internal face indices
        int index = 0;
        for (Face face : internalFaces()) {
            face.setIndex(index);
            index++;
        }

        // set boundary face indices
        for (Boundary boundary : boundaries()) {
            for (Face face : boundary.faces) {
                face.setIndex(index);
                index++;
            }
        }
    }

    static Cell ghostCell(Cell boundaryCell, Face boundaryFace) {
        // mirror cell nodes about the face
        List<Node> ghostCellNodes = new ArrayList<>();
        for (Node node : boundaryCell.nodes) {
            if (faceContains(boundaryFace, node)) {
                ghostCellNodes.add(node);
            } else { // mirror about the face
                ghostCellNodes.add(mirrorNode(boundaryFace, node));
            }
        }

        Node[] nodes = ghostCellNodes.toArray(new Node[0]);

        VTKType vtkType = boundaryCell.vtkType;
        Point ghostCellCentroid = mirrorPoint(boundaryFace, boundaryCell.shape.centroid);
        Shape shape = new Shape(boundaryCell.shape.volume, ghostCellCentroid);
        int numVars = boundaryCell.U.length;

        return new Cell(nodes, vtkType, shape, numVars);
    }

    static private Node mirrorNode(Face face, Node node) {
        Point mirroredPoint = mirrorPoint(face, node.location());

        int numVars = face.U.length;
        return new Node(mirroredPoint, numVars);
    }

    static private Point mirrorPoint(Face face, Point point) {
        Vector faceNormal = face.surface.unitNormal();
        Point faceCentroid = face.surface.centroid;

        Vector nodeVector = new Vector(faceCentroid, point);
        Vector normalComponent = faceNormal.mult(nodeVector.dot(faceNormal));
        Vector tangentComponent = nodeVector.sub(normalComponent);

        Vector mirroredVector = (normalComponent.mult(-1)).add(tangentComponent);

        return mirroredVector
                .add(faceCentroid.toVector())
                .toPoint();
    }

    static private boolean faceContains(Face face, Node node) {
        return Arrays.stream(face.nodes)
                .anyMatch(n -> n == node);
    }
}
