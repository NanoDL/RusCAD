package ru.ruslan.spring.cad;

public class MyLine extends javafx.scene.shape.Line {

    private Point2D startPoint;
    private Point2D endPoint;

    public MyLine(double x1, double y1, double x2, double y2){
        super(x1,y1,x2,y2);
        startPoint = new Point2D(x1,y1);
        endPoint = new Point2D(x2,y2);

    }

    public Point2D getStartPoint() {
        return startPoint;
    }

    public Point2D getEndPoint() {
        return endPoint;
    }
}
