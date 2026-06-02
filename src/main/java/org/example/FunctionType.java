package org.example;

public enum FunctionType {

    LINEAR("Линейная"),
    QUADRATIC("Квадратичная"),
    CUBIC("Кубическая"),
    EXPONENTIAL("Экспоненциальная"),
    LOGARITHMIC("Логарифмическая"),
    POWER("Степенная");

    private final String title;

    FunctionType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}