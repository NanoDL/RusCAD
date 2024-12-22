package ru.ruslan.spring.cad.Models;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import ru.ruslan.spring.cad.Interfaces.Drawable;
import ru.ruslan.spring.cad.Interfaces.Movable;
import ru.ruslan.spring.cad.Interfaces.Selectable;
import ru.ruslan.spring.cad.Interfaces.Zoomable;

import java.util.ArrayList;
import java.util.List;

public class MySpline extends Figure implements Movable, Drawable, Selectable, Zoomable {
    private Group spline;
    private Group lines;
    private Group points;
    private List<Double> tValues;


    public MySpline(List<Double> coord) {
        super();
        lines = new Group();
        spline = new Group();
        points = new Group();
        // Преобразуем координаты в t-параметры
        tValues = new ArrayList<>();
        for (int i = 0; i < coord.size() / 2; i++) {
            tValues.add((double) i); // t = 0, 1, 2, ...
        }

        // Вычисление сплайна по координатам x и y как функция от t
        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();
        for (int i = 0; i < coord.size(); i++) {
            if (i % 2 == 0) {
                xValues.add(coord.get(i)); // x(t)
            } else {
                yValues.add(coord.get(i)); // y(t)
            }
        }
        System.out.println(xValues);
        // Рассчитываем сплайн для x(t) и y(t)
        List<List<Double>> xCoefficients = calculateCubicSpline(tValues, xValues);
        List<List<Double>> yCoefficients = calculateCubicSpline(tValues, yValues);

        // Рисуем сплайн по вычисленным коэффициентам
        drawSpline(spline, xCoefficients, yCoefficients, tValues);
        drawControlLines(lines, coord);
        drawControlPoints(points, coord);
        this.getChildren().addAll(spline);
        setControlPointsHandlers();
    }

    private void drawControlLines (Group lines, List<Double> coord){
        lines.getChildren().clear();
        for (int i = 0; i < coord.size()-2 ; i+=2){
            LineForSpline line = new LineForSpline(coord.get(i), coord.get(i+1), coord.get(i+2), coord.get(i+3));
            line.setStroke(Color.rgb(160,160,160));
            lines.getChildren().add(line);

            /*line.setClickablePoints(true);
            line.setVisiblePoints(false);*/
        }
    }

    private void drawControlPoints (Group points, List<Double> coord){
        for (int i = 0; i < coord.size()-1 ; i+=2){
            Point2D point = new Point2D(coord.get(i), coord.get(i+1));
            point.setFill(Color.rgb(160,160,160));
            point.setRadius(10);
            points.getChildren().add(point);
            /*line.setClickablePoints(true);
            line.setVisiblePoints(false);*/
        }
    }

