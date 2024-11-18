package ru.ruslan.spring.cad.Models;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;

import java.util.Objects;

public class MyPopup extends Popup {

    private VBox container;

    private Label labelX;
    private Label labelY;
    private Label labelR;

    private TextField textField1;
    private TextField textField2;
    private TextField textField3;

    //private Button changeModeButt;


    public MyPopup() {
        super();

        labelX = new Label("X: ");
        labelY = new Label("Y: ");
        labelR = new Label("Радиус:");

        textField1 = new TextField();
        textField2 = new TextField();
        textField3 = new TextField();


        container = new VBox(labelX, textField1, labelY, textField2);

        super.getContent().addAll(container);
    }


    public void setDefaultCoord(double val1, double val2){
        textField1.setText(String.valueOf(val1));
        textField2.setText(String.valueOf(val2));
    }

    public double[] getCoordinates(){
        double coord[] = new double[2];

        coord[0] = Double.parseDouble(textField1.getText());
        coord[1] = Double.parseDouble(textField2.getText());

        return coord;
    }

    public void enableCircleMode(){
        super.getContent().remove(container);
        container.getChildren().addAll(labelR, textField3);
        super.getContent().add(container);
    }

    public void disableCircleMode(){
        super.getContent().remove(container);
        container.getChildren().removeAll(labelR, textField3);
        super.getContent().add(container);
    }

    public double getRadius(){
        return Double.parseDouble(textField3.getText());
    }

    public boolean isCircleMode(){
        return container.getChildren().contains(textField3);
    }


}
