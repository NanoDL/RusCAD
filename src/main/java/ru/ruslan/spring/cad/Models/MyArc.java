package ru.ruslan.spring.cad.Models;

import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Arc;
import javafx.scene.paint.Color;

import javafx.scene.shape.ArcType;
import ru.ruslan.spring.cad.Interfaces.Drawable;
import ru.ruslan.spring.cad.Interfaces.Movable;
import ru.ruslan.spring.cad.Interfaces.Selectable;
import ru.ruslan.spring.cad.Interfaces.Stylized;
import ru.ruslan.spring.cad.Interfaces.Zoomable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyArc extends Figure implements Movable, Zoomable, Selectable, Drawable, Stylized {
    private Arc arc;
    private Point2D startPoint;
    private Point2D endPoint;
    private Point2D center;

    double realRadius;
    private double realArcLength; // Реальная длина дуги в градусах

    private double width;

    public MyArc(double x1, double y1, double x2, double y2, double centerX, double centerY) {
        super();
        startPoint = new Point2D(x1,y1);
        //endPoint = new Point2D(x2,y2);
        center = new Point2D(centerX, centerY);
        realRadius = CoordSystem.distance(x1, y1, centerX, centerY);
        // Вектор хорды
        double chordX = x2 - x1;
        double chordY = (y2 - y1);

        // Уравнение продолжения хорды
        double t = 2 *((-chordX * (x1 - centerX)) - (chordY * (y1 - centerY))) /
                (chordX * chordX + chordY * chordY);

        // Найдем координаты точки пересечения продолжения хорды с окружностью
        double x3 = x1 + t * chordX;
        double y3 = y1 + t * chordY;

        // Углы дуги
        double startAngle = Math.toDegrees(Math.atan2(y1 - centerY, x1 - centerX));
        double endAngle = Math.toDegrees(Math.atan2(y3 - centerY, x3 - centerX));

        // Убедимся, что углы идут в правильном порядке
        if (endAngle < startAngle) {
            endAngle += 360;
        }

        double length = endAngle - startAngle;
        this.realArcLength = length; // Сохраняем реальную длину дуги

        System.out.println("LENGTH" + length);
        arc = new Arc(centerX, centerY, realRadius, realRadius, -startAngle, -length);
        arc.setType(ArcType.OPEN);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(Color.BLACK);
        arc.setStrokeWidth(2);

        endPoint = new Point2D(x3, y3);
        this.getChildren().addAll(startPoint, endPoint, arc, center);
    }

    public MyArc(double x1, double y1, double x2, double y2, double radius) {
        super();
        startPoint = new Point2D(x1,y1);
        endPoint = new Point2D(x2,y2);
        realRadius = radius;

        double chord = CoordSystem.distance(x1, y1, x2, y2);
        // Центральный угол
        double theta = 2 * Math.toDegrees(Math.asin(chord / (2 * radius)));

        // Середина хорды
        double midX = (x1 + x2) / 2;
        double midY = (y1 + y2) / 2;

        // Высота сегмента
        double h = Math.sqrt(radius * radius - Math.pow(chord / 2, 2));

        // Направление нормали
        double normalX = -(y2 - y1);
        double normalY = x2 - x1;

        // Центр окружности
        double centerX = midX + (h * normalX) / chord;
        double centerY = midY + (h * normalY) / chord;

        center = new Point2D(centerX, centerY);
        // Начальный угол
        double startAngle = Math.toDegrees(Math.atan2(y1 - centerY, x1 - centerX));
        //startAngle = theta + startAngle;
        //theta -= 360;
        // Создание дуги
        arc = new Arc(centerX, centerY, radius, radius, -startAngle, -theta);
        arc.setType(ArcType.OPEN);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(Color.BLACK);
        arc.setStrokeWidth(2);

        this.realArcLength = theta; // Сохраняем реальную длину дуги
        this.getChildren().addAll(startPoint, endPoint, arc, center);
    }

    private boolean isClockwise(double x1, double y1, double x2, double y2, double x3, double y3) {
        double crossProduct = (x2 - x1) * (y3 - y2) - (y2 - y1) * (x3 - x2);
        return crossProduct > 0; // Если > 0, то движение по часовой стрелке
    }

    public MyArc(List<Double> coordinates) {
        super();
        double x1 = coordinates.get(0);
        double y1 = coordinates.get(1);
        double x2 = coordinates.get(2);
        double y2 = coordinates.get(3);
        double x3 = coordinates.get(4);
        double y3 = coordinates.get(5);

        startPoint = new Point2D(x1, y1);
        endPoint = new Point2D(x3, y3);

        // Центр окружности
        double ma = (y2 - y1) / (x2 - x1);
        double mb = (y3 - y2) / (x3 - x2);
        double x_c = (ma * mb * (y1 - y3) + mb * (x1 + x2) - ma * (x2 + x3)) / (2 * (mb - ma));
        double y_c = -1 / ma * (x_c - (x1 + x2) / 2) + (y1 + y2) / 2;

        center = new Point2D(x_c, y_c);

        double radius = Math.sqrt(Math.pow(x1 - x_c, 2) + Math.pow(y1 - y_c, 2));

        // Углы
        double startAngle = Math.toDegrees(Math.atan2(-(y1 - y_c), x1 - x_c));
        double midAngle = Math.toDegrees(Math.atan2(-(y2 - y_c), x2 - x_c));
        double endAngle = Math.toDegrees(Math.atan2(-(y3 - y_c), x3 - x_c));

        startAngle = normalizeAngle(startAngle);
        midAngle = normalizeAngle(midAngle);
        endAngle = normalizeAngle(endAngle);

        boolean clockwise = isClockwise(x1, y1, x2, y2, x3, y3);

        double theta = clockwise
                ? (endAngle > startAngle ? endAngle - startAngle - 360 : endAngle - startAngle)
                : (endAngle < startAngle ? endAngle - startAngle + 360 : endAngle - startAngle);

        arc = new Arc(x_c, y_c, radius, radius, startAngle, theta);
        arc.setType(ArcType.OPEN);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(Color.BLACK);
        arc.setStrokeWidth(2);

        this.realArcLength = Math.abs(theta); // Сохраняем реальную длину дуги (по модулю)
        this.getChildren().addAll(startPoint, endPoint, arc, center);
        realRadius = radius;
    }
    // Нормализация углов в диапазон [0, 360)
    private double normalizeAngle(double angle) {
        return (angle + 360) % 360;
    }

    @Override
    public void setRealCoordinates(List<Double> v) {
        coordinates = v;
    }

    public Point2D getCenter(){
        return center;
    }
    public void setRealRadius(Double radius){
        if (radius >= 1){
            this.realRadius = radius;
        }
    }
    
    public double getRealArcLength() {
        return realArcLength;
    }
    
    public void setRealArcLength(double realArcLength) {
        this.realArcLength = realArcLength;
    }
    
    @Override
    public List<Double> getRealCoordinates() {
        return coordinates;
    }
    public List<Double> getScreenCoordinates(){
        List<Double> coord = new ArrayList<>();
        coord.add(arc.getCenterX());
        coord.add(arc.getCenterY());
        coord.add(startPoint.getCenterX());
        coord.add(startPoint.getCenterY());
        coord.add(endPoint.getCenterX());
        coord.add(endPoint.getCenterY());
        return coord;
    }

    public double getRealRadius() {
        return realRadius;
    }

    public javafx.scene.shape.Arc getArc() {
        return arc;
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
        center.setCenterX(center.getCenterX()+x);
        center.setCenterY(center.getCenterY()+y);
        arc.setCenterX(arc.getCenterX()+x);
        arc.setCenterY(arc.getCenterY()+y);
        startPoint.setCenterX(startPoint.getCenterX()+x);
        startPoint.setCenterY(startPoint.getCenterY()+y);
        endPoint.setCenterX(endPoint.getCenterX()+x);
        endPoint.setCenterY(endPoint.getCenterY()+y);
    }

    @Override
    public Figure select() {
        highlight();
        return this;
    }

    @Override
    public void highlight() {
        arc.setStroke(Color.RED);
    }

    @Override
    public void deHighlight() {
        arc.setStroke(Color.BLACK);
    }

    @Override
    public boolean isNear(double x, double y) {
        double centerX = arc.getCenterX();
        double centerY = arc.getCenterY();
        double radius = arc.getRadiusX();
        
        // Расстояние от точки до центра
        double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        
        // Проверяем, находится ли точка рядом с дугой
        if (Math.abs(distance - radius) < 5) {
            // Находим угол точки относительно центра
            double angle = Math.toDegrees(Math.atan2(-(y - centerY), x - centerX));
            
            // Нормализуем угол
            angle = normalizeAngle(angle);
            
            // Получаем параметры дуги
            double startAngle = arc.getStartAngle();
            double length = arc.getLength();
            
            // Проверяем, находится ли точка в пределах дуги
            if (length > 0) {
                double endAngle = normalizeAngle(startAngle + length);
                return (angle >= startAngle && angle <= endAngle) ||
                       (endAngle < startAngle && (angle >= startAngle || angle <= endAngle));
            } else {
                double endAngle = normalizeAngle(startAngle + length);
                return (angle <= startAngle && angle >= endAngle) ||
                       (endAngle > startAngle && (angle <= startAngle || angle >= endAngle));
            }
        }
        
        return false;
    }

    @Override
    public void deSelect() {
        deHighlight();
    }

    @Override
    public void zoom(ScrollEvent scrollEvent, double delta) {
        startPoint.zoom(scrollEvent, delta);
        endPoint.zoom(scrollEvent, delta);
        center.zoom(scrollEvent, delta);

        double mouseX = scrollEvent.getX();
        double mouseY = scrollEvent.getY();

        double shiftX = arc.getCenterX() - mouseX;
        double shiftY = arc.getCenterY() - mouseY;

        double newX = mouseX + shiftX * delta;
        double newY = mouseY + shiftY * delta;

        arc.setCenterX(newX);
        arc.setCenterY(newY);
        arc.setRadiusX(arc.getRadiusX()*delta);
        arc.setRadiusY(arc.getRadiusY()*delta);
    }

    @Override
    public void setupStyle(double width, List<Double> dashes, Double scale) {
        this.width = width;
        arc.setStrokeWidth(width);
        
        arc.getStrokeDashArray().clear();
        if (dashes != null) {
            List<Double> newList = new ArrayList<>();
            for (Double dash : dashes) {
                newList.add(dash * scale);
            }
            arc.getStrokeDashArray().addAll(newList);
        }
    }

    @Override
    public void updateStyle(double scale) {
        List<Double> list = arc.getStrokeDashArray();
        List<Double> newList = new ArrayList<>();
        for (Double aDouble : list) {
            newList.add(aDouble * scale);
        }
        arc.getStrokeDashArray().clear();
        arc.getStrokeDashArray().addAll(newList);
    }
}
