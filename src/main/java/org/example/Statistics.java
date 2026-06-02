package org.example;

import java.util.List;

public class Statistics {

    public static double calculateS(
            List<Point> points,
            java.util.function.DoubleFunction<Double> f
    ) {

        double s = 0;

        for (Point p : points) {
            double e = f.apply(p.getX()) - p.getY();
            s += e * e;
        }

        return s;
    }

    public static double sigma(double s, int n) {
        return Math.sqrt(s / n);
    }

    public static double meanY(List<Point> points) {

        double sum = 0;

        for (Point p : points) {
            sum += p.getY();
        }

        return sum / points.size();
    }

    public static double r2(
            List<Point> points,
            java.util.function.DoubleFunction<Double> f
    ) {

        double avg = meanY(points);

        double ssRes = 0;
        double ssTot = 0;

        for (Point p : points) {

            double yi = p.getY();
            double fi = f.apply(p.getX());

            ssRes += Math.pow(yi - fi, 2);
            ssTot += Math.pow(yi - avg, 2);
        }

        return 1 - ssRes / ssTot;
    }

    public static double pearson(List<Point> points) {

        int n = points.size();

        double sx = 0;
        double sy = 0;

        for (Point p : points) {
            sx += p.getX();
            sy += p.getY();
        }

        double mx = sx / n;
        double my = sy / n;

        double numerator = 0;
        double dx = 0;
        double dy = 0;

        for (Point p : points) {

            numerator +=
                    (p.getX() - mx) *
                            (p.getY() - my);

            dx += Math.pow(p.getX() - mx, 2);
            dy += Math.pow(p.getY() - my, 2);
        }

        return numerator / Math.sqrt(dx * dy);
    }
}