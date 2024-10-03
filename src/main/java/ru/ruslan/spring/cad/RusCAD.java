package ru.ruslan.spring.cad;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class RusCAD extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/ruslan/spring/cad/hello.fxml"));
        Parent root = loader.load();

        MainWindowController controller = loader.getController();


        // Загружаем изображение для курсора
        Image crosshairImage = new Image("file:src/main/resources/cursor1.png"); // Путь к изображению перекрестия
        // Создаем кастомный курсор
        Cursor crosshairCursor = new ImageCursor(crosshairImage,
                crosshairImage.getWidth() / 2,
                crosshairImage.getHeight() / 2);
        stage.setMinHeight(600);
        stage.setMinWidth(800);

        Scene scene = new Scene(root,800,600);

        scene.setCursor(crosshairCursor);
        controller.setScene(scene);


        stage.setScene(scene);
        stage.setTitle("RusCAD");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}