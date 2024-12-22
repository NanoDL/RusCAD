package ru.ruslan.spring.cad.Models;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import ru.ruslan.spring.cad.Interfaces.*;
import ru.ruslan.spring.cad.Mode;

import java.util.ArrayList;
import java.util.List;

public class Polygon extends Figure implements Movable, Drawable, Selectable, Zoomable, Divided {

    private Group lines;
    private double radius;
    private double linesNumber;

    public Polygon(List<Double> coord,double linesNumber,  Mode mode){

        this(coord.get(0), coord.get(1), coord.get(2), coord.get(3), linesNumber, mode);

    }
    public Polygon(List<Double> coord,double radius, double linesNumber,  Mode mode){
        super();
        if (linesNumber >= 3){
            setLinesNumber(linesNumber);
            lines = new Group();
            if (mode == Mode.DRAW_POLYGON_IN){
                lines = createInPolygon(coord.get(0), coord.get(1), radius, linesNumber);

            } else if (mode == Mode.DRAW_POLYGON_OUT){
                lines = createOutPolygon(coord.get(0), coord.get(1), radius, linesNumber);
            }
            this.getChildren().add(lines);
        }
    }

    public Polygon(double centX, double centY, double x, double y, double linesNumber, Mode mode){
        super();
        if (linesNumber >= 3){
            setLinesNumber(linesNumber);
            lines = new Group();
            radius = CoordSystem.distance(centX, centY, x, y);
            if (mode == Mode.DRAW_POLYGON_IN){
                lines = createInPolygon(centX, centY, x, y, linesNumber);

            } else if (mode == Mode.DRAW_POLYGON_OUT){
                lines = createOutPolygon(centX, centY, x, y, linesNumber);
            }
            this.getChildren().add(lines);
        }

    }

    private Group createInPolygon(double centX, double centY, double x, double y, double linesNumber){
        Group newLines = new Group();

        double startAngle = Math.toDegrees(Math.atan2((y-centY), x - centX));
        double angleStep = 360.0 / linesNumber;
        //System.out.println(linesNumber);
        double tempX = x;
        double tempY = y;

        double nextAngle = startAngle;
        for (int i = 0; i < linesNumber-1; i++){
            nextAngle += angleStep;
            double nextX = centX + radius * Math.cos(Math.toRadians(nextAngle));
            double nextY = centY + radius * Math.sin(Math.toRadians(nextAngle));
            newLines.getChildren().add(new MyLine(tempX, tempY, nextX, nextY));
            tempX = nextX;
            tempY = nextY;
        }
        newLines.getChildren().add(new MyLine(tempX,tempY, x, y));
        return newLines;
    }

    private Group createOutPolygon(double centX, double centY, double x, double y, double linesNumber){
        Group polygon = new Group();

        double insideAngle = (180.0 * (linesNumber - 2)) / linesNumber;

        double polySide = 2 * (radius / Math.tan(Math.toRadians(insideAngle / 2)));

        Double[] vectorR = {x - centX, y - centY};
        Double[] vectorR1 = new Double[]{vectorR[0]/radius, vectorR[1]/radius};

        Double[] vectorSide = {-vectorR1[1], vectorR1[0]};

        double vershX = (x - (polySide/2.0) * vectorSide[0]);
        double vershY = (y - (polySide/2.0) * vectorSide[1]);

        double startAngle = Math.toDegrees(Math.atan2((vershY-centY), vershX - centX));
        double angleStep = 360.0 / linesNumber;
        double tempX = vershX;
        double tempY = vershY;

        double distance = CoordSystem.distance(centX,centY,vershX,vershY);

        double nextAngle = startAngle;
        for (int i = 0; i < linesNumber-1; i++){
            nextAngle += angleStep;
            double nextX = centX + distance * Math.cos(Math.toRadians(nextAngle));
            double nextY = centY + distance * Math.sin(Math.toRadians(nextAngle));
            polygon.getChildren().add(new MyLine(tempX, tempY, nextX, nextY));
            tempX = nextX;
            tempY = nextY;
        }
        polygon.getChildren().add(new MyLine(tempX,tempY, vershX, vershY));
        return polygon;
    }

    private Group createInPolygon(double centX, double centY, double radius, double linesNumber) {
        Group newLines = new Group();
        double angleStep = 360.0 / linesNumber;
        double startAngle = 0; // Начальный угол можно задать как 0

        double tempX = centX + radius * Math.cos(Math.toRadians(startAngle));
        double tempY = centY + radius * Math.sin(Math.toRadians(startAngle));

        for (int i = 1; i <= linesNumber; i++) {
            double nextAngle = startAngle + i * angleStep;
            double nextX = centX + radius * Math.cos(Math.toRadians(nextAngle));
            double nextY = centY + radius * Math.sin(Math.toRadians(nextAngle));

            newLines.getChildren().add(new MyLine(tempX, tempY, nextX, nextY));
            tempX = nextX;
            tempY = nextY;
        }

        return newLines;
    }

    private Group createOutPolygon(double centX, double centY, double radius, double linesNumber) {
        Group polygon = new Group();

        double insideAngle = (180.0 * (linesNumber - 2)) / linesNumber;
        double polySide = 2 * (radius / Math.tan(Math.toRadians(insideAngle / 2)));

        Double[] vectorR = {radius, 0.0};
        Double[] vectorSide = {-vectorR[1] / radius, vectorR[0] / radius};

        double vershX = centX + radius - (polySide / 2.0) * vectorSide[0];
        double vershY = centY - (polySide / 2.0) * vectorSide[1];

        double angleStep = 360.0 / linesNumber;
        double startAngle = Math.toDegrees(Math.atan2((vershY - centY), vershX - centX));

        double tempX = vershX;
        double tempY = vershY;
        double distance = CoordSystem.distance(centX, centY, vershX, vershY);

        for (int i = 1; i <= linesNumber; i++) {
            double nextAngle = startAngle + i * angleStep;
            double nextX = centX + distance * Math.cos(Math.toRadians(nextAngle));
            double nextY = centY + distance * Math.sin(Math.toRadians(nextAngle));

            polygon.getChildren().add(new MyLine(tempX, tempY, nextX, nextY));
            tempX = nextX;
            tempY = nextY;
        }

        return polygon;
    }


    public double getLinesNumber() {
        return linesNumber;
    }

    public void setLinesNumber(double linesNumber) {
        this.linesNumber = linesNumber;
    }

    @Override
    public void draw(Pane canvas) {
        canvas.getChildren().add(this);
    }

    @Override
    public void move(List<Double> v) {

    }

    @Override
    public void move(double... v) {

    }

    @Override
    public void shift(double x, double y) {
        for (int i = 0; i < lines.getChildren().size(); i++){
            MyLine line = (MyLine) lines.getChildren().get(i);
            line.shift(x, y);
        }
    }



    @Override
    public Figure select() {
        return this;
    }

    @Override
    public void highlight() {

    }

    @Override
    public void deHighlight() {

    }

    @Override
    public boolean isNear(double x, double y) {
        return false;
    }

    @Override
    public void deSelect() {

    }

    @Override
    public void zoom(ScrollEvent scrollEvent, double delta) {
        for (Node node : lines.getChildren()){
            ((MyLine) node).zoom(scrollEvent, delta);
        }
    }

    @Override
    public void setRealCoordinates(List<Double> v) {

    }

    @Override
    public List<Double> getRealCoordinates() {
        return List.of();
    }

    @Override
    public Figure[] divide() {
        return new Figure[0];
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
