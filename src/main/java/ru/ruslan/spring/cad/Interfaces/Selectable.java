package ru.ruslan.spring.cad.Interfaces;

public interface Selectable {

    public void select();
    public void highlight();
    public void deHighlight();
    public boolean isNear(double x, double y);
    public void deSelect();
}
