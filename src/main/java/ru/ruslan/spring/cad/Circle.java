package ru.ruslan.spring.cad;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Circle extends javafx.scene.shape.Circle implements Drawable, Movable, Zoomable{

    double centerX;
    double centerY;
    double radius;
    Point2D center;

    public Circle(double x, double y, double radius) {
        super(x, y, radius, Color.TRANSPARENT);
        super.setStroke(Color.BLACK);
        super.setStrokeWidth(1);
        centerX = x;
        centerY = y;
        this.radius = radius;
        center = new Point2D(centerX, centerY);
    }

    @Override
    public void draw(Pane canvas) {
        canvas.getChildren().add(this);
        canvas.getChildren().add(center);
    }

    @Override
    public void move(double x, double y) {
        centerX = x;
        centerY = y;
        super.setCenterX(centerX);
        super.setCenterY(centerY);
        center.setCenterX(centerX);
        center.setCenterY(centerY);
    }

    @Override
    public void shift(double x, double y) {
        centerX += x;
        centerY += y;

        super.setCenterX(centerX);
        super.setCenterY(centerY);

        center.setCenterX(centerX);
        center.setCenterY(centerY);


    }

    @Override
    public void zoom(ScrollEvent scrollEvent, double delta) {

        double mouseX = scrollEvent.getX();
        double mouseY = scrollEvent.getY();

        double shiftX = centerX - mouseX;
        double shiftY = centerY - mouseY;

        double newCenterX = mouseX + shiftX * delta;
        double newCenterY = mouseY + shiftY * delta;

        double newRadius = super.getRadius() * delta;
        this.radius = newRadius;
        super.setRadius(radius);
        move(newCenterX,newCenterY);


    }
}
