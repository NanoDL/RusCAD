package ru.ruslan.spring.cad.Models;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import ru.ruslan.spring.cad.Interfaces.Movable;
import ru.ruslan.spring.cad.Interfaces.Zoomable;

import java.util.ArrayList;
import java.util.List;

public class CoordSystem extends Group implements Movable, Zoomable {

    private double originX;
    private double originY;
    private Circle center;
    private Line horLine;
    private Line vertLine;

    private double scale = 1.0;

    private CoordMode coordMode;

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public CoordSystem(double height, double width){
        super();
        horLine = new Line(0, height/2, width, height/2);
        vertLine = new Line(width/2, 0, width/2, height);
        super.getChildren().add(horLine);
        super.getChildren().add(vertLine);

        originX = width/2;
        originY = height/2;
        center = new Circle(originX, originY, 2);

        super.getChildren().add(center);

        coordMode = CoordMode.DECART;
    }

    public double getOriginX() {
        return originX;
    }

    public double getOriginY() {
        return originY;
    }

    public void setOriginY(double originY) {
        this.originY = originY;
        center.setCenterY(originY);
    }

    public void setOriginX(double originX) {
        this.originX = originX;
        center.setCenterX(originX);
    }

    public Line getHorLine() {
        return horLine;
    }

    public Line getVertLine() {
        return vertLine;
    }


    @Override
    public void shift(double x, double y) {

        horLine.setStartY(horLine.getStartY()+y);
        horLine.setEndY(horLine.getEndY()+y);

        vertLine.setStartX(vertLine.getStartX()+x);
        vertLine.setEndX(vertLine.getEndX()+x);

        setOriginX(originX+x);
        setOriginY(originY+y);
    }

    @Override
    public void move(List<Double> v) {

    }

    @Override
    public void move(double ...v){
        if (v.length == 2){
            horLine.setStartY(v[1]);
            horLine.setEndY(v[1]);
            vertLine.setStartX(v[0]);
            vertLine.setEndX(v[0]);
        }

    }

   // private void repaint(){

   // }
    public void extendAxisX(double x){
        horLine.setEndX(horLine.getEndX()+x);
    }
    public void extendAxisY(double y){
        vertLine.setEndY(vertLine.getEndY()+y);
    }

    public List<Double> translatePointsToCoord(List<Point2D> points){
        List<Double> coord = new ArrayList<>();
        for (Point2D point2D : points){
            coord.add(point2D.getCenterX());
            coord.add(point2D.getCenterY());
        }
        return coord;
    }

    public static List<Double> translatePointsToCoord(Group points){
        List<Double> coord = new ArrayList<>();
        for (Node node : points.getChildren()){
            Point2D point2D = (Point2D) node;
            coord.add(point2D.getCenterX());
            coord.add(point2D.getCenterY());
        }
        return coord;
    }

    public double[] translateScreenToReal(double screenX, double screenY){
        double realX = (screenX - originX)/scale;
        double realY = (originY - screenY)/scale;

        return new double[] {realX,realY};
        //realx*factor + originX = screenX
        // originY - realY*factor  =screenY
    }
    public List<Double> translateScreenToReal(List<Double> coordinates){
        List<Double> newCoord = new ArrayList<>();

        for (int i = 0; i < coordinates.size()-1; i+=2) {
            double realX = (coordinates.get(i) - originX) / scale;
            double realY = (originY - coordinates.get(i + 1)) / scale;
            newCoord.addAll(List.of(realX, realY));
        }

        return newCoord;
        //realx*factor + originX = screenX
        // originY - realY*factor  =screenY
    }

    public double[] translateRealToScreen(double realX, double realY, double factor) {
        return translateRealToScreen(realX, realY);
    }

    public List<Double> translateRealToScreen(List<Double> coordinates){
        List<Double> newCoord = new ArrayList<>();
        for (int i = 0; i<coordinates.size()-1; i+=2 ){
            double screenX = originX + coordinates.get(i)*scale;
            double screenY = originY - coordinates.get(i+1)*scale;
            System.out.println(coordinates.get(i));
            System.out.println(coordinates.get(i+1));
            newCoord.add(screenX);
            newCoord.add(screenY);

        }

        return newCoord;
    }

    public double[] translateRealToScreen(double realX, double realY){
        double screenX = originX + realX*scale;
        double screenY = originY - realY*scale;

        return new double[] {screenX,screenY};
    }

    public static double[] rotateAround(double x, double y, double angle, double aroundX, double aroundY){
        double r = distance(x, y, aroundX, aroundY);

        double alpha = Math.atan2(y - aroundY, x - aroundX);

        double angleRad = Math.toRadians(angle);

        double newAngle = alpha + angleRad;

        double X_new = aroundX + r * Math.cos(newAngle);
        double Y_new = aroundY + r * Math.sin(newAngle);

        return new double[]{X_new, Y_new};


        /*double angleRad = Math.toRadians(angle);

        double x_rel = x - aroundX;
        double y_rel = y - aroundY;

        double x_rot = x_rel*Math.cos(angleRad) - y_rel*Math.sin(angleRad);
        double y_rot = x_rel*Math.sin(angleRad) + y_rel*Math.cos(angleRad);

        return new double[]{x_rot + aroundX, y_rot + aroundY};*/
    }

    public static double distance(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
    }


    public CoordMode getCoordMode() {
        return coordMode;
    }

    public void setCoordMode(CoordMode coordMode) {
        this.coordMode = coordMode;
    }

    public void changeCoordMode(){
        if (coordMode == CoordMode.DECART){
            coordMode = CoordMode.POLAR;
        } else {
            coordMode = CoordMode.DECART;
        }
    }

    @Override
    public void zoom(ScrollEvent scrollEvent, double delta) {
        double mouseX = scrollEvent.getX();
        double mouseY = scrollEvent.getY();

        double shiftX = originX - mouseX;
        double shiftY = originY - mouseY;

        double newOriginX = mouseX + shiftX * delta;
        double newOriginY = mouseY + shiftY * delta;

        setOriginX(newOriginX);
        setOriginY(newOriginY);
        move(newOriginX, newOriginY);
    }
}
