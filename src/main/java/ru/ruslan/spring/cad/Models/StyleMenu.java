package ru.ruslan.spring.cad.Models;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import ru.ruslan.spring.cad.Controllers.StyleMenuContrl;
import ru.ruslan.spring.cad.Interfaces.Stylized;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StyleMenu extends Popup {
    private Map<String, List<Stylized>> objects;
    private StyleMenuContrl styleMenuContrl;

    private VBox container = new VBox();



    private Label widthL = new Label("Толщина:");
    private TextField field = new TextField("1.0");

    private Label typeL = new Label("Тип линии:");
    private ComboBox<String> box = new ComboBox<>();


    private Button button = new Button("Применить");
    private Button changeStyleBt = new Button("Сменить стиль");

    private HBox hbox = new HBox(button);

    private Map<String,  Double> dashes = new HashMap<>();
    private Map<String,  Double> dots = new HashMap<>();
    private Map<String,  Double> spaces = new HashMap<>();
    private Map<String, Double> widths = new HashMap<>();

    // Поля для дополнительных параметров
    private Label dashLabel = new Label("Штрихи:");
    private TextField dashField = new TextField("1");

    private Label dotLabel = new Label("Точки:");
    private TextField dotField = new TextField("1");

    private Label spaceLabel = new Label("Пробелы:");
    private TextField spaceField = new TextField("1");


    public StyleMenu(){
        super();
        box.getItems().addAll("Основная толстая", "Основная тонкая", "Штриховая", "Штрих-пунктирная", "Штрих-пунктирная с двумя точками");
        
        // Устанавливаем значения по умолчанию для всех стилей
        // Толщины линий
        widths.put("Основная толстая", 2.0);
        widths.put("Основная тонкая", 1.0);
        widths.put("Штриховая", 1.0);
        widths.put("Штрих-пунктирная", 1.0);
        widths.put("Штрих-пунктирная с двумя точками", 1.0);
        
        // Параметры для штриховой линии
        dashes.put("Штриховая", 5.0);
        spaces.put("Штриховая", 5.0);
        
        // Параметры для штрих-пунктирной линии
        dashes.put("Штрих-пунктирная", 5.0);
        dots.put("Штрих-пунктирная", 1.0);
        spaces.put("Штрих-пунктирная", 2.0);
        
        // Параметры для штрих-пунктирной линии с двумя точками
        dashes.put("Штрих-пунктирная с двумя точками", 5.0);
        dots.put("Штрих-пунктирная с двумя точками", 1.0);
        spaces.put("Штрих-пунктирная с двумя точками", 2.0);

        box.setValue("Основная тонкая");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                widths.put("Основная тонкая", Double.parseDouble(field.getText()));
                styleMenuContrl.updateStyle();
            }
        });
        // Изначально скрываем дополнительные поля
        dashLabel.setVisible(false);
        dashField.setVisible(false);
        dotLabel.setVisible(false);
        dotField.setVisible(false);
        spaceLabel.setVisible(false);
        spaceField.setVisible(false);

        // Добавляем слушатель изменения выбора
        box.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                switch (newValue) {
                    case "Штриховая":
                        showAdditionalFields(true, false, true);

                        setDefaultValues("Штриховая");
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                widths.put(newValue, Double.parseDouble(field.getText()));
                                dashes.put(newValue, Double.parseDouble(dashField.getText()));
                                spaces.put(newValue, Double.parseDouble(spaceField.getText()));
                                styleMenuContrl.changeObjectStyle();
                                styleMenuContrl.updateStyle();
                            }
                        });
                        break;
                    case "Штрих-пунктирная":
                        showAdditionalFields(true, true, true);
                        setDefaultValues("Штрих-пунктирная");
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                widths.put(newValue, Double.parseDouble(field.getText()));
                                dashes.put(newValue, Double.parseDouble(dashField.getText()));
                                dots.put(newValue, Double.parseDouble(dotField.getText()));
                                spaces.put(newValue, Double.parseDouble(spaceField.getText()));
                                styleMenuContrl.changeObjectStyle();
                                styleMenuContrl.updateStyle();
                            }
                        });
                        break;
                    case "Штрих-пунктирная с двумя точками":
                        showAdditionalFields(true, true, true);
                        setDefaultValues("Штрих-пунктирная с двумя точками");
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                widths.put(newValue, Double.parseDouble(field.getText()));
                                dashes.put(newValue, Double.parseDouble(dashField.getText()));
                                dots.put(newValue, Double.parseDouble(dotField.getText()));
                                spaces.put(newValue, Double.parseDouble(spaceField.getText()));
                                styleMenuContrl.changeObjectStyle();
                                styleMenuContrl.updateStyle();
                            }
                        });
                        break;
                    default:
                        showAdditionalFields(false, false, false);
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                widths.put(newValue, Double.parseDouble(field.getText()));
                                styleMenuContrl.changeObjectStyle();
                                styleMenuContrl.updateStyle();
                            }
                        });
                        break;
                }
            }
        });

        changeStyleBt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                styleMenuContrl.changeObjectStyle();
            }
        });

        // Добавляем элементы в контейнер
        container.getChildren().addAll(
                widthL, field, typeL, box,
                dashLabel, dashField,
                dotLabel, dotField,
                spaceLabel, spaceField,
                hbox
        );
        container.setSpacing(10); // Расстояние между элементами
        container.setPadding(new Insets(15)); // Внутренние отступы
        container.setStyle("""
        -fx-background-color: #f9f9f9;
        -fx-border-color: #d3d3d3;
        -fx-border-width: 1px;
        -fx-border-radius: 10px;
        -fx-background-radius: 10px;
        -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);
    """);
        // Добавляем контейнер в Popup
        getContent().addAll(container);
    }


    // Метод для управления отображением дополнительных полей
    private void showAdditionalFields(boolean showDashes, boolean showDots, boolean showSpaces) {
        dashLabel.setVisible(showDashes);
        dashField.setVisible(showDashes);
        dotLabel.setVisible(showDots);
        dotField.setVisible(showDots);
        spaceLabel.setVisible(showSpaces);
        spaceField.setVisible(showSpaces);
    }

    // Метод для установки значений по умолчанию
    private void setDefaultValues(String str) {
        switch (str){
            case "Штриховая":
                dashField.setText(String.valueOf(dashes.get("Штриховая")));
                spaceField.setText(String.valueOf(spaces.get("Штриховая")));
                break;
            case "Штрих-пунктирная":
                dashField.setText(String.valueOf(dashes.get("Штрих-пунктирная")));
                spaceField.setText(String.valueOf(spaces.get("Штрих-пунктирная")));
                dotField.setText(String.valueOf(dots.get("Штрих-пунктирная")));
                break;
            case "Штрих-пунктирная с двумя точками":
                dashField.setText(String.valueOf(dashes.get("Штрих-пунктирная с двумя точками")));
                spaceField.setText(String.valueOf(spaces.get("Штрих-пунктирная с двумя точками")));
                dotField.setText(String.valueOf(dots.get("Штрих-пунктирная с двумя точками")));
                break;
        }

    }

    public Map<String, Double> getDashes() {
        return dashes;
    }

    public void setDashes(Map<String, Double> dashes) {
        this.dashes = dashes;
    }

    public Map<String, Double> getDots() {
        return dots;
    }

    public void setDots(Map<String, Double> dots) {
        this.dots = dots;
    }

    public Map<String, Double> getSpaces() {
        return spaces;
    }

    public void setSpaces(Map<String, Double> spaces) {
        this.spaces = spaces;
    }

    public ComboBox<String> getBox() {
        return box;
    }

    public void setBox(ComboBox<String> box) {
        this.box = box;
    }

    public Map<String, List<Stylized>> getObjects() {
        return objects;
    }

    public void setObjects(Map<String, List<Stylized>> objects) {
        this.objects = objects;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public StyleMenuContrl getStyleMenuContrl() {
        return styleMenuContrl;
    }

    public void setStyleMenuContrl(StyleMenuContrl styleMenuContrl) {
        this.styleMenuContrl = styleMenuContrl;
    }

    public Map<String, Double> getWidths() {
        return widths;
    }

    public void setWidths(Map<String, Double> widths) {
        this.widths = widths;
    }
}
