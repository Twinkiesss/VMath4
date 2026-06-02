package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static List<Point> read(File file)
            throws IOException {

        List<Point> points =
                new ArrayList<>();

        try (BufferedReader br =
                     new BufferedReader(
                             new FileReader(file))) {

            int n =
                    Integer.parseInt(
                            br.readLine()
                    );

            for (int i = 0; i < n; i++) {

                String[] parts =
                        br.readLine()
                                .trim()
                                .split("\\s+");

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
        }

        return points;
    }
}