package ru.ruslan.spring.cad.Models;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import ru.ruslan.spring.cad.Interfaces.*;

import java.util.ArrayList;
import java.util.List;

public class BezieSpline extends Figure implements Movable, Drawable, Selectable, Zoomable {

    private Group spline;
    private Point2D startPoint;
    private LineForSpline kasLineSt;
    private Point2D controlPointStart;
    private Point2D controlPointEnd;
    private LineForSpline kasLineEnd;
    private Point2D endPoint;

    public BezieSpline(List<Double> coord){
        super();
        kasLineSt = new LineForSpline(coord.get(0), coord.get(1), coord.get(2), coord.get(3));
        kasLineEnd = new LineForSpline(coord.get(6), coord.get(7), coord.get(4), coord.get(5));

        spline = createBezierCurve(coord);
        startPoint = new Point2D(coord.get(0), coord.get(1));

        controlPointStart = new Point2D(coord.get(2), coord.get(3));
        controlPointStart.setRadius(10);
        controlPointStart.setFill(Color.rgb(160,160,160));
        controlPointEnd = new Point2D(coord.get(4), coord.get(5));
        controlPointEnd.setFill(Color.rgb(160,160,160));
        controlPointEnd.setRadius(10);

        endPoint = new Point2D(coord.get(6), coord.get(7));
        super.getChildren().addAll(spline,startPoint,endPoint);
        setControlPointsHandlers();
    }

    private Group createBezierCurve(List<Double> coord) {
        int stepsCount = 100; // Шаг для параметра t
        Group root = new Group();
        List<Double> P0 = coord.subList(0,2);
        List<Double> P1 = coord.subList(2,4);
        List<Double> P2 = coord.subList(4,6);
        List<Double> P3 = coord.subList(6,8);

        List<Double> prevPoint = coord.subList(0,2); // Предыдущая точка (начинаем с P0)
        for (int i = 0; i <= stepsCount; i += 1) {
            double t  = (double) i /stepsCount;
            List<Double> currentPoint = calculateBezierPoint(t, P0, P1, P2, P3);
            LineForSpline line = new LineForSpline(prevPoint.get(0), prevPoint.get(1), currentPoint.get(0), currentPoint.get(1));// Цвет линии
            line.setStrokeWidth(width);
            root.getChildren().add(line);
            prevPoint = currentPoint; // Обновляем предыдущую точку
        }
        return root;
    }

    /*private Group createBezierCurve(List<Point2D> points) {
        int stepsCount = 100; // Шаг для параметра t
        Group root = new Group();

        Point2D P0 = points.get(0);
        Point2D P1 = points.get(1);
        Point2D P2 = points.get(2);
        Point2D P3 = points.get(3);
        Point2D prevPoint = points.get(0); // Предыдущая точка (начинаем с P0)

        kasLineSt.move(P0.get(0), P0.get(1), P1.get(0),P1.get(1));
        kasLineEnd.move(P3.get(0), P3.get(1), P2.get(0),P2.get(1));

        for (int i = 0; i <= stepsCount; i += 1) {
            double t  = (double) i /stepsCount;
            Point2D currentPoint = calculateBezierPoint(t, P0, P1, P2, P3);
            LineForSpline line = new LineForSpline(prevPoint.getCenterX(), prevPoint.getCenterY(), currentPoint.getCenterX(), currentPoint.getCenterY());// Цвет линии
            line.setStrokeWidth(width);
            root.getChildren().add(line);
            prevPoint = currentPoint; // Обновляем предыдущую точку
        }
        return root;
    }*/

