package ru.ruslan.spring.cad.Models;

import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import ru.ruslan.spring.cad.Interfaces.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Rectangle extends Figure implements Drawable, Zoomable, Movable, Selectable, Divided, Stylized {

    private MyLine line1;
    private MyLine line2;
    private MyLine line3;
    private MyLine line4;

    // List<Double> coordinates;

    /*private double startRealX, startRealY;
    private double endRealX, endRealY;*/

    public Rectangle(Double ...coord){
        super();
        coordinates = Arrays.asList(coord);
        line1 = new MyLine(coord[0], coord[1], coord[2], coord[3]);
        line2 = new MyLine(coord[2], coord[3], coord[4], coord[5]);
        line3 = new MyLine(coord[4], coord[5], coord[6], coord[7]);
        line4 = new MyLine(coord[6], coord[7], coord[0], coord[1]);


        super.getChildren().addAll(line1, line2, line3, line4);
    }

    public Rectangle(double x1, double y1, double x2, double y2) {
        super();
        line1 = new MyLine(x1, y1, x2, y1);
        line2 = new MyLine(x2, y1, x2, y2);
        line3 = new MyLine(x2, y2, x1, y2);
        line4 = new MyLine(x1, y2, x1, y1);

        super.getChildren().addAll(line1, line2, line3, line4);
    }

    public List<Double> getScreenCoordinates(){
        List<Double> coord = new ArrayList<>();
        Point2D point = line1.getStartPoint();
        coord.addAll(List.of(point.getCenterX(), point.getCenterY()));
        point = line2.getStartPoint();
        coord.addAll(List.of(point.getCenterX(), point.getCenterY()));
        point = line3.getStartPoint();
        coord.addAll(List.of(point.getCenterX(), point.getCenterY()));
        point = line4.getStartPoint();
        coord.addAll(List.of(point.getCenterX(), point.getCenterY()));
        return coord;
    }

    public void setRealCoordinates(List<Double> v){
        coordinates = v;

    };


    public List<Double> getRealCoordinates(){
        return coordinates;
    }



    @Override
    public void draw(Pane canvas) {
        canvas.getChildren().add(this);
    }

    // здесь только поменять

    @Override
    public void move(double ...v) {
        line1.move(v[0],v[1],v[2],v[3]);
        line2.move(v[2],v[3],v[4],v[5]);
        line3.move(v[4],v[5],v[6],v[7]);
        line4.move(v[6],v[7],v[0],v[1]);
    }

    @Override
    public void move(List<Double> v){
        line1.move(v.get(0),v.get(1),v.get(2),v.get(3));
        line2.move(v.get(2),v.get(3),v.get(4),v.get(5));
        line3.move(v.get(4),v.get(5),v.get(6),v.get(7));
        line4.move(v.get(6),v.get(7),v.get(0),v.get(1));

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

    @Override
    public Figure select() {
        line1.select();
        line2.select();
        line3.select();
        line4.select();
        return this;
    }

    @Override
    public void highlight() {
        line1.highlight();
        line2.highlight();
        line3.highlight();
        line4.highlight();
    }

    @Override
    public void deHighlight() {
        line1.deHighlight();
        line2.deHighlight();
        line3.deHighlight();
        line4.deHighlight();
    }

    @Override
    public boolean isNear(double x, double y) {
        return line1.isNear(x, y) || line2.isNear(x, y) || line3.isNear(x, y) || line4.isNear(x, y);
    }

    @Override
    public void deSelect() {

    }

    // и здесь
    @Override
    public Figure[] divide() {
        line1.setRealCoordinates(coordinates.subList(0,4));
        line2.setRealCoordinates(coordinates.subList(2,6));
        line3.setRealCoordinates(coordinates.subList(4,8));

        List<Double> subList = new ArrayList<>(coordinates.subList(6, 8));
        subList.addAll(coordinates.subList(0, 3));
        line4.setRealCoordinates(subList);

        return new Figure[] {line1, line2, line3, line4};
    }

    @Override
    public void setupStyle(double width, List<Double> dashes, Double scale) {
        this.width = width;
        line1.setupStyle(width, dashes, scale);
        line2.setupStyle(width, dashes, scale);
        line3.setupStyle(width, dashes, scale);
        line4.setupStyle(width, dashes, scale);
    }

    @Override
    public void updateStyle(double scale) {
        line2.updateStyle(scale);
        line1.updateStyle(scale);
        line3.updateStyle(scale);
        line4.updateStyle(scale);
    }
}
