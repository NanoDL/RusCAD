package ru.ruslan.spring.cad.Controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ru.ruslan.spring.cad.Interfaces.Stylized;
import ru.ruslan.spring.cad.Models.Figure;
import ru.ruslan.spring.cad.Models.StyleMenu;

import java.util.*;

public class StyleMenuContrl {

    private StyleMenu menu;
    private Pane canvas;


    private CanvasController canvasController;

    Map<String, List<Stylized>> objects = new HashMap<>();

    public StyleMenuContrl(Pane canvas) {
        menu = new StyleMenu();
        menu.setAutoHide(true);
        menu.setStyleMenuContrl(this);
        this.canvas = canvas;
    }

    public void show(Stage stage){
        menu.show(stage);
        menu.setX(stage.getX() + stage.getWidth()-menu.getWidth() - 100);
        System.out.println(stage.getWidth());
        menu.setY(stage.getY() + 150);
    }

    public CanvasController getCanvasController() {
        return canvasController;
    }

    public void setCanvasController(CanvasController canvasController) {
        this.canvasController = canvasController;
    }

    /**
     * Возвращает карту объектов со стилями
     * @return Map с ключами - именами стилей и значениями - списками объектов с таким стилем
     */
    public Map<String, List<Stylized>> getObjects() {
        return objects;
    }

    public void addObjectToStyle(String styleName, Stylized object) {
        objects.computeIfAbsent(styleName, k -> new ArrayList<>()).add(object);
        System.out.println("Стилизованные объекты" + objects);
    }
    public void changeObjectStyle(){
        List<Figure> figures = canvasController.getSelected();

        figures.removeIf(figure -> !(figure instanceof Stylized));

        if (!figures.isEmpty()){
            String selectedStyle = menu.getBox().getValue();
            if (selectedStyle != null){
                for (String key : objects.keySet()){
                    List<Stylized> value = objects.get(key);
                    value.removeAll(figures);
                }
                for (Figure figure : figures){
                    Stylized temp = (Stylized) figure;
                    objects.computeIfAbsent(selectedStyle, k -> new ArrayList<>()).add(temp);
                }


            }
        }
    }
    public void updateStyleOnZoom(Double scale){

        System.out.println("Обновление при зуме");
        for (String key : objects.keySet()){
            List<Stylized> objs = objects.get(key);
            for (Stylized obj : objs){
                obj.updateStyle(scale);
            }
        }
    }

    public void updateStyle() {

        System.out.println(objects);
        String selectedStyle = menu.getBox().getValue();
        if (selectedStyle != null && objects.containsKey(selectedStyle)) {
            List<Stylized> stylizedObjects = objects.get(selectedStyle);

            Double width = menu.getWidths().get(selectedStyle); // Толщина
            Double dash = menu.getDashes().get(selectedStyle);            // Штрихи
            Double dot = menu.getDots().get(selectedStyle);               // Точки
            Double space = menu.getSpaces().get(selectedStyle);
            switch (selectedStyle){
                case "Штриховая":
                    for (Stylized object : stylizedObjects) {
                        object.setupStyle(width, List.of(dash, space), canvasController.getScale());
                    }
                    break;
                case "Штрих-пунктирная":
                    for (Stylized object : stylizedObjects) {
                        object.setupStyle(width, List.of(dash, space, dot, space), canvasController.getScale());
                    }
                    break;
                case "Штрих-пунктирная с двумя точками":
                    for (Stylized object : stylizedObjects) {
                        object.setupStyle(width, List.of(dash, space, dot, space, dot, space), canvasController.getScale());
                    }
                    break;
                default:
                    for (Stylized object : stylizedObjects) {
                        object.setupStyle(width, null, null);
                    }
                    break;
            }

            System.out.println("Обновлены стили для объектов с ключом: " + selectedStyle);
        }
    }
}