    private void setControlPointsHandlers(){
        for (Node node : points.getChildren()){
            Point2D point = (Point2D) node;
            point.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    point.setUserData(mouseEvent);
                }
            });
            point.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    controlPointHandler(mouseEvent, point);
                }
            });
        }
    }

    private void controlPointHandler(MouseEvent mouseEvent, Point2D point){
        this.getChildren().removeAll(spline, lines);
        MouseEvent mouseEvent1 = (MouseEvent) point.getUserData();
        double raznicaX = mouseEvent.getX() - mouseEvent1.getX();
        double raznicaY = mouseEvent.getY() - mouseEvent1.getY();
        point.shift(raznicaX, raznicaY);
        point.setUserData(mouseEvent);


        List<Double> coord = CoordSystem.translatePointsToCoord(points);
        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();

        for (int i = 0; i < coord.size(); i++) {
            if (i % 2 == 0) {
                xValues.add(coord.get(i)); // x(t)
            } else {
                yValues.add(coord.get(i)); // y(t)
            }
        }

        List<List<Double>> xCoefficients = calculateCubicSpline(tValues, xValues);
        List<List<Double>> yCoefficients = calculateCubicSpline(tValues, yValues);

        // Рисуем сплайн по вычисленным коэффициентам
        drawSpline(spline, xCoefficients, yCoefficients, tValues);
        drawControlLines(lines, coord);
        this.getChildren().addFirst(spline);
        this.getChildren().addFirst(lines);

    }

    private List<List<Double>> calculateCubicSpline(List<Double> tValues, List<Double> values) {
        int n = tValues.size() - 1;
        List<Double> h = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            h.add(tValues.get(i + 1) - tValues.get(i)); // Разности t
        }

        List<Double> alpha = new ArrayList<>();
        alpha.add(0.0); // Первый элемент
        for (int i = 1; i < n; i++) {
            alpha.add(3 * (values.get(i + 1) - values.get(i)) / h.get(i) - 3 * (values.get(i) - values.get(i - 1)) / h.get(i - 1));
        }

        List<Double> l = new ArrayList<>();
        List<Double> mu = new ArrayList<>();
        List<Double> z = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            l.add(0.0);
            z.add(0.0);
            if (i < n) {
                mu.add(0.0);
            }
        }

        l.set(0, 1.0);
        for (int i = 1; i < n; i++) {
            l.set(i, 2 * (tValues.get(i + 1) - tValues.get(i - 1)) - h.get(i - 1) * mu.get(i - 1));
            mu.set(i, h.get(i) / l.get(i));
            z.set(i, (alpha.get(i) - h.get(i - 1) * z.get(i - 1)) / l.get(i));
        }

        l.set(n, 1.0);

        List<Double> c = new ArrayList<>();
        List<Double> b = new ArrayList<>();
        List<Double> d = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            c.add(0.0);
            if (i < n) {
                b.add(0.0);
                d.add(0.0);
            }
        }

        for (int j = n - 1; j >= 0; j--) {
            c.set(j, z.get(j) - mu.get(j) * c.get(j + 1));
            b.set(j, (values.get(j + 1) - values.get(j)) / h.get(j) - h.get(j) * (c.get(j + 1) + 2 * c.get(j)) / 3);
            d.set(j, (c.get(j + 1) - c.get(j)) / (3 * h.get(j)));
        }

        List<List<Double>> coefficients = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<Double> coeff = new ArrayList<>();
            coeff.add(values.get(i)); // a
            coeff.add(b.get(i)); // b
            coeff.add(c.get(i)); // c
            coeff.add(d.get(i)); // d
            coefficients.add(coeff);
        }

        return coefficients;
    }

    private void drawSpline(Group root, List<List<Double>> xCoefficients, List<List<Double>> yCoefficients, List<Double> tValues) {
        root.getChildren().clear();
        int n = tValues.size() - 1;

        for (int i = 0; i < n; i++) {
            double xStart = tValues.get(i);
            double xEnd = tValues.get(i + 1);
            double step = (xEnd - xStart) / 100.0;

            double aX = xCoefficients.get(i).get(0);
            double bX = xCoefficients.get(i).get(1);
            double cX = xCoefficients.get(i).get(2);
            double dX = xCoefficients.get(i).get(3);

            double aY = yCoefficients.get(i).get(0);
            double bY = yCoefficients.get(i).get(1);
            double cY = yCoefficients.get(i).get(2);
            double dY = yCoefficients.get(i).get(3);

            ArrayList<Double> realX = new ArrayList<>();
            ArrayList<Double> realY = new ArrayList<>();

            for (int j = 0; j <= 100; j++) {
                double currentX = xStart + step * j;
                double dx = currentX - xStart;
                double currentY = aY + bY * dx + cY * dx * dx + dY * dx * dx * dx;
                double currentXVal = aX + bX * dx + cX * dx * dx + dX * dx * dx * dx;
                realX.add(currentXVal);
                realY.add(currentY);
            }

            for (int j = 0; j < realX.size()-1; j++){
                LineForSpline segment = new LineForSpline(realX.get(j), realY.get(j), realX.get(j+1), realY.get(j+1));

                segment.setStroke(Color.BLACK);
                segment.setStrokeWidth(1.5);
                root.getChildren().add(segment);
            }
        }
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
        for (Node node : points.getChildren()){
            ((Point2D) node).shift(x,y);
        }
        for (Node line: lines.getChildren()){
            ((LineForSpline) line).shift(x, y);
        }
        for (Node line: spline.getChildren()){
            ((LineForSpline) line).shift(x, y);
        }
    }

    @Override
    public Figure select() {
        if (!this.getChildren().contains(lines) && !this.getChildren().contains(points)){
            this.getChildren().addAll(lines, points);
        }
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
        this.getChildren().removeAll(lines, points);
    }

    @Override
    public void zoom(ScrollEvent scrollEvent, double delta) {
        for (Node node : points.getChildren()){
            ((Point2D) node).zoom(scrollEvent, delta);
        }
        for (Node line: lines.getChildren()){
            ((LineForSpline) line).zoom(scrollEvent, delta);
        }
        for (Node line: spline.getChildren()){
            ((LineForSpline) line).zoom(scrollEvent, delta);
        }
    }

    @Override
    void setRealCoordinates(List<Double> v) {

    }

    @Override
    List<Double> getRealCoordinates() {
        return List.of();
    }
}
