package org.example;

public class MatrixSolver {

    public static double[] solve(double[][] a, double[] b) {

        int n = b.length;

        for (int i = 0; i < n; i++) {

            int max = i;

            for (int j = i + 1; j < n; j++) {
                if (Math.abs(a[j][i]) > Math.abs(a[max][i])) {
                    max = j;
                }
            }

            double[] tempRow = a[i];
            a[i] = a[max];
            a[max] = tempRow;

            double temp = b[i];
            b[i] = b[max];
            b[max] = temp;

            for (int j = i + 1; j < n; j++) {

                double factor = a[j][i] / a[i][i];

                b[j] -= factor * b[i];

                for (int k = i; k < n; k++) {
                    a[j][k] -= factor * a[i][k];
                }
            }
        }

        double[] x = new double[n];

        for (int i = n - 1; i >= 0; i--) {

            double sum = b[i];

            for (int j = i + 1; j < n; j++) {
                sum -= a[i][j] * x[j];
            }

            x[i] = sum / a[i][i];
        }

        return x;
    }
}