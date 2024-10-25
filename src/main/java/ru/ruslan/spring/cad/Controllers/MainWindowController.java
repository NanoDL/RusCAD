package ru.ruslan.spring.cad.Controllers;

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
import ru.ruslan.spring.cad.*;

import java.text.DecimalFormat;
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
    @FXML
    private Label scaleLabel;
    private Scene scene;
    private Mode mode = Mode.PANORAM;

    private CoordSystem coordSystem;

    DecimalFormat decimalFormat = new DecimalFormat( "#.###" );



    private double scale = 1.0;
    private double zoomFactor = 2;

    private double realX;
    private double realY;

    private double originX;   //координаты системы координат в окне
    private double originY;

    double firstX,firstY;
    double secondX, secondY;



    private MyLine currentMyLine;
    private Circle currentCircle;
    private Rectangle currentRect;

    public MainWindowController (){

    }


    public void initialize(){
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
                        });
                    }
                });
            }
        });

        canvas.widthProperty().addListener((observable, oldValue, newValue) -> {
            canvas.getChildren()
                    .stream()
                    .filter(node -> node instanceof CoordSystem).findFirst()
                    .ifPresentOrElse(
                            node -> ((CoordSystem) node).extendAxisX((double) newValue - (double) oldValue),
                            () -> System.err.println("CoordSystem not found in canvas!")
                    );
        });

        canvas.heightProperty().addListener((observable, oldValue, newValue) -> {
            canvas.getChildren().stream()
                    .filter(node -> node instanceof CoordSystem)
                    .findFirst()
                    .ifPresentOrElse(
                            node -> ((CoordSystem) node).extendAxisY((double) newValue - (double) oldValue),
                            () -> System.err.println("CoordSystem not found in canvas!")
                    );
        });


        canvas.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent scrollEvent) {
                zoom(scrollEvent);
                scrollEvent.consume();
            }
        });

    }

    private void updateAllCoord(double raznicaX,double raznicaY){

        for (Node node : canvas.getChildren()){
            if (node instanceof MyLine){
                MyLine line = (MyLine)node;

                line.shift(raznicaX,raznicaY);
            } else if (node instanceof Line){
                Line line = (Line)node;

                line.setStartX(line.getStartX()+raznicaX);
                line.setStartY(line.getStartY()+raznicaY);
                line.setEndX(line.getEndX()+raznicaX);
                line.setEndY(line.getEndY()+raznicaY);
            } else if (node instanceof CoordSystem){
                CoordSystem coordSystem1 = (CoordSystem) node;
                coordSystem1.shift(raznicaX,raznicaY);
            } else if (node instanceof Circle){
                Circle circle = (Circle) node;
                circle.shift(raznicaX,raznicaY);
            } else if (node instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) node;
                rectangle.shift(raznicaX, raznicaY);
            }
        }


        coordSystem.setOriginY(coordSystem.getOriginY() + raznicaY);
        coordSystem.setOriginX(coordSystem.getOriginX() + raznicaX);
    }


    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setCoordSystem() {
        coordSystem = new CoordSystem(canvas.getHeight(), canvas.getWidth());
        canvas.getChildren().add(coordSystem);
    }

    public void createRectangle(MouseEvent mouseEvent){
        if (currentRect == null){
            firstX = mouseEvent.getX();
            firstY = mouseEvent.getY();

            currentRect = new Rectangle(firstX, firstY, 0, 0);
        } else {
            secondX = mouseEvent.getX();
            secondY = mouseEvent.getY();

            currentRect = new Rectangle(firstX,firstY,secondX,secondY);
            currentRect.draw(canvas);

            currentRect = (Rectangle) resetObjAndCoord();
        }
    }

    public void createCircle(MouseEvent mouseEvent){
        if (currentCircle == null) {
            firstX = mouseEvent.getX();
            firstY = mouseEvent.getY();

            currentCircle = new Circle(firstX,firstY,0);
        } else {
            secondX = mouseEvent.getX();
            secondY = mouseEvent.getY();

            double radius = Math.sqrt(Math.pow(secondX-firstX,2)+Math.pow(secondY-firstY,2));

            currentCircle = new Circle(firstX,firstY,radius);
            currentCircle.draw(canvas);

            currentCircle = (Circle) resetObjAndCoord();

        }
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

            currentMyLine = (MyLine) resetObjAndCoord();

        }




    }

    public Node resetObjAndCoord(){
        firstX = 0;
        firstY = 0;
        secondX = 0;
        secondY = 0;

        return null;
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
    public void changeModeToCircle(ActionEvent event){
        this.mode = Mode.DRAW_CIRCLE;
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
            case DRAW_CIRCLE -> setDrawCircleHandler();
            case DRAW_RECT -> setDrawRectHandler();
        }
    }

    private void setDrawRectHandler() {
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                createRectangle(mouseEvent);
            }
        });
    }

    private void setDrawCircleHandler() {
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                createCircle(mouseEvent);
            }
        });
    }

    private void setPanoramHandler() {
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                firstX = mouseEvent.getX();
                firstY = mouseEvent.getY();
                System.out.println(canvas.getChildren());

            }
        });

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                secondX = mouseEvent.getX();
                secondY = mouseEvent.getY();
                double raznicaX = secondX - firstX;
                double raznicaY = secondY - firstY;

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

    private void setZoomHandler() {
        canvas.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent scrollEvent) {
                zoom(scrollEvent);
                scrollEvent.consume();
            }
        });
    }

    private void coordUpd(MouseEvent mouseEvent){

        double[] coord = coordSystem.tranclateCoord(mouseEvent.getX(),mouseEvent.getY(), scale);

        coordX.setText(String.format("%.2f",coord[0]));
        coordY.setText(String.format("%.2f",coord[1]));
        scaleLabel.setText(String.format("%.2f",scale));
        //System.out.println(scale);
    }

    private void zoom(ScrollEvent scrollEvent){

        double mouseX = scrollEvent.getX();
        double mouseY = scrollEvent.getY();

        double delta = (scrollEvent.getDeltaY() > 0) ? zoomFactor : (1/zoomFactor);

        scale *= delta;

        //System.out.println("delta" + delta);
        for (Node node : canvas.getChildren()){
            if (node instanceof MyLine){

                MyLine myLine = (MyLine) node;

                myLine.zoom(scrollEvent,delta);

            } else if (node instanceof CoordSystem) {
                CoordSystem coordSystem = (CoordSystem) node;

                double shiftX = coordSystem.getOriginX() - mouseX;
                double shiftY = coordSystem.getOriginY() - mouseY;

                double newOriginX = mouseX + shiftX * delta;
                double newOriginY = mouseY + shiftY * delta;

                coordSystem.setOriginX(newOriginX);
                coordSystem.setOriginY(newOriginY);
                coordSystem.moveTo(newOriginX,newOriginY);


            } else if (node instanceof Circle){
                Circle circle = (Circle) node;
                circle.zoom(scrollEvent,delta);
            } else if (node instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) node;
                rectangle.zoom(scrollEvent,delta);

            }
        }
    }


    public void changeModeToRectrangle(ActionEvent event) {
        this.mode = Mode.DRAW_RECT;
        setEventHandlerBasedOnMode();
    }
}