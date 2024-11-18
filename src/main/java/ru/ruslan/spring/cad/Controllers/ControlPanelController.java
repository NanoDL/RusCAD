package ru.ruslan.spring.cad.Controllers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import ru.ruslan.spring.cad.Interfaces.Selectable;
import ru.ruslan.spring.cad.Models.*;
import ru.ruslan.spring.cad.Models.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ControlPanelController {

    private VBox controlPanel;

    private CoordSystem coordSystem;
    private CanvasController canvasController;

    public ControlPanelController(VBox controlPanel) {
        this.controlPanel = controlPanel;

    }

    public CoordSystem getCoordSystem() {
        return coordSystem;
    }

    public void setCoordSystem(CoordSystem coordSystem) {
        this.coordSystem = coordSystem;
    }
    public VBox getControlPanel() {
        return controlPanel;
    }

    public void setControlPanel(VBox controlPanel) {
        this.controlPanel = controlPanel;
    }

    public void updateControlPanel(Selectable shape, MouseEvent mouseEvent){
        if (shape == null) {
            hideControlPanel(); // Отключить управление пространством
            return;
        }

        showControlPanel(); // Включить управление пространством
        controlPanel.requestFocus();
        controlPanel.getChildren().clear();

        switch (shape) {
            case MyLine line -> {
                TextField startX = new TextField(String.valueOf(line.getRealX1()));
                TextField startY = new TextField(String.valueOf(line.getRealY1()));
                TextField endX = new TextField(String.valueOf(line.getRealX2()));
                TextField endY = new TextField(String.valueOf(line.getRealY2()));

                TextField rotationX = new TextField();
                TextField rotationY = new TextField();
                TextField angleText = new TextField();

                Button apply = new Button("Применить");
                apply.setOnAction(e -> {
                    double x1 = Double.parseDouble(startX.getText());
                    double y1 = Double.parseDouble(startY.getText());
                    double x2 = Double.parseDouble(endX.getText());
                    double y2 = Double.parseDouble(endY.getText());

                    line.setRealX1(x1);
                    line.setRealY1(y1);
                    line.setRealX2(x2);
                    line.setRealY2(y2);

                    double[] coord1 = coordSystem.translateRealToScreen(x1, y1);
                    double[] coord2 = coordSystem.translateRealToScreen(x2, y2);

                    line.move(coord1[0], coord1[1], coord2[0], coord2[1]);
                });

                Button rotate = new Button("Повернуть");
                rotate.setOnAction(e -> {
                    double x1 = Double.parseDouble(startX.getText());
                    double y1 = Double.parseDouble(startY.getText());
                    double x2 = Double.parseDouble(endX.getText());
                    double y2 = Double.parseDouble(endY.getText());

                    double aroundX = Double.parseDouble(rotationX.getText());
                    double aroundY = Double.parseDouble(rotationY.getText());
                    double angle = Double.parseDouble(angleText.getText());

                    double[] coord1 = CoordSystem.rotateAround(x1, y1, angle, aroundX, aroundY);
                    double[] coord2 = CoordSystem.rotateAround(x2, y2, angle, aroundX, aroundY);

                    line.setRealCoordinates(List.of(coord1[0], coord1[1], coord2[0], coord2[1]));

                    coord1 = coordSystem.translateRealToScreen(coord1[0], coord1[1]);
                    coord2 = coordSystem.translateRealToScreen(coord2[0], coord2[1]);

                    line.move(coord1[0], coord1[1], coord2[0], coord2[1]);

                });

                controlPanel.getChildren().addAll(
                        new Label("Начало (X, Y):"), startX, startY,
                        new Label("Конец (X, Y):"), endX, endY,
                        apply,
                        new Label("Повернуть вокруг точки:"), rotationX, rotationY,
                        new Label("На угол"), angleText,
                        rotate

                );
            }
            case MyCircle myCircle -> {
                TextField centerX = new TextField(String.valueOf(myCircle.getRealCentX()));
                TextField centerY = new TextField(String.valueOf(myCircle.getRealCentY()));
                TextField radius = new TextField(String.valueOf(myCircle.getRealRadius()));

                Button apply = new Button("Применить");
                apply.setOnAction(e -> {
                    double x = Double.parseDouble(centerX.getText());
                    double y = Double.parseDouble(centerY.getText());
                    double r = Double.parseDouble(radius.getText());

                    myCircle.setRealCoordinates(List.of(x, y));
                    myCircle.setRealRadius(r);

                    double[] coord = coordSystem.translateRealToScreen(x, y);

                    myCircle.move(coord);
                    myCircle.setRadius(r * coordSystem.getScale());
                });

                TextField rotationX = new TextField();
                TextField rotationY = new TextField();
                TextField angleText = new TextField();

                Button rotate = new Button("Повернуть");

                rotate.setOnAction(e -> {

                    double x = Double.parseDouble(centerX.getText());
                    double y = Double.parseDouble(centerY.getText());
                    double r = Double.parseDouble(radius.getText());

                    double aroundX = Double.parseDouble(rotationX.getText());
                    double aroundY = Double.parseDouble(rotationY.getText());
                    double angle = Double.parseDouble(angleText.getText());

                    double[] coord1 = CoordSystem.rotateAround(x, y, angle, aroundX, aroundY);

                    myCircle.setRealCoordinates(List.of(coord1[0], coord1[1]));

                    coord1 = coordSystem.translateRealToScreen(coord1[0], coord1[1]);

                    myCircle.move(coord1[0], coord1[1]);



                });

                controlPanel.getChildren().addAll(
                        new Label("Центр (X, Y):"), centerX, centerY,
                        new Label("Радиус:"), radius,
                        apply,
                        new Label("Повернуть вокруг точки:"), rotationX, rotationY,
                        new Label("На угол"), angleText,
                        rotate
                );
            }
            case Rectangle rectangle -> {
                List<Double> coordinates = rectangle.getRealCoordinates();
                List<TextField> textCoordinates = new ArrayList<>();

                for (Double coord : coordinates){
                    textCoordinates.add(new TextField(String.valueOf(coord)));
                }

                Button apply = new Button("Применить");

                apply.setOnAction(e -> {
                    List<Double> newRealCoord = new ArrayList<>();

                    for (TextField textField : textCoordinates){
                        newRealCoord.add(Double.parseDouble(textField.getText()));
                    }

                    rectangle.setRealCoordinates(newRealCoord);

                    List<Double> newCoord = new ArrayList<>();

                    for (int i = 0; i<newRealCoord.size()-1; i+=2){
                        System.out.println(newRealCoord.get(i) + " " + newRealCoord.get(i+1));
                         double[] coord = coordSystem.translateRealToScreen(newRealCoord.get(i),newRealCoord.get(i+1));

                         for (Double v : coord){
                             newCoord.add(v);
                         }
                    }

                    rectangle.move(newCoord);
                });

                TextField rotationX = new TextField();
                TextField rotationY = new TextField();
                TextField angleText = new TextField();

                Button rotate = new Button("Повернуть");
                rotate.setOnAction(e -> {

                    double aroundX = Double.parseDouble(rotationX.getText());
                    double aroundY = Double.parseDouble(rotationY.getText());
                    double angle = Double.parseDouble(angleText.getText());

                    List<Double> newRealCoord = new ArrayList<>();
                    List<Double> newCoord = new ArrayList<>();

                    for (int i = 0; i < coordinates.size() - 1; i += 2) {
                        double[] coord = CoordSystem.rotateAround(coordinates.get(i), coordinates.get(i+1), angle, aroundX, aroundY);
                        double[] coord1 = coordSystem.translateRealToScreen(coord[0], coord[1]);

                        for (Double v : coord){
                            newRealCoord.add(v);
                        }
                        for (Double v : coord1){
                            newCoord.add(v);
                        }
                    }

                    rectangle.setRealCoordinates(newRealCoord);
                    rectangle.move(newCoord);



                });

                controlPanel.getChildren().add(new Label("Координаты (X, Y):"));
                for (TextField textField : textCoordinates){
                    controlPanel.getChildren().add(textField);
                }

                controlPanel.getChildren().addAll(
                        apply,new Label("Повернуть вокруг точки:"), rotationX, rotationY,
                        new Label("На угол"), angleText,
                        rotate
                );


            }
            case Point2D point2D -> {
            }
            default -> {
            }
        }
    }

    private void showControlPanel() {
        controlPanel.setVisible(true);
        controlPanel.setManaged(true);
    }

    private void hideControlPanel() {
        controlPanel.setVisible(false);
        controlPanel.setManaged(false);
    }
}
