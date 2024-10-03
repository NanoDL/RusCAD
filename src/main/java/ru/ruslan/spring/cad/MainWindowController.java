package ru.ruslan.spring.cad;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

public class MainWindowController {

    @FXML
    private Button button;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Pane canvas;
    @FXML
    private AnchorPane root;
    @FXML
    private Label coordX;
    @FXML
    private Label coordY;
    private Scene scene;


    double firstX,firstY;
    double secondX, secondY;

    private Line currentLine;


    public MainWindowController (){

    }


    private void initialize(){
        borderPane.prefWidthProperty().bind(root.getScene().widthProperty());
        borderPane.prefHeightProperty().bind(root.getScene().heightProperty());


    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }


    public void Create(MouseEvent mouseEvent) {
        if (currentLine == null) {
            firstX = mouseEvent.getX();
            firstY = mouseEvent.getY();
            secondX = firstX;
            secondY = firstY;

            currentLine = new Line(firstX,firstY,secondX,secondY);
        } else {
            canvas.getChildren().remove(currentLine);
            secondX = mouseEvent.getX();
            secondY = mouseEvent.getY();
            currentLine = new Line(firstX,firstY,secondX,secondY);
            canvas.getChildren().add(currentLine);
            firstX = 0;
            firstY = 0;
            secondX = 0;
            secondY = 0;
            currentLine = null;

        }





        /*if (firstX == 0 && firstY==0){
            firstX = mouseEvent.getX();
            firstY = mouseEvent.getY();
        } else {
            secondX = mouseEvent.getX();
            secondY = mouseEvent.getY();

            Line line = new Line(firstX,firstY,secondX,secondY);
            line.setStrokeWidth(10);


            firstX = 0;
            firstY = 0;
            secondX = 0;
            secondY = 0;
            canvas.getChildren().add(line);


        }*/



    }

    public void Moved(MouseEvent mouseEvent) {
        if(currentLine != null){
            canvas.getChildren().remove(currentLine);
            currentLine = new Line(firstX,firstY,mouseEvent.getX(),mouseEvent.getY());
            canvas.getChildren().add(currentLine);
        }


        coordX.setText(String.valueOf(mouseEvent.getX()));
        coordY.setText(String.valueOf(mouseEvent.getY()));
    }
}