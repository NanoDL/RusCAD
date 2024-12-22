package ru.ruslan.spring.cad.Services;

import ru.ruslan.spring.cad.Models.*;

import java.util.List;

public class CoordService {
    private CoordSystem coordSystem;

    public CoordService(CoordSystem coordSystem) {
        this.coordSystem = coordSystem;
    }

    public CoordSystem getCoordSystem() {
        return coordSystem;
    }

    public void setCoordSystem(CoordSystem coordSystem) {
        this.coordSystem = coordSystem;
    }
    public void updateFigureRealCoord(Figure figure){
        switch (figure){
            case Rectangle rectangle ->{
                List<Double> coord = rectangle.getScreenCoordinates();
                coord = coordSystem.translateScreenToReal(coord);
                rectangle.setRealCoordinates(coord);
            }
            case MyArc arc -> {
                List<Double> coord = arc.getScreenCoordinates();
                Point2D center = arc.getCenter();
                coord = coordSystem.translateScreenToReal(coord);
                double[] centerCoord = coordSystem.translateScreenToReal(center.getCenterX(), center.getCenterY());
                double radius = CoordSystem.distance(coord.get(0), coord.get(1), centerCoord[0], centerCoord[1]);
                arc.setRealRadius(radius);
                arc.setRealCoordinates(coord);
            }
            default -> throw new IllegalStateException("Unexpected value: " + figure);
        }
    }
}
