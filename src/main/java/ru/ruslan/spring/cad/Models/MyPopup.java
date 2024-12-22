package ru.ruslan.spring.cad.Models;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import ru.ruslan.spring.cad.Mode;

public class MyPopup extends Popup {

    private VBox container;

    private Label labelX;
    private Label labelY;
    private Label label3;
    private Label label4;

    private TextField textField1;
    private TextField textField2;
    private TextField textField3;
    private TextField textField4;

    //private Button changeModeButt;


    public MyPopup() {
        super();

        labelX = new Label("X: ");
        labelY = new Label("Y: ");
        label3 = new Label("Радиус:");
        label4 = new Label("Количество сторон");

        textField1 = new TextField();
        textField2 = new TextField();
        textField3 = new TextField();
        textField4 = new TextField();


        container = new VBox(labelX, textField1, labelY, textField2);

        super.getContent().addAll(container);
    }


    public void setDefaultCoord(double val1, double val2) {
        textField1.setText(String.valueOf(val1));
        textField2.setText(String.valueOf(val2));
    }

    public double[] getCoordinates() {
        double coord[] = new double[2];

        coord[0] = Double.parseDouble(textField1.getText());
        coord[1] = Double.parseDouble(textField2.getText());
        return coord;
    }

    public void setPolarMode(boolean bool){
        if (bool){
            labelX.setText("R:");
            labelY.setText("Угол:");
        } else {
            labelX.setText("X:");
            labelY.setText("Y:");
        }
    }
    public void enableOtherFields(Mode mode) {
        super.getContent().remove(container);
        switch (mode) {
            case DRAW_CIRCLE -> {
                label3.setText("R:");
                container.getChildren().addAll(label3, textField3);
            }
            case DRAW_POLYGON_IN, DRAW_POLYGON_OUT -> {
                label3.setText("R:");
                label4.setText("Количество сторон:");
                container.getChildren().addAll(label3, textField3, label4, textField4);
            }
        }
        super.getContent().add(container);
    }

    public void disable3Field() {
        super.getContent().remove(container);
        container.getChildren().removeAll(label3, textField3, label4, textField4);
        super.getContent().add(container);
    }

    public double get3Field() {
        String value = textField3.getText();
        try {
            // Пробуем преобразовать текст в число
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            // Если ввод некорректный, выводим сообщение об ошибке и возвращаем значение по умолчанию
            System.err.println("Ошибка: Введите корректное число в 3 строку.");
            return 0.0; // Значение по умолчанию
        }
    }

    public double get4Field() {
        String value = textField4.getText();
        try {
            // Пробуем преобразовать текст в число
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            // Если ввод некорректный, выводим сообщение об ошибке и возвращаем значение по умолчанию
            System.err.println("Ошибка: Введите корректное число в 4 строку.");
            return 0.0; // Значение по умолчанию
        }
    }

    public boolean isCircleMode() {
        return container.getChildren().contains(textField3);
    }


}
