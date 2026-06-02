package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleFunction;

public class ApproximationService {

    public List<ApproximationResult> approximate(List<Point> points) {

        List<ApproximationResult> results = new ArrayList<>();

        results.add(linear(points));
        results.add(quadratic(points));
        results.add(cubic(points));

        ApproximationResult exp = exponential(points);
        if (exp != null) results.add(exp);

        ApproximationResult log = logarithmic(points);
        if (log != null) results.add(log);

        ApproximationResult power = power(points);
        if (power != null) results.add(power);

        return results;
    }

    public ApproximationResult best(List<ApproximationResult> results) {

        ApproximationResult best = results.get(0);

        for (ApproximationResult r : results) {
            if (r.getSigma() < best.getSigma()) {
                best = r;
            }
        }

        return best;
    }

    private ApproximationResult linear(List<Point> points) {

        int n = points.size();

        double sx = 0;
        double sy = 0;
        double sxx = 0;
        double sxy = 0;

        for (Point p : points) {
            sx += p.getX();
            sy += p.getY();
            sxx += p.getX() * p.getX();
            sxy += p.getX() * p.getY();
        }

        double a =
                (n * sxy - sx * sy)
                        /
                        (n * sxx - sx * sx);

        double b =
                (sy - a * sx) / n;

        DoubleFunction<Double> f =
                x -> a * x + b;

        double s =
                Statistics.calculateS(points, f);

        return new ApproximationResult(
                FunctionType.LINEAR,
                new double[]{a, b},
                s,
                Statistics.sigma(s, n),
                Statistics.r2(points, f),
                Statistics.pearson(points),
                String.format(
                        "y = %.6fx + %.6f",
                        a,
                        b
                )
        );
    }

    private ApproximationResult quadratic(List<Point> points) {
        return polynomial(points, 2);
    }

    private ApproximationResult cubic(List<Point> points) {
        return polynomial(points, 3);
    }

    private ApproximationResult polynomial(
            List<Point> points,
            int degree
    ) {

        int size = degree + 1;

        double[][] a =
                new double[size][size];

        double[] b =
                new double[size];

        for (int row = 0; row < size; row++) {

            for (int col = 0; col < size; col++) {

                double sum = 0;

                for (Point p : points) {
                    sum += Math.pow(
                            p.getX(),
                            row + col
                    );
                }

                a[row][col] = sum;
            }

            double sum = 0;

            for (Point p : points) {
                sum += p.getY()
                        *
                        Math.pow(
                                p.getX(),
                                row
                        );
            }

            b[row] = sum;
        }

        double[] c =
                MatrixSolver.solve(a, b);

        DoubleFunction<Double> f =
                x -> {

                    double y = 0;

                    for (int i = 0; i < c.length; i++) {
                        y += c[i] * Math.pow(x, i);
                    }

                    return y;
                };

        double s =
                Statistics.calculateS(points, f);

        StringBuilder formula =
                new StringBuilder("y = ");

        for (int i = 0; i < c.length; i++) {

            if (i > 0) {
                formula.append(" + ");
            }

            formula.append(
                    String.format(
                            "%.6f",
                            c[i]
                    )
            );

            if (i > 0) {
                formula.append("x");
                if (i > 1) {
                    formula.append("^").append(i);
                }
            }
        }

        FunctionType type =
                degree == 2
                        ? FunctionType.QUADRATIC
                        : FunctionType.CUBIC;

        return new ApproximationResult(
                type,
                c,
                s,
                Statistics.sigma(
                        s,
                        points.size()
                ),
                Statistics.r2(points, f),
                0,
                formula.toString()
        );
    }

    private ApproximationResult exponential(
            List<Point> points
    ) {

        for (Point p : points) {
            if (p.getY() <= 0) {
                return null;
            }
        }

        List<Point> transformed =
                new ArrayList<>();

        for (Point p : points) {

            transformed.add(
                    new Point(
                            p.getX(),
                            Math.log(p.getY())
                    )
            );
        }

        ApproximationResult temp =
                linear(transformed);

        double b =
                temp.getCoefficients()[0];

        double a =
                Math.exp(
                        temp.getCoefficients()[1]
                );

        DoubleFunction<Double> f =
                x -> a * Math.exp(b * x);

        double s =
                Statistics.calculateS(points, f);

        return new ApproximationResult(
                FunctionType.EXPONENTIAL,
                new double[]{a, b},
                s,
                Statistics.sigma(
                        s,
                        points.size()
                ),
                Statistics.r2(points, f),
                0,
                String.format(
                        "y = %.6fe^(%.6fx)",
                        a,
                        b
                )
        );
    }

    private ApproximationResult logarithmic(
            List<Point> points
    ) {

        for (Point p : points) {
            if (p.getX() <= 0) {
                return null;
            }
        }

        List<Point> transformed =
                new ArrayList<>();

        for (Point p : points) {

            transformed.add(
                    new Point(
                            Math.log(
                                    p.getX()
                            ),
                            p.getY()
                    )
            );
        }

        ApproximationResult temp =
                linear(transformed);

        double a =
                temp.getCoefficients()[0];

        double b =
                temp.getCoefficients()[1];

        DoubleFunction<Double> f =
                x -> a * Math.log(x) + b;

        double s =
                Statistics.calculateS(points, f);

        return new ApproximationResult(
                FunctionType.LOGARITHMIC,
                new double[]{a, b},
                s,
                Statistics.sigma(
                        s,
                        points.size()
                ),
                Statistics.r2(points, f),
                0,
                String.format(
                        "y = %.6fln(x)+%.6f",
                        a,
                        b
                )
        );
    }

    private ApproximationResult power(
            List<Point> points
    ) {

        for (Point p : points) {
            if (p.getX() <= 0 || p.getY() <= 0) {
                return null;
            }
        }

        List<Point> transformed =
                new ArrayList<>();

        for (Point p : points) {

            transformed.add(
                    new Point(
                            Math.log(
                                    p.getX()
                            ),
                            Math.log(
                                    p.getY()
                            )
                    )
            );
        }

        ApproximationResult temp =
                linear(transformed);

        double b =
                temp.getCoefficients()[0];

        double a =
                Math.exp(
                        temp.getCoefficients()[1]
                );

        DoubleFunction<Double> f =
                x -> a * Math.pow(x, b);

        double s =
                Statistics.calculateS(points, f);

        return new ApproximationResult(
                FunctionType.POWER,
                new double[]{a, b},
                s,
                Statistics.sigma(
                        s,
                        points.size()
                ),
                Statistics.r2(points, f),
                0,
                String.format(
                        "y = %.6fx^(%.6f)",
                        a,
                        b
                )
        );
    }
}