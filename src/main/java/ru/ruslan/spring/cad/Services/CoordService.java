package ru.ruslan.spring.cad.Services;

import javafx.scene.Group;
import ru.ruslan.spring.cad.Models.*;
import ru.ruslan.spring.cad.Controllers.CanvasController;

import java.util.ArrayList;
import java.util.List;

public class CoordService {
    private CoordSystem coordSystem;
    private CanvasController canvasController;

    public CoordService(CoordSystem coordSystem) {
        this.coordSystem = coordSystem;
    }

    public CoordSystem getCoordSystem() {
        return coordSystem;
    }

    public void setCoordSystem(CoordSystem coordSystem) {
        this.coordSystem = coordSystem;
    }
    
    public CanvasController getCanvasController() {
        return canvasController;
    }
    
    public void setCanvasController(CanvasController canvasController) {
        this.canvasController = canvasController;
    }

    /**
     * Преобразует X-координату из реальной системы координат в экранную
     * @param realX реальная X-координата
     * @return экранная X-координата
     */
    public double toScreenX(double realX) {
        double[] screenCoord = coordSystem.translateRealToScreen(realX, 0);
        return screenCoord[0];
    }

    /**
     * Преобразует Y-координату из реальной системы координат в экранную
     * @param realY реальная Y-координата
     * @return экранная Y-координата
     */
    public double toScreenY(double realY) {
        double[] screenCoord = coordSystem.translateRealToScreen(0, realY);
        return screenCoord[1];
    }

    /**
     * Преобразует длину из реальной системы координат в экранную
     * @param realLength реальная длина
     * @return экранная длина
     */
    public double toScreenLength(double realLength) {
        return realLength * coordSystem.getScale();
    }

    public void updateFigureRealCoord(Figure figure) {
        switch (figure) {
            case Rectangle rectangle -> {
                List<Double> coord = rectangle.getScreenCoordinates();
                coord = coordSystem.translateScreenToReal(coord);
                rectangle.setRealCoordinates(coord);
            }
            case MyArc arc -> {
                List<Double> coord = arc.getScreenCoordinates();
                Point2D center = arc.getCenter();
                coord = coordSystem.translateScreenToReal(coord);
                System.out.println("Real " + coord);
                double[] centerCoord = coordSystem.translateScreenToReal(center.getCenterX(), center.getCenterY());
                double radius = CoordSystem.distance(coord.get(2), coord.get(3), centerCoord[0], centerCoord[1]);
                arc.setRealRadius(radius);
                arc.setRealCoordinates(coord);
                
                // Вычисляем реальную длину дуги в градусах
                if (coord.size() >= 6) {
                    // Извлекаем координаты центра, начальной и конечной точек
                    double centerX = coord.get(0);
                    double centerY = coord.get(1);
                    double startX = coord.get(2);
                    double startY = coord.get(3);
                    double endX = coord.get(4);
                    double endY = coord.get(5);
                    
                    // Вычисляем углы начальной и конечной точек относительно центра
                    double startAngle = Math.toDegrees(Math.atan2(startY - centerY, startX - centerX));
                    double endAngle = Math.toDegrees(Math.atan2(endY - centerY, endX - centerX));
                    
                    // Нормализуем углы в диапазоне [0, 360)
                    startAngle = (startAngle + 360) % 360;
                    endAngle = (endAngle + 360) % 360;
                    
                    // Вычисляем длину дуги в градусах
                    double arcLength = endAngle - startAngle;
                    
                    // Если длина отрицательная, добавляем 360 градусов,
                    // т.к. дуга идет через 0 градусов
                    if (arcLength < 0) {
                        arcLength += 360;
                    }
                    
                    // Определяем направление дуги
                    javafx.scene.shape.Arc javafxArc = arc.getArc();
                    boolean isNegativeDirection = javafxArc.getLength() < 0;
                    
                    // Если дуга идет в отрицательном направлении, инвертируем длину
                    if (isNegativeDirection) {
                        arcLength = 360 - arcLength;
                    }
                    
                    arc.setRealArcLength(arcLength);
                    System.out.println("Реальная длина дуги: " + arcLength + " градусов");
                }
            }
            case Polygon polygon -> {
                Group lines = polygon.getLines();
                List<Double> polyCoord = new ArrayList<Double>();
                for (int i = 0; i < lines.getChildren().size(); i++) {
                    MyLine line = (MyLine) lines.getChildren().get(i);
                    List<Double> coord = List.of(line.getStartPoint().getCenterX(), line.getStartPoint().getCenterY());
                    coord = coordSystem.translateScreenToReal(coord);
                    polyCoord.addAll(coord);
                }
                polygon.setRealCoordinates(polyCoord);
            }
            default -> throw new IllegalStateException("Unexpected value: " + figure);
        }
    }
}
