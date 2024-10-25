package ru.ruslan.spring.cad;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public interface Zoomable {
    public void zoom(ScrollEvent scrollEvent, double delta);
}
