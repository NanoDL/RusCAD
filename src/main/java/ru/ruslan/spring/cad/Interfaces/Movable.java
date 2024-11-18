package ru.ruslan.spring.cad.Interfaces;

import java.util.List;

public interface Movable {
    public void move(List<Double> v);
    public void move(double ...v);
    public void shift(double x, double y);
}
