package ru.ruslan.spring.cad;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public class CoordSystem  {

    private Line horLine;
    private Line vertLine;


    CoordSystem(Line horLine,Line vertLine){
        this.vertLine = vertLine;
        this.horLine = horLine;
    }

    public Line getHorLine() {
        return horLine;
    }

    public Line getVertLine() {
        return vertLine;
    }
}
