package ru.ruslan.spring.cad.Models;

import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import ru.ruslan.spring.cad.Interfaces.*;

import java.util.List;

public class LineForSpline extends Figure implements Zoomable, Movable, Drawable, Selectable, Rotatable{
    private Line line;
    private double realX1, realY1, realX2, realY2;

    public LineForSpline(double x1, double y1, double x2, double y2){
        super();

        line = new Line(x1, y1, x2, y2);

        line.setStrokeWidth(width);
        super.getChildren().addAll(line);
    }

    @Override
    public void setRealCoordinates(List<Double> v){
        setRealX1(v.get(0));
        setRealY1(v.get(1));
        setRealX2(v.get(2));
        setRealY2(v.get(3));
    }

    @Override
    List<Double> getRealCoordinates() {
        return coordinates;
    }

    public double getRealX1() {
        return realX1;
    }

    public void setRealX1(double realX1) {
        this.realX1 = realX1;
    }

    public double getRealY1() {
        return realY1;
    }

    public void setRealY1(double realY1) {
        this.realY1 = realY1;
    }

    public double getRealX2() {
        return realX2;
    }

    public void setRealX2(double realX2) {
        this.realX2 = realX2;
    }

    public double getRealY2() {
        return realY2;
    }

    public void setRealY2(double realY2) {
        this.realY2 = realY2;
    }
    

    @Override
    public void shift(double differenceX, double differenceY) {
        line.setStartX(line.getStartX()+differenceX);
        line.setStartY(line.getStartY()+differenceY);
        line.setEndX(line.getEndX()+differenceX);
        line.setEndY(line.getEndY()+differenceY);
    }

    @Override
    public void move(List<Double> v) {

    }

    @Override
    public void move(double ...v){
        line.setStartX(v[0]);
        line.setStartY(v[1]);
        line.setEndX(v[2]);
        line.setEndY(v[3]);
    }

    @Override
    public void zoom(ScrollEvent scrollEvent, double delta) {
        double mouseX = scrollEvent.getX();
        double mouseY = scrollEvent.getY();

        double shiftXStart = line.getStartX() - mouseX;
        double shiftYStart = line.getStartY()- mouseY;
        double shiftXEnd = line.getEndX() - mouseX;
        double shiftYEnd = line.getEndY() - mouseY;

        double newStartX = mouseX + shiftXStart * delta;
        double newStartY = mouseY + shiftYStart * delta;
        double newEndX = mouseX + shiftXEnd * delta;
        double newEndY = mouseY + shiftYEnd * delta;

        move(newStartX,newStartY,newEndX,newEndY);
    }

    @Override
    public void draw(Pane canvas) {
        canvas.getChildren().addAll(this);
    }

    @Override
    public Figure select() {
        return this;
    }

    @Override
    public void highlight() {
        line.setStroke(Color.RED);
    }

    @Override
    public void deHighlight() {
        line.setStroke(Color.BLACK);
    }

    @Override
    public boolean isNear(double x, double y) {
        double x1 = line.getStartX();
        double y1 = line.getStartY();
        double x2 = line.getEndX();
        double y2 = line.getEndY();

        double r1 = CoordSystem.distance(x, y, x1, y1);
        double r2 = CoordSystem.distance(x, y, x2 ,y2);
        double r12 = CoordSystem.distance(x1, y1, x2, y2);

        double distance = 0;
        if (r1 > CoordSystem.distance(r2, r12,0,0)){
            distance = r1;
        } else if (r2 > CoordSystem.distance(r1, r12,0,0)){
            distance = r2;
        } else {
            distance = Math.abs((y2-y1)*x - (x2-x1)*y + x2*y1 - y2*x1) / Math.sqrt(Math.pow(y2-y1,2)+Math.pow(x2-x1,2));
        }

        return distance < 5;
    }

    @Override
    public void deSelect() {
        line.setStroke(Color.BLACK);
    }

    public void setStartX(double x){
        line.setStartX(x);
    }
    public void setEndX(double x){
        line.setEndX(x);
    }
    public void setStartY(double x){
        line.setStartY(x);
    }
    public void setEndY(double x){
        line.setEndY(x);
    }

    public double getStartX() {
        return line.getStartX();
    }

    public double getStartY() {
        return line.getStartY();
    }

    public double getEndX() {
        return line.getEndX();
    }

    public double getEndY() {
        return line.getEndY();
    }

    @Override
    public void rotate(double x, double y, double angle) {

    }

    public void setStrokeWidth(double width) {
        this.width = width;
        line.setStrokeWidth(width);
    }
    public void setStroke(Paint color) {
        line.setStroke(color);
    }

    @Override
    public String toString() {
        return "LineForSpline{" +
                "line=" + line +
                ", realX1=" + realX1 +
                ", realY1=" + realY1 +
                ", realX2=" + realX2 +
                ", realY2=" + realY2 +
                '}';
    }
}