    private Point2D calculateBezierPoint (double t, Point2D P0, Point2D P1, Point2D P2, Point2D P3){
        double x = Math.pow(1 - t, 3) * P0.getCenterX() +
                3 * Math.pow(1 - t, 2) * t * P1.getCenterX() +
                3 * (1 - t) * Math.pow(t, 2) * P2.getCenterX() +
                Math.pow(t, 3) * P3.getCenterX();

        double y = Math.pow(1 - t, 3) * P0.getCenterY() +
                3 * Math.pow(1 - t, 2) * t * P1.getCenterY() +
                3 * (1 - t) * Math.pow(t, 2) * P2.getCenterY() +
                Math.pow(t, 3) * P3.getCenterY();

        return new Point2D(x, y);
    }

    private List<Double> calculateBezierPoint (double t, List<Double> P0, List<Double> P1, List<Double> P2, List<Double> P3){
        double x = Math.pow(1 - t, 3) * P0.get(0) +
                3 * Math.pow(1 - t, 2) * t * P1.get(0) +
                3 * (1 - t) * Math.pow(t, 2) * P2.get(0) +
                Math.pow(t, 3) * P3.get(0);

        double y = Math.pow(1 - t, 3) * P0.get(1) +
                3 * Math.pow(1 - t, 2) * t * P1.get(1) +
                3 * (1 - t) * Math.pow(t, 2) * P2.get(1) +
                Math.pow(t, 3) * P3.get(1);

        return List.of(x, y);
    }

