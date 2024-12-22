package ru.ruslan.spring.cad.Controllers;

import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import ru.ruslan.spring.cad.Interfaces.*;
import ru.ruslan.spring.cad.Mode;
import ru.ruslan.spring.cad.Models.*;
import ru.ruslan.spring.cad.Services.CoordService;

import java.util.ArrayList;
import java.util.List;


public class CanvasController {

    private final Pane canvas;
    private final MyPopup popup;

    private Mode mode;

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
    private Label modeLabel;

    private MyLine currentMyLine;
    private Rectangle currentRect;
    private MyCircle currentMyCircle;
    private MySpline currentMySpline;
    private Group tempPoints = new Group();

    private List<Figure> selected = new ArrayList<>();

    private double scale = 1.0;
    //private double zoomFactor = 2;

    private ControlPanelController controlPanelController;
    private StyleMenuContrl styleMenuContrl;


    private CoordSystem coordSystem;
    private CoordService coordService;

    private double firstX;
    private double firstY;
    private double secondX;
    private double secondY;

    private List<Double> coordinates = new ArrayList<>();

    private double tempX;
    private double tempY;

    private EventHandler<MouseEvent> highlightHandler;
    private EventHandler<MouseEvent> createLineHandler;
    private EventHandler<MouseEvent> showPopupHandler;
    private EventHandler<KeyEvent> createShapeByPopup;
    private EventHandler<MouseEvent> createShapeByMouse;
    private EventHandler<MouseEvent> createCircleHandler;
    //private EventHandler<MouseEvent> createCircle3DotsHandler;
    private EventHandler<MouseEvent> createRectangleHandler;


    public CanvasController(Pane canvas) {
        this.canvas = canvas;
        canvas.setFocusTraversable(true);
        popup = new MyPopup();
        popup.setAutoHide(true);

    }

    public Label getModeLabel() {
        return modeLabel;
    }

    public void setModeLabel(Label modeLabel) {
        this.modeLabel = modeLabel;
    }

    public StyleMenuContrl getStyleMenuContrl() {
        return styleMenuContrl;
    }

