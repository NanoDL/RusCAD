package ru.ruslan.spring.cad.Interfaces;

import java.util.List;

public interface Stylized {

    public void setupStyle(double width, List<Double> dashes, Double scale);
    public void updateStyle(double scale);
}
