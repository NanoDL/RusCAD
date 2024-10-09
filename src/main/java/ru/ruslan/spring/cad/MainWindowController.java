package ru.ruslan.spring.cad;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.WindowEvent;
//import javafx.scene.shape.Line;

//import java.awt.geom.Line2D;

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
    private CoordSystem coordSystem;
    private Mode mode = Mode.PANORAM;
    private double scale = 1.0;
    private double originX;
    private double originY;
    double firstX,firstY;
    double secondX, secondY;

    private MyLine currentMyLine;


    public MainWindowController (){

    }


    public void initialize(){


        canvas.widthProperty().addListener(((observableValue, number, t1) -> updateOrigin()));
        canvas.heightProperty().addListener(((observableValue, number, t1) -> updateOrigin()));


        canvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                coordUpd(mouseEvent);
            }
        });
        canvas.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent scrollEvent) {

            }
        });
        // Проверяем, когда сцена будет отображена
        canvas.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
                            setCoordSystem(); // Устанавливаем координатную систему только после отображения окна
                            canvas.getChildren().add(new Line(0,0,1000,0));
                        });
                    }
                });
            }
        });
    }

    private void updateAllCoord(double raznicaX,double raznicaY){
        originY += raznicaY;
        originX += raznicaX;
        for (Node node : canvas.getChildren()){
            if (node instanceof Line){
                Line line = (Line)node;
                line.setStartX(line.getStartX()+raznicaX);
                line.setStartY(line.getStartY()+raznicaY);
                line.setEndX(line.getEndX()+raznicaX);
                line.setEndY(line.getEndY()+raznicaY);
            }
        }
    }

    private void updateOrigin() {

        System.out.println("Высота" + canvas.getHeight());
        System.out.println("Ширина" + canvas.getWidth());

    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setCoordSystem() {
        originX = canvas.getWidth()/2;
        originY = canvas.getHeight()/2;
        Line horLine = new Line(0,canvas.getHeight()/2,canvas.getWidth(), canvas.getHeight()/2);
        Line vertLine = new Line(canvas.getWidth()/2,0, canvas.getWidth()/2,canvas.getHeight());
        coordSystem = new CoordSystem(horLine, vertLine);
        canvas.getChildren().add(coordSystem.getHorLine());
        canvas.getChildren().add(coordSystem.getVertLine());

    }

    public void createLine(MouseEvent mouseEvent) {
        if (currentMyLine == null) {
            firstX = mouseEvent.getX();
            firstY = mouseEvent.getY();
            secondX = firstX;
            secondY = firstY;

            currentMyLine = new MyLine(firstX,firstY,secondX,secondY);
        } else {
            canvas.getChildren().remove(currentMyLine);
            secondX = mouseEvent.getX();
            secondY = mouseEvent.getY();
            currentMyLine = new MyLine(firstX,firstY,secondX,secondY);


            canvas.getChildren().add(currentMyLine);
            canvas.getChildren().add(currentMyLine.getStartPoint());
            canvas.getChildren().add(currentMyLine.getEndPoint());


            firstX = 0;
            firstY = 0;
            secondX = 0;
            secondY = 0;
            currentMyLine = null;

        }




    }

    public void createLineRefresh(MouseEvent mouseEvent) {
        if(currentMyLine != null){
            canvas.getChildren().remove(currentMyLine);
            currentMyLine = new MyLine(firstX,firstY,mouseEvent.getX(),mouseEvent.getY());
            canvas.getChildren().add(currentMyLine);
        }


    }

    @FXML
    public void changeModeToLine(ActionEvent event){
        this.mode = Mode.DRAW_LINE;
        setEventHandlerBasedOnMode();
    }

    @FXML
    public void changeModeToZoom(ActionEvent event){
        this.mode = Mode.ZOOM;
        setEventHandlerBasedOnMode();
    }

    @FXML
    public void changeModeToPanoram(ActionEvent event){
        this.mode = Mode.PANORAM;
        setEventHandlerBasedOnMode();
    }

    private void clearAllPaneHandlers(){
        canvas.setOnMousePressed(null);
        canvas.setOnMouseDragged(null);
        //canvas.setOnMouseMoved(null);
        canvas.setOnMouseClicked(null);
    }

    //смена обработчиков
    private void setEventHandlerBasedOnMode(){
        clearAllPaneHandlers();
        switch (this.mode){
            case ZOOM -> setZoomHandler();
            case PANORAM -> setPanoramHandler();
            case DRAW_LINE -> setDrawLineHandler();
        }
    }

    private void setPanoramHandler() {
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                firstX = mouseEvent.getX();
                firstY = mouseEvent.getY();

                System.out.println(firstX);
            }
        });

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                secondX = mouseEvent.getX();
                secondY = mouseEvent.getY();
                double raznicaX = secondX - firstX;
                double raznicaY = secondY - firstY;
                System.out.println(raznicaX);
                updateAllCoord(raznicaX,raznicaY);
                firstX = secondX;
                firstY = secondY;
            }
        });
    }

    private void setDrawLineHandler(){
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                createLine(mouseEvent);
            }
        });
        canvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                createLineRefresh(mouseEvent);
                coordUpd(mouseEvent);
            }
        });

    }

    private void coordUpd(MouseEvent mouseEvent){
        coordX.setText(String.valueOf(mouseEvent.getX() - originX));
        coordY.setText(String.valueOf(originY - mouseEvent.getY()));
    }

    private void setZoomHandler() {

    }




}