    public void setStyleMenuContrl(StyleMenuContrl styleMenuContrl) {
        this.styleMenuContrl = styleMenuContrl;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Label getCoordX() {
        return coordX;
    }

    public void setCoordX(Label coordX) {
        this.coordX = coordX;
    }

    public Label getCoordY() {
        return coordY;
    }

    public void setCoordY(Label coordY) {
        this.coordY = coordY;
    }

    public Label getScrCoordX() {
        return scrCoordX;
    }

    public void setScrCoordX(Label scrCoordX) {
        this.scrCoordX = scrCoordX;
    }

    public Label getScrCoordY() {
        return scrCoordY;
    }

    public void setScrCoordY(Label scrCoordY) {
        this.scrCoordY = scrCoordY;
    }

    public Label getScaleLabel() {
        return scaleLabel;
    }

    public void setScaleLabel(Label scaleLabel) {
        this.scaleLabel = scaleLabel;
    }

    public CoordSystem getCoordSystem() {
        return coordSystem;
    }

    public Label getCoordModeLabel() {
        return coordModeLabel;
    }

    public void setCoordModeLabel(Label coordModeLabel) {
        this.coordModeLabel = coordModeLabel;
    }

    private void lablesUpd(MouseEvent mouseEvent) {

        double[] coord = coordSystem.translateScreenToReal(mouseEvent.getX(), mouseEvent.getY());

        coordX.setText(String.format("%.2f", coord[0]));
        coordY.setText(String.format("%.2f", coord[1]));
        scrCoordX.setText(String.valueOf(mouseEvent.getX()));
        scrCoordY.setText(String.valueOf(mouseEvent.getY()));
        scaleLabel.setText(String.format("%.2f", scale));
        coordModeLabel.setText(String.valueOf(coordSystem.getCoordMode()));
        modeLabel.setText(String.valueOf(mode));
        //System.out.println(scale);
    }

    public void initialize() {

        highlightHandler = mouseEvent -> highlight(mouseEvent);
        createLineHandler = mouseEvent -> createLine(mouseEvent);
        createShapeByPopup = keyEvent -> createShapeByPopup(keyEvent);
        createShapeByMouse = mouseEvent -> createShapeByMouse(mouseEvent);
        showPopupHandler = mouseEvent -> showPopup(mouseEvent);
        createCircleHandler = mouseEvent -> createCircle(mouseEvent);
        createRectangleHandler = mouseEvent -> createRectangle(mouseEvent);

        popup.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                canvas.requestFocus();
                setEventHandlerBasedOnMode(Mode.PANORAM);
            }
        });

        canvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                lablesUpd(mouseEvent);
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
                            canvas.getChildren().add(tempPoints);
                            coordService = new CoordService(coordSystem);
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

        // Добавляем слушатель на изменения в списке детей canvas
        canvas.getChildren().addListener((ListChangeListener<Node>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    // Проходим по всем добавленным объектам
                    for (Node addedNode : change.getAddedSubList()) {
                        if (addedNode instanceof Stylized) {
                            // Если добавленный объект - линия, добавляем в массив тонких линий
                            styleMenuContrl.addObjectToStyle("Основная тонкая", (Stylized)addedNode);

                            System.out.println("Добавлена новая линия!");
                        }
                    }
                }
            }
        });

    }


    private void setCoordSystem() {
        coordSystem = new CoordSystem(canvas.getHeight(), canvas.getWidth());
        canvas.getChildren().add(coordSystem);
        controlPanelController.setCoordSystem(coordSystem);
    }

    public void showPopup(MouseEvent mouseEvent) {
        double[] coord = coordSystem.translateScreenToReal(mouseEvent.getX(), mouseEvent.getY());

        popup.setDefaultCoord(coord[0], coord[1]);
        popup.show(canvas, mouseEvent.getScreenX() + 20, mouseEvent.getScreenY() + 20);
        canvas.requestFocus();
    }

    public void hidePopup() {
        if (popup.isShowing()) {
            popup.hide();
        }
    }

    private void createShapeByMouse(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            List<Double> coord = getCoordFromMouse(mouseEvent);

            tempPoints.getChildren().add(new Point2D(coord.get(0), coord.get(1)));
            switch (mode) {
                case DRAW_CIRCLE3DOTS -> {
                    if (isEnoughCoord(mode)) {
                        createCircle3Dots(coordinates);
                    }
                }
                case DRAW_SPLINEBEZIE -> {
                    if (isEnoughCoord(mode)) {
                        createSplineBezie(coordinates);
                        List<Double> lastPoint = List.copyOf(coordinates.subList(6, 8));
                        coordinates.clear();
                        coordinates.addAll(lastPoint);
                    }
                }
                case DRAW_ARCRC -> {
                    if (isEnoughCoord(mode)) {
                        //coordinates.add(popup.get3Field());
                        createArc(coordinates, mode);
                    }
                }
                case DRAW_ARC3DOTS -> {
                    if (isEnoughCoord(mode)) {
                        createArc(coordinates, mode);
                    }
                }
                case DRAW_POLYGON_IN, DRAW_POLYGON_OUT -> {
                    if (isEnoughCoord(mode)) {
                        createPolygon(coordinates, popup.get4Field());
                    } else if (isPopupFieldsEnough(mode)) {
                        createPolygon(coordinates, popup.get3Field(), popup.get4Field());
                    }

                }
                case DRAW_RECT_CENTR -> {
                    if (isEnoughCoord(mode)) {
                        createRectangleCentr(coordinates);
                    }
                }
                case DRAW_SPLINE_MYSPLINE -> {
                    if (isEnoughCoord(mode)) {
                        createMySpline(coordinates);
                        System.out.println(coordinates);
                    }
                }
            }
        }
    }

    public void createShapeByPopup(KeyEvent keyEvent) {

        if (keyEvent.getCode() == KeyCode.ENTER) {
            double[] coord = popup.getCoordinates();
            getCoordFromPopup();
            List<Double> screenCoord = coordSystem.translateRealToScreen(List.of(coord[0], coord[1]));
            tempPoints.getChildren().add(new Point2D(screenCoord.get(0), screenCoord.get(1)));
            switch (mode) {
                case DRAW_LINE -> {
                    if (coordSystem.getCoordMode() == CoordMode.POLAR) {
                        createLineByPolar(coord[0], coord[1]);
                        tempPoints.getChildren().clear();
                    } else if (coordSystem.getCoordMode() == CoordMode.DECART) {
                        createLineByDecart(coord[0], coord[1]);

                    }
                }
                case DRAW_CIRCLE -> createCircle(coord[0], coord[1], popup.get3Field());
                case DRAW_CIRCLE3DOTS -> {
                    // getCoordFromPopup();
                    if (isEnoughCoord(mode)) {
                        createCircle3Dots(coordinates);
                        coordinates.clear();
                        tempPoints.getChildren().clear();
                    }

                }
                case DRAW_RECT -> createRectangle(coord[0], coord[1]);
                case DRAW_RECT_CENTR -> {
                    if (isEnoughCoord(mode)) {
                        //getCoordFromPopup();
                        createRectangleCentr(coordinates);
                        tempPoints.getChildren().clear();
                    }
                }
                case DRAW_ARCRC -> {
                    if (isEnoughCoord(mode)) {
                        coordinates.add(popup.get3Field());
                        createArc(coordinates, mode);
                        tempPoints.getChildren().clear();
                    }
                }
                case DRAW_ARC3DOTS -> {
                    if (isEnoughCoord(mode)) {
                        createArc(coordinates, mode);
                        tempPoints.getChildren().clear();
                    }
                }
                case DRAW_POLYGON_IN, DRAW_POLYGON_OUT -> {
                    if (isEnoughCoord(mode)) {
                        createPolygon(coordinates, popup.get4Field());
                        tempPoints.getChildren().clear();
                    } else if (isPopupFieldsEnough(mode)) {
                        createPolygon(coordinates, popup.get3Field(), popup.get4Field());
                        tempPoints.getChildren().clear();
                    }

                }
                case DRAW_SPLINE_MYSPLINE -> {
                    if (isEnoughCoord(mode)) {
                        createMySpline(coordinates);
                        System.out.println(coordinates);
                        tempPoints.getChildren().clear();
                    }
                }
                case DRAW_SPLINEBEZIE -> {
                    if (isEnoughCoord(mode)) {
                        createSplineBezie(coordinates);
                        List<Double> lastPoint = List.copyOf(coordinates.subList(6, 8));
                        coordinates.clear();
                        coordinates.addAll(lastPoint);
                        tempPoints.getChildren().clear();
                    }
                }

            }

        }


    }

    private void createMySpline(List<Double> coordinates) {
        List<Double> coord = coordSystem.translateRealToScreen(coordinates);
        canvas.getChildren().remove(currentMySpline);
        currentMySpline = new MySpline(coord);
        currentMySpline.draw(canvas);
    }


    private void createRectangleCentr(List<Double> coordinates) {
        List<Double> coord = coordSystem.translateRealToScreen(coordinates);
        double centrX = coord.get(0);
        double centrY = coord.get(1);
        double x1 = coord.get(2);
        double y1 = coord.get(3);

        double distanceX = x1 - centrX;
        double distanceY = y1 - centrY;

        double x2 = centrX - distanceX;
        double y2 = centrY - distanceY;

        currentRect = new Rectangle(x1, y1, x2, y2);
        coordService.updateFigureRealCoord(currentRect);
        currentRect.draw(canvas);
        resetObjAndCoord();
    }

    private void createPolygon(List<Double> coordinates, double linesCount) {
        if (linesCount >= 3) {
            List<Double> screenCoord = coordSystem.translateRealToScreen(coordinates);
            Polygon poly = new Polygon(screenCoord, linesCount, mode);
            poly.setRealCoordinates(coordinates);
            poly.draw(canvas);
        } else {
            System.out.println("Не то число сторон!");
        }

        resetObjAndCoord();
    }

    private void createPolygon(List<Double> coordinates, double radius, double linesCount) {
        if (linesCount >= 3 && radius > 0.0) {
            List<Double> screenCoord = coordSystem.translateRealToScreen(coordinates);
            Polygon poly = new Polygon(screenCoord, radius * scale, linesCount, mode);
            poly.setRealCoordinates(coordinates);
            poly.setRadius(radius);
            poly.draw(canvas);
        } else {
            System.out.println("Не то число сторон!");
        }

        resetObjAndCoord();
    }

    private void createArc(List<Double> coordinates, Mode mode) {
        List<Double> screenCoord = coordSystem.translateRealToScreen(coordinates);
        MyArc arc = null;
        switch (mode) {
            case DRAW_ARC3DOTS -> {
                arc = new MyArc(screenCoord);
                System.out.println(mode);
            }
            case DRAW_ARCRC -> {
                double x1 = screenCoord.get(0);
                double y1 = screenCoord.get(1);
                double x2 = screenCoord.get(2);
                double y2 = screenCoord.get(3);
                double centerX = screenCoord.get(4);
                double centerY = screenCoord.get(5);

                arc = new MyArc(x1, y1, x2, y2, centerX, centerY);
            }
            default -> {
                return;
            }
        }

        coordService.updateFigureRealCoord(arc);
        arc.draw(canvas);
        resetObjAndCoord();
    }

    private void createSplineBezie(List<Double> coordinates) {
        List<Double> screenCoord = coordSystem.translateRealToScreen(coordinates);

        BezieSpline spline = new BezieSpline(screenCoord);
        spline.setRealCoordinates(coordinates);
        spline.draw(canvas);

    }

    private boolean isEnoughCoord(Mode mode) {
        switch (mode) {
            case DRAW_CIRCLE3DOTS, DRAW_ARC3DOTS, DRAW_ARCRC -> {
                return coordinates.size() == 6;
            }
            case DRAW_SPLINEBEZIE -> {
                return coordinates.size() == 8;
            }
            case DRAW_POLYGON_IN, DRAW_POLYGON_OUT, DRAW_RECT_CENTR -> {
                return coordinates.size() == 4;
            }
            case DRAW_SPLINE_MYSPLINE -> {
                return coordinates.size() >= 4;
            }
            case DRAW_LINE, DRAW_CIRCLE, DRAW_RECT -> {
                return coordinates.size() >= 0;
            }
            default -> {
                return false;
            }
        }

    }

    private boolean isPopupFieldsEnough(Mode mode) {
        switch (mode) {
            case DRAW_POLYGON_IN, DRAW_POLYGON_OUT -> {
                return popup.get3Field() != 0.0;

            }
            default -> {
                return false;
            }
        }
    }


    public void createLineByPolar(double radius, double angle) {

        double[] coord = new double[2];

        if (currentMyLine == null) {
            firstX = radius;
            firstY = angle;

            currentMyLine = new MyLine(firstX, firstY, secondX, secondY);
            popup.setPolarMode(true);
        } else {
            // Вычисление второй точки по полярным координатам
            double deltaX = radius * Math.cos(Math.toRadians(angle)); // Смещение по X
            double deltaY = radius * Math.sin(Math.toRadians(angle)); // Смещение по Y

            secondX = firstX + deltaX; // Вторая точка X
            secondY = firstY + deltaY; // Вторая точка Y


            coord = coordSystem.translateRealToScreen(firstX, firstY);

            tempX = firstX;
            tempY = firstY;

            firstX = coord[0];
            firstY = coord[1];



            coord = coordSystem.translateRealToScreen(secondX, secondY, scale);

            currentMyLine = new MyLine(firstX, firstY, coord[0], coord[1]);

            currentMyLine.setRealX1(tempX);
            currentMyLine.setRealY1(tempY);
            currentMyLine.setRealX2(secondX);
            currentMyLine.setRealY2(secondY);

            currentMyLine.draw(canvas);

            firstX = 0;
            firstY = 0;
            secondX = 0;
            secondY = 0;
            currentMyLine = null;
            popup.setPolarMode(false);
        }

    }

    public void createLineByDecart(double x, double y) {

        double[] coord;

        if (currentMyLine == null) {

            firstX = x;
            firstY = y;

            currentMyLine = new MyLine(firstX, firstY, 0, 0);
        } else {
            coord = coordSystem.translateRealToScreen(firstX, firstY);

            tempX = firstX;
            tempY = firstY;

            firstX = coord[0];
            firstY = coord[1];

            coord = coordSystem.translateRealToScreen(x, y);

            secondX = coord[0];
            secondY = coord[1];

            currentMyLine = new MyLine(firstX, firstY, secondX, secondY);

            currentMyLine.setRealX1(tempX);
            currentMyLine.setRealY1(tempY);
            currentMyLine.setRealX2(x);
            currentMyLine.setRealY2(y);

            currentMyLine.draw(canvas);

            resetObjAndCoord();
            currentMyLine = null;


        }
    }

    public void createRectangle(MouseEvent mouseEvent) {

        double[] coord;
        if (currentRect == null) {
            firstX = mouseEvent.getX();
            firstY = mouseEvent.getY();

            coord = coordSystem.translateScreenToReal(firstX, firstY);

            firstX = coord[0];
            firstY = coord[1];

            currentRect = new Rectangle(firstX, firstY, 0, 0);
        } else {
            secondX = mouseEvent.getX();
            secondY = mouseEvent.getY();

            coord = coordSystem.translateRealToScreen(firstX, firstY, scale);

            tempX = firstX;
            tempY = firstY;

            firstX = coord[0];
            firstY = coord[1];

            currentRect = new Rectangle(firstX, firstY, secondX, secondY);

            coord = coordSystem.translateScreenToReal(secondX, secondY);

            double realX1 = tempX;
            double realY1 = tempY;
            double realX2 = coord[0];
            double realY2 = coord[1];

            currentRect.setRealCoordinates(List.of(realX1, realY1, realX2, realY1, realX2, realY2, realX1, realY2));
            System.out.println(currentRect.getRealCoordinates());
            currentRect.draw(canvas);

            currentRect = null;

            resetObjAndCoord();
        }
    }

    public void createRectangle(double x, double y) {
        if (currentRect == null) {
            firstX = x;
            firstY = y;

            currentRect = new Rectangle(firstX, firstY, 0, 0);
        } else {
            secondX = x;
            secondY = y;

            double[] coord = coordSystem.translateRealToScreen(firstX, firstY, scale);
            tempX = firstX;
            tempY = firstY;

            firstX = coord[0];
            firstY = coord[1];
            coord = coordSystem.translateRealToScreen(secondX, secondY, scale);

            currentRect = new Rectangle(firstX, firstY, coord[0], coord[1]);

            double realX1 = tempX;
            double realY1 = tempY;
            double realX2 = secondX;
            double realY2 = secondY;

            currentRect.setRealCoordinates(List.of(realX1, realY1, realX2, realY1, realX2, realY2, realX1, realY2));

            currentRect.draw(canvas);

            currentRect = null;

            resetObjAndCoord();
        }
    }

    public void createCircle(MouseEvent mouseEvent) {
        if (currentMyCircle == null) {
            double[] coord = coordSystem.translateScreenToReal(mouseEvent.getX(), mouseEvent.getY());

            firstX = coord[0];
            firstY = coord[1];

            currentMyCircle = new MyCircle(mouseEvent.getX(), mouseEvent.getY(), 0);
            currentMyCircle.setRealCentX(firstX);
            currentMyCircle.setRealCentY(firstY);

        } else {
            secondX = mouseEvent.getX();
            secondY = mouseEvent.getY();

            double[] coord = coordSystem.translateRealToScreen(firstX, firstY, scale);

            firstX = coord[0];
            firstY = coord[1];

            double radius = Math.sqrt(Math.pow(secondX - firstX, 2) + Math.pow(secondY - firstY, 2));

            currentMyCircle.move(firstX, firstY);


            currentMyCircle.setRealRadius(radius);
            currentMyCircle.setRadius(radius);

            currentMyCircle.draw(canvas);

            currentMyCircle = (MyCircle) resetObjAndCoord();

        }
    }

    public void createCircle(double x, double y, double radius) {
        double[] coord = coordSystem.translateRealToScreen(x, y, scale);

        currentMyCircle = new MyCircle(coord[0], coord[1], radius * scale);

        currentMyCircle.setRealCentX(x);
        currentMyCircle.setRealCentY(y);
        currentMyCircle.setRealRadius(radius);
        currentMyCircle.draw(canvas);

        currentMyCircle = (MyCircle) resetObjAndCoord();
    }

    private void createCircle3Dots(List<Double> coordinates) {

        double x1 = coordinates.get(0);
        double y1 = coordinates.get(1);
        double x2 = coordinates.get(2);
        double y2 = coordinates.get(3);
        double x3 = coordinates.get(4);
        double y3 = coordinates.get(5);

        double a1 = x2 - x1;
        double b1 = y2 - y1;
        double c1 = (x2 * x2 - x1 * x1 + y2 * y2 - y1 * y1) / 2;

        double a2 = x3 - x2;
        double b2 = y3 - y2;
        double c2 = (x3 * x3 - x2 * x2 + y3 * y3 - y2 * y2) / 2;

        // Вычисление определителя
        double det = a1 * b2 - a2 * b1;

        if (Math.abs(det) < 1e-9) {

            System.out.println("Точки лежат на одной прямой");
            return;
            // Если определитель близок к нулю, точки лежат на одной прямой
            //return;
        }

        // Решение системы линейных уравнений
        double centerRealX = (c1 * b2 - c2 * b1) / det;
        double centerRealY = (a1 * c2 - a2 * c1) / det;

        // Радиус окружности
        double realRadius = Math.sqrt(Math.pow(centerRealX - x1, 2) + Math.pow(centerRealY - y1, 2));

        double[] coord = coordSystem.translateRealToScreen(centerRealX, centerRealY);

        currentMyCircle = new MyCircle(coord[0], coord[1], realRadius * coordSystem.getScale());

        currentMyCircle.setRealCoordinates(List.of(centerRealX, centerRealY));
        currentMyCircle.setRealRadius(realRadius);
        currentMyCircle.draw(canvas);
        resetObjAndCoord();

    }

    public void createLine(MouseEvent mouseEvent) {
        if (currentMyLine == null) {
            double[] coord = coordSystem.translateScreenToReal(mouseEvent.getX(), mouseEvent.getY());


            firstX = coord[0];
            firstY = coord[1];
            secondX = firstX;
            secondY = firstY;

            currentMyLine = new MyLine(firstX, firstY, secondX, secondY);
        } else {
            //canvas.getChildren().remove(currentMyLine);
            secondX = mouseEvent.getX();
            secondY = mouseEvent.getY();

            double[] coord = coordSystem.translateRealToScreen(firstX, firstY, scale);
            currentMyLine = new MyLine(coord[0], coord[1], secondX, secondY);


            coord = coordSystem.translateScreenToReal(secondX, secondY);

            currentMyLine.setRealX1(firstX);
            currentMyLine.setRealY1(firstY);
            currentMyLine.setRealX2(coord[0]);
            currentMyLine.setRealY2(coord[1]);

            currentMyLine.draw(canvas);

            currentMyLine = null;

            resetObjAndCoord();

        }
    }

    private void setDrawRectHandler() {
        canvas.setOnMouseMoved(withLablesUpdate(showPopupHandler));
        canvas.setOnMouseClicked(createRectangleHandler);

        canvas.setOnKeyPressed(createShapeByPopup);
    }

    private void setDrawCircleHandler() {
        canvas.setOnMouseMoved(withLablesUpdate(showPopupHandler));
        canvas.setOnMouseClicked(createCircleHandler);
        canvas.setOnKeyPressed(createShapeByPopup);
    }

    private void setPanoramHandler() {
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.isMiddleButtonDown()) {
                    firstX = mouseEvent.getX();
                    firstY = mouseEvent.getY();
                    System.out.println(canvas.getChildren());
                }


            }
        });

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.isMiddleButtonDown()) {
                    secondX = mouseEvent.getX();
                    secondY = mouseEvent.getY();
                    double raznicaX = secondX - firstX;
                    double raznicaY = secondY - firstY;

                    shiftAllObjects(raznicaX, raznicaY);
                    firstX = secondX;
                    firstY = secondY;
                }

            }
        });

    }

    private void setDrawLineHandler() {
        canvas.setOnMouseMoved(withLablesUpdate(showPopupHandler));
        canvas.setOnMouseClicked(createLineHandler);
        canvas.setOnKeyPressed(createShapeByPopup);
    }

    public void setHighlightHandler() {
        //canvas.setOnMouseMoved(withLablesUpdate(highlightHandler));
    }

    //смена обработчиков
    public void setEventHandlerBasedOnMode(Mode mode) {
        this.mode = mode;
        clearAllPaneHandlers();
        switch (this.mode) {
            case PANORAM -> {
                setPanoramHandler();
                setHighlightHandler();
                setSelectHandler();
                canvas.setOnKeyPressed(withCancelSelect(null));
            }
            case DRAW_LINE -> setDrawLineHandler();
            case DRAW_CIRCLE -> {
                setDrawCircleHandler();
                popup.enableOtherFields(Mode.DRAW_CIRCLE);
            }
            case DRAW_RECT -> setDrawRectHandler();
            case DIVIDE -> setDivideHandler();
            case DRAW_CIRCLE3DOTS, DRAW_SPLINEBEZIE, DRAW_RECT_CENTR, DRAW_ARC3DOTS, DRAW_SPLINE_MYSPLINE -> {
                canvas.setOnMouseMoved(withLablesUpdate(showPopupHandler));
                createFigure(mode);
            }
            case DRAW_ARCRC, DRAW_POLYGON_IN, DRAW_POLYGON_OUT -> {
                canvas.setOnMouseMoved(withLablesUpdate(showPopupHandler));
                createFigure(mode);
                popup.enableOtherFields(mode);
            }
        }


    }

    public void createFigure(Mode mode) {
        switch (mode) {
            case DRAW_CIRCLE3DOTS, DRAW_SPLINEBEZIE, DRAW_ARCRC, DRAW_POLYGON_IN, DRAW_POLYGON_OUT, DRAW_RECT_CENTR,
                 DRAW_ARC3DOTS, DRAW_SPLINE_MYSPLINE -> {
                canvas.setOnMouseClicked(createShapeByMouse);
                canvas.setOnKeyPressed(createShapeByPopup);
            }
        }
    }

    private List<Double> getCoordFromMouse(MouseEvent mouseEvent) {
        double[] coord = coordSystem.translateScreenToReal(mouseEvent.getX(), mouseEvent.getY());
        coordinates.addAll(List.of(coord[0], coord[1]));
        return List.of(mouseEvent.getX(), mouseEvent.getY());
    }

    private void getCoordFromPopup() {
        double[] coord = popup.getCoordinates();
        coordinates.addAll(List.of(coord[0], coord[1]));
    }

    private void setDrawCircle3DotsHandler() {
        canvas.setOnMouseMoved(withLablesUpdate(showPopupHandler));
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                getCoordFromMouse(mouseEvent);
                if (coordinates.size() == 6) {
                    createCircle3Dots(coordinates);
                    coordinates.clear();
                }
            }
        });
        canvas.setOnKeyPressed(createShapeByPopup);
    }

    private void setDivideHandler() {
        canvas.setOnMouseMoved(withLablesUpdate(highlightHandler));
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                divide(mouseEvent);
            }
        });
    }

    private void setSelectHandler() {

        canvas.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                select(mouseEvent);
            }
        });
    }

    private EventHandler<KeyEvent> withCancelSelect(EventHandler<KeyEvent> originalHandler) {
        return keyEvent -> {
            if (originalHandler != null) {
                originalHandler.handle(keyEvent); // Вызов оригинального обработчика
            }

            deSelect(keyEvent); // Обновление координат
        };
    }

    private EventHandler<MouseEvent> withLablesUpdate(EventHandler<MouseEvent> originalHandler) {
        return mouseEvent -> {
            if (originalHandler != null) {
                originalHandler.handle(mouseEvent); // Вызов оригинального обработчика
            }
            canvas.requestFocus();
            lablesUpd(mouseEvent); // Обновление координат
        };
    }

    private void clearAllPaneHandlers() {
        if (popup.isCircleMode()) {
            popup.disable3Field();
        }

        canvas.setOnMousePressed(null);
        canvas.setOnMouseDragged(null);
        canvas.setOnMouseMoved(withLablesUpdate(null));
        canvas.setOnMouseClicked(null);
        canvas.setOnKeyPressed(null);


        resetObjAndCoord();
        currentMyLine = null;
        currentMyCircle = null;
        currentRect = null;
        currentMySpline = null;
        for (Node node : canvas.getChildren()) {
            if (node instanceof Selectable) {
                selected.clear();
                ((Selectable) node).deSelect();
            }
        }
        popup.setPolarMode(false);
    }

    private void highlight(MouseEvent mouseEvent) {

        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        for (Node node : canvas.getChildren()) {
            if (node instanceof Selectable) {
                if (((Selectable) node).isNear(mouseX, mouseY)) {
                    ((Selectable) node).highlight();
                } else {
                    ((Selectable) node).deHighlight();
                }
            }

        }
    }

    private void divide(MouseEvent mouseEvent) {
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        boolean found = false;

        for (Node node : canvas.getChildren()) {
            if (node instanceof Selectable && node instanceof Divided) {
                if (((Selectable) node).isNear(mouseX, mouseY)) {

                    canvas.getChildren().addAll(((Divided) node).divide());
                    canvas.getChildren().remove(node);
                    found = true;
                    break; // Прекратить цикл

                }
            }
        }

        if (!found) {
            controlPanelController.updateControlPanel(null, null);
        }
    }

    private void select(MouseEvent mouseEvent) {

        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        System.out.println(canvas.getChildren());

        boolean found = false;

        for (Node node : canvas.getChildren()) {
            if (node instanceof Selectable) {
                if (((Selectable) node).isNear(mouseX, mouseY)) {
                    controlPanelController.updateControlPanel((Selectable) node, mouseEvent);
                    Figure fig = ((Selectable) node).select();
                    if (!selected.contains(fig)){
                        selected.add(fig);
                    }
                    found = true;
                    break; // Прекратить цикл
                }
            }
        }

        if (!found) {
            controlPanelController.updateControlPanel(null, null);
        }
        System.out.println(selected);
    }

    private void deSelect(KeyEvent keyEvent) {
        System.out.println("AAAAA");
        if (keyEvent.getCode() == KeyCode.ESCAPE){
            System.out.println("BBBBB");
            for (Node node : canvas.getChildren()) {
                if (node instanceof Selectable) {
                    selected.clear();
                    ((Selectable) node).deSelect();
                }
            }
        }

    }

    private void zoom(ScrollEvent scrollEvent) {

        double zoomFactor = 2;
        double delta = (scrollEvent.getDeltaY() > 0) ? zoomFactor : (1 / zoomFactor);

        scale *= delta;

        coordSystem.setScale(scale);
        styleMenuContrl.updateStyleOnZoom(delta);
        for (Node node : canvas.getChildren()) {
            if (node instanceof Zoomable) {
                ((Zoomable) node).zoom(scrollEvent, delta);
            }
        }
    }

    private void shiftAllObjects(double raznicaX, double raznicaY) {
        for (Node node : canvas.getChildren()) {
            if (node instanceof Movable) {
                ((Movable) node).shift(raznicaX, raznicaY);
            }
        }
    }

    public void changeCoordMode() {
        coordSystem.changeCoordMode();
        coordModeLabel.setText(String.valueOf(coordSystem.getCoordMode()));
    }

    public Node resetObjAndCoord() {
        coordinates.clear();
        firstX = 0;
        firstY = 0;
        secondX = 0;
        secondY = 0;
        tempX = 0;
        tempY = 0;
        tempPoints.getChildren().clear();
        return null;
    }

    public ControlPanelController getControlPanelController() {
        return controlPanelController;
    }

    public void setControlPanelController(ControlPanelController controlPanelController) {
        this.controlPanelController = controlPanelController;
    }

    public List<Figure> getSelected() {
        return selected;
    }

    public void setSelected(List<Figure> selected) {
        this.selected = selected;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }
}
