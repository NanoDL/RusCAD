package ru.ruslan.spring.cad.Interfaces;


import javafx.scene.input.ScrollEvent;

public interface Zoomable {
    public void zoom(ScrollEvent scrollEvent, double delta);
}
