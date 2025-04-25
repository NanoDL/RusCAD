package ru.ruslan.spring.cad.Controllers;

import javafx.scene.layout.Pane;
import ru.ruslan.spring.cad.Savers.AsposeDXFImporter;
import ru.ruslan.spring.cad.Services.CoordService;

import ru.ruslan.spring.cad.Controllers.StyleMenuContrl;

public class ImportsController {

    /**
     * Импортирует DXF файл
     * @param filePath путь к файлу
     * @param targetPane целевая панель
     * @param coordService сервис координат
     * @param styleMenuContrl контроллер стилей
     * @return true если импорт успешен, false в противном случае
     */
    public boolean importDXF(String filePath, Pane targetPane, CoordService coordService, StyleMenuContrl styleMenuContrl) {

            return AsposeDXFImporter.importFromDXF(filePath, targetPane, coordService, styleMenuContrl);
    }
}