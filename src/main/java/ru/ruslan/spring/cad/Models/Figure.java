package ru.ruslan.spring.cad.Models;

import javafx.scene.Group;
import javafx.scene.paint.Color;

import java.util.List;

public abstract class Figure extends Group {
    protected double width = 2;
    protected Color color = Color.BLACK;
    protected List<Double> coordinates;

   abstract void setRealCoordinates(List<Double> v);
   abstract List<Double> getRealCoordinates();
}
