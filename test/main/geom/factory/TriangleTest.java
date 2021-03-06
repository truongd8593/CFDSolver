package main.geom.factory;

import main.geom.Point;
import main.geom.Vector;
import org.junit.Test;

import static main.util.TestHelper.assertThrows;
import static main.geom.VTKType.VTK_TRIANGLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TriangleTest {

    @Test
    public void points() {
        Point p0 = new Point(-11.29642784, 16.02452759, -38.94467857);
        Point p1 = new Point(3.10731051, 10.31302616, -38.94467857);
        Point p2 = new Point(30.23637047, 18.61893824, -0.41788033);
        Triangle triangle = new Triangle(p0, p1, p2);

        Point[] points = triangle.points();
        assertSame(p0, points[0]);
        assertSame(p1, points[1]);
        assertSame(p2, points[2]);
    }

    @Test
    public void vtkType() {
        Point p0 = new Point(-11.29642784, 16.02452759, -38.94467857);
        Point p1 = new Point(3.10731051, 10.31302616, -38.94467857);
        Point p2 = new Point(30.23637047, 18.61893824, -0.41788033);
        Triangle triangle = new Triangle(p0, p1, p2);

        assertEquals(VTK_TRIANGLE, triangle.vtkType());
    }

    @Test
    public void length() {
        Point p0 = new Point(-11.29642784, 16.02452759, -38.94467857);
        Point p1 = new Point(3.10731051, 10.31302616, -38.94467857);
        Point p2 = new Point(30.23637047, 18.61893824, -0.41788033);
        Triangle triangle = new Triangle(p0, p1, p2);
        assertThrows(ArithmeticException.class, triangle::length);
    }

    @Test
    public void area() {
        Point p0 = new Point(-11.29642784, 16.02452759, -38.94467857);
        Point p1 = new Point(3.10731051, 10.31302616, -38.94467857);
        Point p2 = new Point(30.23637047, 18.61893824, -0.41788033);
        Triangle triangle = new Triangle(p0, p1, p2);

        double expectedArea = 328.54363842;

        assertEquals(0, (expectedArea - triangle.area()) / expectedArea, 1e-8);
    }

    @Test
    public void volume() {
        Point p0 = new Point(-11.29642784, 16.02452759, -38.94467857);
        Point p1 = new Point(3.10731051, 10.31302616, -38.94467857);
        Point p2 = new Point(30.23637047, 18.61893824, -0.41788033);
        Triangle triangle = new Triangle(p0, p1, p2);
        assertThrows(ArithmeticException.class, triangle::volume);
    }

    @Test
    public void centroid() {
        Point p0 = new Point(-11.29642784, 16.02452759, -38.94467857);
        Point p1 = new Point(3.10731051, 10.31302616, -38.94467857);
        Point p2 = new Point(30.23637047, 18.61893824, -0.41788033);
        Triangle triangle = new Triangle(p0, p1, p2);

        Point expectedCentroid = new Point(7.34908438, 14.98549733, -26.10241249);

        assertEquals(0, expectedCentroid.distance(triangle.centroid()), 1e-8);
    }

    @Test
    public void unitNormal() {
        Point p0 = new Point(-11.29642784, 16.02452759, -38.94467857);
        Point p1 = new Point(3.10731051, 10.31302616, -38.94467857);
        Point p2 = new Point(30.23637047, 18.61893824, -0.41788033);
        Triangle triangle = new Triangle(p0, p1, p2);

        Vector expectedUnitNormal = new Vector(0.334880725, 0.844530005, -0.417880332).mult(-1);

        assertEquals(0, expectedUnitNormal.sub(triangle.unitNormal()).mag(), 1e-8);
    }
}
