package ru.ruslan.spring.cad;

import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class CoordSystem extends Group {

    private double originX;
    private double originY;
    private Circle center;
    private Line horLine;
    private Line vertLine;


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

    public void shift(double x, double y) {

        horLine.setStartY(horLine.getStartY()+y);

        horLine.setEndY(horLine.getEndY()+y);

        vertLine.setStartX(vertLine.getStartX()+x);

        vertLine.setEndX(vertLine.getEndX()+x);

    }

    public void moveTo(double x, double y){
        horLine.setStartY(y);
        horLine.setEndY(y);
        vertLine.setStartX(x);
        vertLine.setEndX(x);
    }

   // private void repaint(){

   // }
    public void extendAxisX(double x){
        horLine.setEndX(horLine.getEndX()+x);
    }
    public void extendAxisY(double y){
        vertLine.setEndY(vertLine.getEndY()+y);
    }

    public double[] tranclateCoord(double logicX, double logicY, double factor){
        double realX = (logicX - originX)/factor;
        double realY = (originY - logicY)/factor;

        return new double[] {realX,realY};

    }
}
