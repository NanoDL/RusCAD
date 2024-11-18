package ru.ruslan.spring.cad.Models;


import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import ru.ruslan.spring.cad.Interfaces.Drawable;
import ru.ruslan.spring.cad.Interfaces.Movable;
import ru.ruslan.spring.cad.Interfaces.Selectable;
import ru.ruslan.spring.cad.Interfaces.Zoomable;

import java.util.List;


public class MyCircle extends Figure implements Drawable, Movable, Zoomable, Selectable {

    Point2D center;

    Circle circle;

    double realCentX;
    double realCentY;
    double realRadius;

    public double getRealRadius() {
        return realRadius;
    }

    public void setRealRadius(double realRadius) {
        this.realRadius = realRadius;
    }



    public Point2D getCenter() {
        return center;
    }

    public void setCenter(Point2D center) {
        this.center = center;
    }



    public MyCircle(double x, double y, double radius) {
        super();
        circle = new Circle(x, y, radius, Color.TRANSPARENT);
        circle.setStroke(Color.BLACK);
        /*centerX = x;
        centerY = y;*/
        circle.setRadius(radius);
        circle.setStrokeWidth(width);
        center = new Point2D(x, y);
        super.getChildren().addAll(circle, center);
    }

    @Override
    public void setRealCoordinates(List<Double> v){
        coordinates = v;
        realCentX = v.get(0);
        realCentY = v.get(1);
    }

    @Override
    List<Double> getRealCoordinates() {
        return coordinates;
    }

    @Override
    public void draw(Pane canvas) {
        canvas.getChildren().add(this);
    }
    public double getRealCentX() {
        return realCentX;
    }

    public void setRealCentX(double realCentX) {
        this.realCentX = realCentX;
    }

    public double getRealCentY() {
        return realCentY;
    }

    public void setRealCentY(double realCentY) {
        this.realCentY = realCentY;
    }

    public void setRadius(double r){
        circle.setRadius(r);
    }

    @Override
    public void move(List<Double> v) {

    }

    @Override
    public void move(double ...v) {
        if (v.length == 2){
            /*centerX = v[0];
            centerY = v[1];*/
            circle.setCenterX(v[0]);
            circle.setCenterY(v[1]);

            center.setCenterX(v[0]);
            center.setCenterY(v[1]);
        }

    }

    @Override
    public void shift(double x, double y) {
        /*centerX += x;
        centerY += y;*/

        circle.setCenterX(circle.getCenterX() + x);
        circle.setCenterY(circle.getCenterY() + y);

        center.setCenterX(circle.getCenterX() + x);
        center.setCenterY(circle.getCenterY() + y);


    }

    @Override
    public void zoom(ScrollEvent scrollEvent, double delta) {

        double mouseX = scrollEvent.getX();
        double mouseY = scrollEvent.getY();

        double shiftX = circle.getCenterX() - mouseX;
        double shiftY = circle.getCenterY() - mouseY;

        double newCenterX = mouseX + shiftX * delta;
        double newCenterY = mouseY + shiftY * delta;

        /*this.radius = super.getRadius() * delta;*/
        circle.setRadius(circle.getRadius() * delta);
        move(newCenterX,newCenterY);
    }

    @Override
    public void select() {

    }

    @Override
    public void highlight() {
        circle.setStroke(Color.RED);
    }

    @Override
    public void deHighlight() {
        circle.setStroke(Color.BLACK);
    }

    @Override
    public boolean isNear(double x, double y) {
        double r = CoordSystem.distance(x, y, circle.getCenterX(), circle.getCenterY());

        return Math.abs(circle.getRadius() - r) < 5;
    }

    @Override
    public void deSelect() {

    }
}