    private void setControlPointsHandlers(){
        controlPointStart.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                controlPointStart.setUserData(mouseEvent);
            }
        });
        controlPointStart.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                controlPointHandler(mouseEvent, kasLineSt);
                BezieSpline.super.getChildren().remove(spline);
                spline = createBezierCurve(List.of(startPoint.getCenterX(),startPoint.getCenterY(),
                        controlPointStart.getCenterX(), controlPointStart.getCenterY(),
                        controlPointEnd.getCenterX(), controlPointEnd.getCenterY(),
                        endPoint.getCenterX(), endPoint.getCenterY()));
                BezieSpline.super.getChildren().add(spline);

            }
        });
        controlPointEnd.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                controlPointEnd.setUserData(mouseEvent);
            }
        });
        controlPointEnd.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                controlPointHandler(mouseEvent, kasLineEnd);
                BezieSpline.super.getChildren().remove(spline);
                spline = createBezierCurve(List.of(startPoint.getCenterX(),startPoint.getCenterY(),
                        controlPointStart.getCenterX(), controlPointStart.getCenterY(),
                        controlPointEnd.getCenterX(), controlPointEnd.getCenterY(),
                        endPoint.getCenterX(), endPoint.getCenterY()));
                BezieSpline.super.getChildren().add(spline);
            }
        });
    }

    private void controlPointHandler(MouseEvent mouseEvent, LineForSpline line){
        Point2D point = (Point2D) mouseEvent.getSource();
        MouseEvent mouseEvent1 = (MouseEvent) point.getUserData();
        double raznicaX = mouseEvent.getX() - mouseEvent1.getX();
        double raznicaY = mouseEvent.getY() - mouseEvent1.getY();
        point.shift(raznicaX, raznicaY);
        line.setEndX(point.getCenterX());
        line.setEndY(point.getCenterY());
        point.setUserData(mouseEvent);
    }

    @Override
    public void setRealCoordinates(List<Double> v) {
        coordinates = v;
        System.out.println(coordinates);
    }

    @Override
    List<Double> getRealCoordinates() {
        return coordinates;
    }

    @Override
    public void draw(Pane canvas) {
        canvas.getChildren().add(this);
    }

    @Override
    public void move(List<Double> v) {
        startPoint.setCenterX(v.get(0));
        startPoint.setCenterY(v.get(1));
        kasLineSt.move(List.of(v.get(0), v.get(1), v.get(2), v.get(3)));
        controlPointStart.setCenterX(v.get(2));
        controlPointStart.setCenterY(v.get(3));
        controlPointEnd.setCenterX(v.get(4));
        controlPointEnd.setCenterY(v.get(5));
        kasLineEnd.move(List.of(v.get(6), v.get(7), v.get(4), v.get(5)));
        endPoint.setCenterX(v.get(6));
        endPoint.setCenterY(v.get(7));

        this.getChildren().remove(spline);
        spline = createBezierCurve(v);

        this.getChildren().add(spline);


    }

    @Override
    public void move(double... v) {

    }

    @Override
    public void shift(double x, double y) {
        startPoint.setCenterX(startPoint.getCenterX()+x);
        startPoint.setCenterY(startPoint.getCenterY()+y);
        controlPointStart.setCenterX(controlPointStart.getCenterX()+x);
        controlPointStart.setCenterY(controlPointStart.getCenterY()+y);
        controlPointEnd.setCenterX(controlPointEnd.getCenterX()+x);
        controlPointEnd.setCenterY(controlPointEnd.getCenterY()+y);
        kasLineSt.shift(x, y);
        kasLineEnd.shift(x, y);
        endPoint.setCenterX(endPoint.getCenterX()+x);
        endPoint.setCenterY(endPoint.getCenterY()+y);
        for (int i = 0; i < spline.getChildren().size(); i++){
            LineForSpline line = (LineForSpline) spline.getChildren().get(i);
            line.shift(x, y);
        }
    }

    @Override
    public Figure select() {
        super.getChildren().addAll(kasLineSt, kasLineEnd, controlPointEnd, controlPointStart);
        return null;
    }

    @Override
    public void highlight() {
        for (int i = 0; i < spline.getChildren().size(); i++) {
            LineForSpline line = (LineForSpline) spline.getChildren().get(i);
            line.highlight();
        }
    }

    @Override
    public void deHighlight() {
        for (int i = 0; i < spline.getChildren().size(); i++) {
            LineForSpline line = (LineForSpline) spline.getChildren().get(i);
            line.deHighlight();
        }
    }

    @Override
    public boolean isNear(double x, double y) {
        for (int i = 0; i < spline.getChildren().size(); i++){
            LineForSpline line = (LineForSpline) spline.getChildren().get(i);
            if (line.isNear(x,y)){
                return true;
            };

        }
        return false;
    }

    @Override
    public void deSelect() {
        super.getChildren().removeAll(controlPointEnd, controlPointStart, kasLineSt, kasLineEnd);
    }

    @Override
    public void zoom(ScrollEvent scrollEvent, double delta) {
        kasLineSt.zoom(scrollEvent,delta);
        kasLineEnd.zoom(scrollEvent,delta);

        double mouseX = scrollEvent.getX();
        double mouseY = scrollEvent.getY();

        double shiftXStart = startPoint.getCenterX() - mouseX;
        double shiftYStart = startPoint.getCenterY()- mouseY;

        double shiftXCtrlStart = controlPointStart.getCenterX()- mouseX;
        double shiftYCtrlStart = controlPointStart.getCenterY()- mouseY;
        double shiftXCtrlEnd = controlPointEnd.getCenterX()- mouseX;
        double shiftYCtrlEnd = controlPointEnd.getCenterY()- mouseY;

        double shiftXEnd = endPoint.getCenterX() - mouseX;
        double shiftYEnd = endPoint.getCenterY() - mouseY;

        double newStartX = mouseX + shiftXStart * delta;
        double newStartY = mouseY + shiftYStart * delta;

        double newXCtrlStart = mouseX + shiftXCtrlStart * delta;
        double newYCtrlStart = mouseY + shiftYCtrlStart * delta;
        double newXCtrlEnd = mouseX + shiftXCtrlEnd * delta;
        double newYCtrlEnd = mouseY + shiftYCtrlEnd * delta;

        double newEndX = mouseX + shiftXEnd * delta;
        double newEndY = mouseY + shiftYEnd * delta;

        move(List.of(newStartX, newStartY, newXCtrlStart, newYCtrlStart, newXCtrlEnd, newYCtrlEnd, newEndX, newEndY));

    }
}
