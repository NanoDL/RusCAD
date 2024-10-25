package ru.ruslan.spring.cad;

import javafx.scene.input.ScrollEvent;

public class MyLine extends javafx.scene.shape.Line implements Zoomable{

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


    public void shift(double differenceX, double differenceY) {
        super.setStartX(super.getStartX()+differenceX);
        super.setStartY(super.getStartY()+differenceY);
        super.setEndX(super.getEndX()+differenceX);
        super.setEndY(super.getEndY()+differenceY);

        startPoint.setCenterX(super.getStartX());
        startPoint.setCenterY(super.getStartY());
        endPoint.setCenterX(super.getEndX());
        endPoint.setCenterY(super.getEndY());
    }

    public void shift(double newStartX, double newStartY, double newEndX, double newEndY){
        super.setStartX(newStartX);
        super.setStartY(newStartY);
        super.setEndX(newEndX);
        super.setEndY(newEndY);

        startPoint.setCenterX(newStartX);
        startPoint.setCenterY(newStartY);
        endPoint.setCenterX(newEndX);
        endPoint.setCenterY(newEndY);
    }

    @Override
    public void zoom(ScrollEvent scrollEvent, double delta) {
        double mouseX = scrollEvent.getX();
        double mouseY = scrollEvent.getY();

        double shiftXStart = getStartX() - mouseX;
        double shiftYStart = getStartY()- mouseY;
        double shiftXEnd = getEndX() - mouseX;
        double shiftYEnd = getEndY() - mouseY;

        double newStartX = mouseX + shiftXStart * delta;
        double newStartY = mouseY + shiftYStart * delta;
        double newEndX = mouseX + shiftXEnd * delta;
        double newEndY = mouseY + shiftYEnd * delta;

        shift(newStartX,newStartY,newEndX,newEndY);
    }
}
