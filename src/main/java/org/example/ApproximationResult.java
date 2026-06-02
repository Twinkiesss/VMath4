package org.example;

public class ApproximationResult {

    private FunctionType type;

    private double[] coefficients;

    private double s;

    private double sigma;

    private double r2;

    private double pearson;

    private String formula;

    public ApproximationResult(
            FunctionType type,
            double[] coefficients,
            double s,
            double sigma,
            double r2,
            double pearson,
            String formula
    ) {
        this.type = type;
        this.coefficients = coefficients;
        this.s = s;
        this.sigma = sigma;
        this.r2 = r2;
        this.pearson = pearson;
        this.formula = formula;
    }

    public FunctionType getType() {
        return type;
    }

    public double[] getCoefficients() {
        return coefficients;
    }

    public double getS() {
        return s;
    }

    public double getSigma() {
        return sigma;
    }

    public double getR2() {
        return r2;
    }

    public double getPearson() {
        return pearson;
    }

    public String getFormula() {
        return formula;
    }

    @Override
    public String toString() {
        return type.getTitle()
                + "\nФормула: " + formula
                + "\nS = " + String.format("%.6f", s)
                + "\nσ = " + String.format("%.6f", sigma)
                + "\nR² = " + String.format("%.6f", r2)
                + (type == FunctionType.LINEAR
                ? "\nPearson = " + String.format("%.6f", pearson)
                : "")
                + "\n";
    }
}