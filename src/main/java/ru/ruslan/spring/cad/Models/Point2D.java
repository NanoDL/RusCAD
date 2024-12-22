package ru.ruslan.spring.cad.Models;

import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import ru.ruslan.spring.cad.Interfaces.Movable;
import ru.ruslan.spring.cad.Interfaces.Selectable;
import ru.ruslan.spring.cad.Interfaces.Zoomable;

import java.util.List;

public class Point2D extends Circle implements Selectable, Zoomable, Movable {

    public Point2D(double x, double y) {
        super(x,y,3);
    }

    @Override
    public Figure select() {
        LineForSpline line = new LineForSpline(1,1,1,1);
        return line;
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
        double r = CoordSystem.distance(x, y, this.getCenterX(), this.getCenterY());

        return Math.abs(this.getRadius() - r) < 10;
    }

    @Override
    public void deSelect() {

    }

    public void setClickable(boolean bool){
        if (bool){
            this.setRadius(10);

        }else {
            this.setRadius(3);
        }
    }

    @Override
    public void zoom(ScrollEvent scrollEvent, double delta) {
        double mouseX = scrollEvent.getX();
        double mouseY = scrollEvent.getY();

        double shiftX = super.getCenterX() - mouseX;
        double shiftY = super.getCenterY() - mouseY;

        double newX = mouseX + shiftX * delta;
        double newY = mouseY + shiftY * delta;
        super.setCenterX(newX);
        super.setCenterY(newY);
    }

    @Override
    public void move(List<Double> v) {

    }

    @Override
    public void move(double... v) {

    }

    @Override
    public void shift(double x, double y) {
        this.setCenterX(this.getCenterX()+x);
        this.setCenterY(this.getCenterY()+y);
    }
}
