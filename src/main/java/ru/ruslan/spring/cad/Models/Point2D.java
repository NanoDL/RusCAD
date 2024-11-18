package ru.ruslan.spring.cad.Models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import ru.ruslan.spring.cad.Interfaces.Selectable;

public class Point2D extends Circle implements Selectable {

    public Point2D(double x, double y) {
        super(x,y,3);
    }

    @Override
    public void select() {

    }

    @Override
    public void highlight() {
        super.setFill(Color.RED);
    }

    @Override
    public void deHighlight() {
        super.setFill(Color.BLACK);
    }

    @Override
    public boolean isNear(double x, double y) {
        return false;
    }

    @Override
    public void deSelect() {

    }
}
