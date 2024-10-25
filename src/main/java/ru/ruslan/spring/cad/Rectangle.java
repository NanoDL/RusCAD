package ru.ruslan.spring.cad;

import javafx.scene.Group;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

public class Rectangle extends Group implements Drawable, Zoomable, Movable{

    private MyLine line1;
    private MyLine line2;
    private MyLine line3;
    private MyLine line4;

    private double startX, startY;
    private double endX, endY;

    public Rectangle(double x1, double y1, double x2, double y2) {
        super();
        line1 = new MyLine(x1, y1, x2, y1);
        line2 = new MyLine(x2, y1, x2, y2);
        line3 = new MyLine(x2, y2, x1, y2);
        line4 = new MyLine(x1, y2, x1, y1);
        super.getChildren().addAll(line1,line2,line3,line4);
        super.getChildren().addAll(line1.getStartPoint(),line1.getEndPoint(),line2.getStartPoint(),line2.getEndPoint(),line3.getStartPoint(),line3.getEndPoint(),line4.getStartPoint(),line4.getEndPoint());
    }


    @Override
    public void draw(Pane canvas) {
        canvas.getChildren().add(this);
    }

    @Override
    public void move(double x, double y) {

    }

    @Override
    public void shift(double x, double y) {
        line1.shift(x,y);
        line2.shift(x,y);
        line3.shift(x,y);
        line4.shift(x,y);
    }

    @Override
    public void zoom(ScrollEvent scrollEvent, double delta) {
        line1.zoom(scrollEvent, delta);
        line2.zoom(scrollEvent, delta);
        line3.zoom(scrollEvent, delta);
        line4.zoom(scrollEvent, delta);
    }
}
