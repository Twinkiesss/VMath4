package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private final JTextArea inputArea;
    private final JTextArea resultArea;

    private final GraphPanel graphPanel;

    private final ApproximationService service;

    public MainFrame() {

        super("Лабораторная работа №4");

        service = new ApproximationService();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel();

        JButton loadButton =
                new JButton("Загрузить файл");

        JButton calculateButton =
                new JButton("Вычислить");

        JButton clearButton =
                new JButton("Очистить");

        topPanel.add(loadButton);
        topPanel.add(calculateButton);
        topPanel.add(clearButton);

        inputArea = new JTextArea();
        resultArea = new JTextArea();

        resultArea.setEditable(false);

        graphPanel = new GraphPanel();

        JScrollPane inputScroll =
                new JScrollPane(inputArea);

        JScrollPane resultScroll =
                new JScrollPane(resultArea);

        inputScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Исходные данные"
                )
        );

        resultScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Результаты"
                )
        );

        JSplitPane leftSplit =
                new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT,
                        inputScroll,
                        resultScroll
                );

        leftSplit.setDividerLocation(300);

        JSplitPane mainSplit =
                new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT,
                        leftSplit,
                        graphPanel
                );

        mainSplit.setDividerLocation(500);

        add(topPanel, BorderLayout.NORTH);
        add(mainSplit, BorderLayout.CENTER);

        loadButton.addActionListener(
                e -> loadFromFile()
        );

        calculateButton.addActionListener(
                e -> calculate()
        );

        clearButton.addActionListener(
                e -> clear()
        );

        fillExample();
    }
    private void fillExample() {

        inputArea.setText("""
11
0.0 0.000000
0.2 0.239923
0.4 0.477555
0.6 0.701809
0.8 0.887311
1.0 1.000000
1.2 1.017869
1.4 0.950054
1.6 0.830910
1.8 0.696882
2.0 0.571429
""");
    }

    private void clear() {

        inputArea.setText("");
        resultArea.setText("");

        graphPanel.setData(
                new ArrayList<>(),
                null
        );
    }

    private void loadFromFile() {

        JFileChooser chooser =
                new JFileChooser();

        int result =
                chooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file =
                chooser.getSelectedFile();

        try {

            List<Point> points =
                    FileManager.read(file);

            StringBuilder sb =
                    new StringBuilder();

            sb.append(points.size())
                    .append("\n");

            for (Point p : points) {

                sb.append(p.getX())
                        .append(" ")
                        .append(p.getY())
                        .append("\n");
            }

            inputArea.setText(sb.toString());

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private List<Point> readPoints()
            throws Exception {

        String[] lines =
                inputArea.getText()
                        .trim()
                        .split("\\R");

        if (lines.length < 2) {
            throw new Exception(
                    "Нет данных"
            );
        }

        int n =
                Integer.parseInt(
                        lines[0].trim()
                );

        if (n < 8 || n > 12) {

            throw new Exception(
                    "Количество точек должно быть от 8 до 12"
            );
        }

        if (lines.length - 1 != n) {

            throw new Exception(
                    "Количество строк не совпадает с n"
            );
        }

        List<Point> points =
                new ArrayList<>();

        for (int i = 1; i <= n; i++) {

            String[] parts =
                    lines[i]
                            .trim()
                            .split("\\s+");

            if (parts.length != 2) {

                throw new Exception(
                        "Неверный формат строки "
                                + i
                );
            }

            double x =
                    Double.parseDouble(
                            parts[0]
                    );

            double y =
                    Double.parseDouble(
                            parts[1]
                    );

            points.add(
                    new Point(x, y)
            );
        }

        return points;
    }
    private void calculate() {

        try {

            List<Point> points =
                    readPoints();

            List<ApproximationResult> results =
                    service.approximate(points);

            ApproximationResult best =
                    service.best(results);

            StringBuilder sb =
                    new StringBuilder();

            sb.append("ЛУЧШАЯ ФУНКЦИЯ\n");
            sb.append("========================\n");

            sb.append(
                    best.getType().getTitle()
            );

            sb.append("\n\n");

            for (ApproximationResult r : results) {

                sb.append(r);

                sb.append(
                        "\n========================\n"
                );
            }

            sb.append("\nОценка R²:\n");

            if (best.getR2() >= 0.95) {

                sb.append(
                        "Высокая точность аппроксимации"
                );

            } else if (best.getR2() >= 0.75) {

                sb.append(
                        "Хорошая точность аппроксимации"
                );

            } else if (best.getR2() >= 0.5) {

                sb.append(
                        "Удовлетворительная точность"
                );

            } else {

                sb.append(
                        "Низкая точность аппроксимации"
                );
            }

            resultArea.setText(
                    sb.toString()
            );

            graphPanel.setData(
                    points,
                    best
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}