package ru.ruslan.spring.cad.Interfaces;

import ru.ruslan.spring.cad.Models.Figure;

public interface Selectable {

    public Figure select();
    public void highlight();
    public void deHighlight();
    public boolean isNear(double x, double y);
    public void deSelect();
}
