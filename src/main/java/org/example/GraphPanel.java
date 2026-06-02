package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraphPanel extends JPanel {

    private List<Point> points;

    private ApproximationResult result;

    public void setData(
            List<Point> points,
            ApproximationResult result
    ) {
        this.points = points;
        this.result = result;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (points == null ||
                points.isEmpty() ||
                result == null) {
            return;
        }

        Graphics2D g2 =
                (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int w = getWidth();
        int h = getHeight();

        int left = 60;
        int right = 20;
        int top = 20;
        int bottom = 50;

        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;

        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Point p : points) {

            minX = Math.min(minX, p.getX());
            maxX = Math.max(maxX, p.getX());

            minY = Math.min(minY, p.getY());
            maxY = Math.max(maxY, p.getY());
        }

        double dx = (maxX - minX) * 0.1;
        double dy = (maxY - minY) * 0.1;

        minX -= dx;
        maxX += dx;

        minY -= dy;
        maxY += dy;

        g2.drawLine(
                left,
                h - bottom,
                w - right,
                h - bottom
        );

        g2.drawLine(
                left,
                top,
                left,
                h - bottom
        );

        g2.drawString(
                "X",
                w - right - 10,
                h - bottom + 20
        );

        g2.drawString(
                "Y",
                left - 20,
                top + 10
        );

        g2.setColor(Color.RED);

        for (Point p : points) {

            int px =
                    mapX(
                            p.getX(),
                            minX,
                            maxX,
                            left,
                            w - right
                    );

            int py =
                    mapY(
                            p.getY(),
                            minY,
                            maxY,
                            top,
                            h - bottom
                    );

            g2.fillOval(
                    px - 4,
                    py - 4,
                    8,
                    8
            );
        }

        g2.setColor(Color.BLUE);

        int prevX = -1;
        int prevY = -1;

        for (double x = minX;
             x <= maxX;
             x += (maxX - minX) / 500.0) {

            double y =
                    evaluate(
                            result,
                            x
                    );

            int px =
                    mapX(
                            x,
                            minX,
                            maxX,
                            left,
                            w - right
                    );

            int py =
                    mapY(
                            y,
                            minY,
                            maxY,
                            top,
                            h - bottom
                    );

            if (prevX != -1) {
                g2.drawLine(
                        prevX,
                        prevY,
                        px,
                        py
                );
            }

            prevX = px;
            prevY = py;
        }

        g2.setColor(Color.BLACK);

        g2.drawString(
                result.getType().getTitle(),
                left + 20,
                top + 20
        );
    }

    private int mapX(
            double x,
            double minX,
            double maxX,
            int left,
            int right
    ) {

        return left +
                (int) (
                        (x - minX)
                                /
                                (maxX - minX)
                                *
                                (right - left)
                );
    }

    private int mapY(
            double y,
            double minY,
            double maxY,
            int top,
            int bottom
    ) {

        return bottom -
                (int) (
                        (y - minY)
                                /
                                (maxY - minY)
                                *
                                (bottom - top)
                );
    }

    private double evaluate(
            ApproximationResult result,
            double x
    ) {

        double[] c =
                result.getCoefficients();

        switch (result.getType()) {

            case LINEAR:
                return c[0] * x + c[1];

            case QUADRATIC:
                return c[0]
                        + c[1] * x
                        + c[2] * x * x;

            case CUBIC:
                return c[0]
                        + c[1] * x
                        + c[2] * x * x
                        + c[3] * x * x * x;

            case EXPONENTIAL:
                return c[0] *
                        Math.exp(
                                c[1] * x
                        );

            case LOGARITHMIC:
                if (x <= 0) {
                    return Double.NaN;
                }
                return c[0]
                        *
                        Math.log(x)
                        + c[1];

            case POWER:
                if (x <= 0) {
                    return Double.NaN;
                }
                return c[0]
                        *
                        Math.pow(
                                x,
                                c[1]
                        );
        }

        return 0;
    }
}