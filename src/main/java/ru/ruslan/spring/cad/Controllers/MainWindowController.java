package ru.ruslan.spring.cad.Controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.WindowEvent;
import ru.ruslan.spring.cad.*;
import ru.ruslan.spring.cad.Interfaces.Movable;
import ru.ruslan.spring.cad.Interfaces.Zoomable;
import ru.ruslan.spring.cad.Models.*;

import javax.swing.text.ViewFactory;
//import javafx.scene.shape.Line;

//import java.awt.geom.Line2D;

public class MainWindowController {

    @FXML
    public StackPane stackContainer;
    @FXML
    private Pane canvas;

    @FXML
    private Label coordX;
    @FXML
    private Label coordY;
    @FXML
    private Label scrCoordX;
    @FXML
    private Label scrCoordY;
    @FXML
    private Label scaleLabel;
    @FXML
    private Label coordModeLabel;
    @FXML
    public Label modeLabel;

    @FXML
    private VBox controlPanel;
    private CanvasController canvasController;

    private ControlPanelController controlPanelController;


    private Scene scene;
    private Mode mode = Mode.PANORAM;


    public MainWindowController (){

    }





    public void initialize(){

        canvasController = new CanvasController(canvas);
        controlPanelController = new ControlPanelController(controlPanel);

        canvasController.setCoordX(this.coordX);
        canvasController.setCoordY(this.coordY);
        canvasController.setScrCoordX(this.scrCoordX);
        canvasController.setScrCoordY(this.scrCoordY);
        canvasController.setScaleLabel(this.scaleLabel);
        canvasController.setCoordModeLabel(this.coordModeLabel);
        canvasController.setModeLabel(this.modeLabel);
        canvasController.setMode(mode);
        canvasController.setControlPanelController(controlPanelController);

        canvasController.setEventHandlerBasedOnMode(Mode.PANORAM);
        canvasController.initialize();
        StackPane.setAlignment(controlPanel, Pos.CENTER_RIGHT);

        StackPane.setMargin(controlPanel, new Insets(0, 100.0, 0, 0));

    }




    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }



    @FXML
    public void changeModeToLine(ActionEvent event){
        canvasController.setEventHandlerBasedOnMode(Mode.DRAW_LINE);
    }

    @FXML
    public void changeModeToZoom(ActionEvent event){
        canvasController.setEventHandlerBasedOnMode(Mode.ZOOM);
    }

    @FXML
    public void changeModeToCircle(ActionEvent event){
        canvasController.setEventHandlerBasedOnMode(Mode.DRAW_CIRCLE);
    }

    @FXML
    public void changeModeToPanoram(ActionEvent event){
        canvasController.setEventHandlerBasedOnMode(Mode.PANORAM);
    }

    @FXML
    public void changeModeToRectrangle(ActionEvent event) {
        canvasController.setEventHandlerBasedOnMode(Mode.DRAW_RECT);
    }
    @FXML
    public void changeCoordMode(ActionEvent event) {
        canvasController.changeCoordMode();
    }

    @FXML
    public void changeModeToDivide(ActionEvent event) {
        canvasController.setEventHandlerBasedOnMode(Mode.DIVIDE);
    }
}