package ru.ruslan.spring.cad.Savers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aspose.cad.Image;
import com.aspose.cad.fileformats.cad.CadImage;

import com.aspose.cad.fileformats.cad.cadobjects.CadCircle;
import com.aspose.cad.fileformats.cad.cadobjects.CadLine;
import com.aspose.cad.fileformats.cad.cadobjects.CadArc;
import com.aspose.cad.fileformats.cad.cadobjects.CadEntityBase;
import com.aspose.cad.fileformats.cad.cadobjects.CadPoint;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import ru.ruslan.spring.cad.Controllers.StyleMenuContrl;
import ru.ruslan.spring.cad.Models.MyArc;
import ru.ruslan.spring.cad.Models.MyCircle;
import ru.ruslan.spring.cad.Models.MyLine;
import ru.ruslan.spring.cad.Interfaces.Stylized;
import ru.ruslan.spring.cad.Services.CoordService;
import ru.ruslan.spring.cad.Controllers.CanvasController;

public class AsposeDXFImporter {

    private Pane targetPane;
    private CoordService coordService;
    private StyleMenuContrl styleMenuContrl;
    private CanvasController canvasController;
    


    /**
     * Импортирует объекты из DXF-файла
     * @param filePath путь к DXF-файлу
     * @param targetPane панель для добавления импортированных объектов
     * @param coordService сервис координат
     * @param styleMenuContrl контроллер стилей
     * @return true, если импорт успешен, иначе false
     */
    public static boolean importFromDXF(String filePath, Pane targetPane, 
                                     CoordService coordService, StyleMenuContrl styleMenuContrl) {
        try {
            // Установка флага импорта, чтобы отключить автоматическое добавление стилей
            coordService.getCanvasController().setImportingDXF(true);
            
            // Загружаем DXF файл
            CadImage cadImage = (CadImage)Image.load(filePath);
            System.out.println("Файл DXF загружен: " + filePath);
            
            // Обходим все объекты в файле
            for (CadEntityBase entity : cadImage.getEntities()) {
                processEntity(entity, targetPane, coordService, styleMenuContrl);
            }
            
            System.out.println("Импорт DXF файла успешно завершен");
            return true;
        } catch (Exception e) {
            System.err.println("Ошибка при импорте DXF файла: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // В любом случае сбрасываем флаг импорта
            coordService.getCanvasController().setImportingDXF(false);
        }
    }
    
    /**
     * Обрабатывает сущность CAD файла
     */
    private static void processEntity(CadEntityBase entity, Pane targetPane, 
                                     CoordService coordService, StyleMenuContrl styleMenuContrl) {
        if (entity instanceof CadLine) {
            processLine((CadLine)entity, targetPane, coordService, styleMenuContrl);
        } else if (entity instanceof CadArc) {
            processArc((CadArc)entity, targetPane, coordService, styleMenuContrl);
        }else if (entity instanceof CadCircle) {
              processCircle((CadCircle)entity, targetPane, coordService, styleMenuContrl);
        }
        // Если требуется поддержка других типов сущностей (полилинии, тексты и т.д.), 
        // добавьте их обработку здесь
    }

    /**
     * Обрабатывает линию CAD
     */
    private static void processLine(CadLine line, Pane targetPane, CoordService coordService,
                                    StyleMenuContrl styleMenuContrl) {
        // Получаем координаты
        double startX = line.getFirstPoint().getX();
        double startY = line.getFirstPoint().getY();
        double endX = line.getSecondPoint().getX();
        double endY = line.getSecondPoint().getY();
        
        // В DXF ось Y направлена вверх, в JavaFX - вниз
        // Инвертируем Y координату ТОЛЬКО для реальных координат,
        // но не для экранных, так как coordService.toScreenY уже учитывает это
        double realStartY = -startY;
        double realEndY = -endY;
        
        System.out.println("Создаю линию: (" + startX + "," + realStartY + ") -> (" + endX + "," + realEndY + ")");
        
        // Создаем линию, НЕ инвертируя Y, так как coordService.toScreenY делает это автоматически
        MyLine myLine = new MyLine(
            coordService.toScreenX(startX),
            coordService.toScreenY(startY), // Не инвертируем, coordService.toScreenY сам учитывает направление оси
            coordService.toScreenX(endX),
            coordService.toScreenY(endY)    // Не инвертируем, coordService.toScreenY сам учитывает направление оси
        );
        
        // Устанавливаем цвет (конвертируем из индекса цвета AutoCAD)
        myLine.setColor(getColorByIndex(line.getColorId()));
        
        // Сохраняем реальные координаты (с инвертированной осью Y для совместимости)
        myLine.setRealX1(startX);
        myLine.setRealY1(realStartY); // Сохраняем инвертированную Y-координату как реальную
        myLine.setRealX2(endX);
        myLine.setRealY2(realEndY);   // Сохраняем инвертированную Y-координату как реальную
        
        // Добавляем на холст
        myLine.draw(targetPane);
        
        // Определяем тип линии
        double lineWeight = line.getLineWeight() > 0 ? line.getLineWeight() / 100.0 : 1.0;
        String lineTypeName = line.getLineTypeName() != null ? line.getLineTypeName() : "CONTINUOUS";
        String styleName = getStyleNameByLineType(lineTypeName);
        
        // Добавляем линию в соответствующий стиль
        styleMenuContrl.addObjectToStyle(styleName, myLine);
        
        // Принудительно обновляем стили для правильного отображения
        Map<String, List<Stylized>> objects = styleMenuContrl.getObjects();
        if (objects.containsKey(styleName)) {
            // Определяем параметры стиля на основе типа линии DXF
            double scale = coordService.getCoordSystem().getScale();
            List<Double> dashPattern = null;
            
            // Устанавливаем шаблон штрихов в зависимости от типа линии
            String upperLineType = lineTypeName.toUpperCase();
            if (upperLineType.equals("DASHED") || upperLineType.equals("DASH") || upperLineType.equals("DASHDASH")) {
                dashPattern = List.of(5.0, 5.0);
            } else if (upperLineType.equals("DASHDOT")) {
                dashPattern = List.of(5.0, 2.0, 1.0, 2.0);
            } else if (upperLineType.equals("DASHDOTDOT")) {
                dashPattern = List.of(5.0, 2.0, 1.0, 2.0, 1.0, 2.0);
            } else if (upperLineType.equals("DOTTED") || upperLineType.equals("DOT")) {
                dashPattern = List.of(1.0, 2.0);
            } else if (upperLineType.equals("CONTINUOUS")) {
                dashPattern = null;
            } else {
                // По умолчанию
                dashPattern = null;
            }
            
            // Применяем стиль напрямую к линии
            myLine.setupStyle(lineWeight, dashPattern, scale);
            System.out.println("Применен стиль: " + styleName + " с типом линии: " + lineTypeName);
        }
    }
    
    /**
     * Обрабатывает окружность CAD
     */
    private static void processCircle(CadCircle circle, Pane targetPane, CoordService coordService,
                                     StyleMenuContrl styleMenuContrl) {
        // Получаем координаты центра и радиус
        double centerX = circle.getCenterPoint().getX();
        double centerY = circle.getCenterPoint().getY();
        double radius = circle.getRadius();
        
        // В DXF ось Y направлена вверх, в JavaFX - вниз
        double realCenterY = -centerY;
        
        System.out.println("Создаю окружность: центр (" + centerX + "," + realCenterY + "), радиус " + radius);
        
        // Создаем окружность, используя экранные координаты
        MyCircle myCircle = new MyCircle(
            coordService.toScreenX(centerX),
            coordService.toScreenY(centerY), // Не инвертируем, coordService.toScreenY учитывает направление оси
            coordService.toScreenLength(radius)
        );
        
        // Устанавливаем цвет
        myCircle.setColor(getColorByIndex(circle.getColorId()));
        
        // Сохраняем реальные координаты
        myCircle.setRealCentX(centerX);
        myCircle.setRealCentY(realCenterY); // Сохраняем инвертированную Y-координату как реальную
        myCircle.setRealRadius(radius);
        
        // Добавляем на холст
        myCircle.draw(targetPane);
        
        // Определяем тип линии
        double lineWeight = circle.getLineWeight() > 0 ? circle.getLineWeight() / 100.0 : 1.0;
        String lineTypeName = circle.getLineTypeName() != null ? circle.getLineTypeName() : "CONTINUOUS";
        String styleName = getStyleNameByLineType(lineTypeName);
        
        // Добавляем окружность в соответствующий стиль
        styleMenuContrl.addObjectToStyle(styleName, myCircle);
        
        // Применяем стиль
        double scale = coordService.getCoordSystem().getScale();
        List<Double> dashPattern = null;
        
        // Устанавливаем шаблон штрихов в зависимости от типа линии
        String upperLineType = lineTypeName.toUpperCase();
        if (upperLineType.equals("DASHED") || upperLineType.equals("DASH") || upperLineType.equals("DASHDASH")) {
            dashPattern = List.of(5.0, 5.0);
        } else if (upperLineType.equals("DASHDOT")) {
            dashPattern = List.of(5.0, 2.0, 1.0, 2.0);
        } else if (upperLineType.equals("DASHDOTDOT")) {
            dashPattern = List.of(5.0, 2.0, 1.0, 2.0, 1.0, 2.0);
        } else if (upperLineType.equals("DOTTED") || upperLineType.equals("DOT")) {
            dashPattern = List.of(1.0, 2.0);
        } else if (upperLineType.equals("CONTINUOUS")) {
            dashPattern = null;
        } else {
            // По умолчанию
            dashPattern = null;
        }
        
        // Применяем стиль напрямую к окружности
        myCircle.setupStyle(lineWeight, dashPattern, scale);
    }
    
    /**
     * Обрабатывает дугу CAD
     */
    private static void processArc(CadArc arc, Pane targetPane, CoordService coordService,
                                  StyleMenuContrl styleMenuContrl) {
        // Получаем координаты центра, радиус и углы
        double centerX = arc.getCenterPoint().getX();
        double centerY = arc.getCenterPoint().getY();
        double radius = arc.getRadius();
        
        // В DXF ось Y направлена вверх, в JavaFX - вниз
        double realCenterY = -centerY;
        
        // Получаем углы из DXF (в радианах) и конвертируем в градусы
        double startAngle = Math.toDegrees(arc.getStartAngle());
        double endAngle = Math.toDegrees(arc.getEndAngle());
        
        System.out.println("Исходные углы дуги из DXF: " + startAngle + " - " + endAngle);
        
        // Поворачиваем против часовой стрелки на 90 градусов
        // Применяем поворот к начальному и конечному углам
        startAngle = (startAngle - 90) % 360;
        endAngle = (endAngle - 90) % 360;
        
        System.out.println("Углы после поворота на 90 градусов: " + startAngle + " - " + endAngle);
        
        // Вычисляем координаты начальной и конечной точек дуги
        double startX = centerX + radius * Math.cos(Math.toRadians(startAngle));
        double startY = centerY + radius * Math.sin(Math.toRadians(startAngle));
        double endX = centerX + radius * Math.cos(Math.toRadians(endAngle));
        double endY = centerY + radius * Math.sin(Math.toRadians(endAngle));
        
        // Инвертируем Y-координаты для реальных значений
        double realStartY = -startY;
        double realEndY = -endY;
        
        // Преобразуем координаты центра и точек в экранные
        double screenCenterX = coordService.toScreenX(centerX);
        double screenCenterY = coordService.toScreenY(centerY);
        double screenStartX = coordService.toScreenX(startX);
        double screenStartY = coordService.toScreenY(startY);
        double screenEndX = coordService.toScreenX(endX);
        double screenEndY = coordService.toScreenY(endY);
        
        // Создаем дугу, используя экранные координаты
        // И меняем порядок точек для корректного отображения
        MyArc myArc = new MyArc(
            screenEndX, screenEndY,
            screenStartX, screenStartY,
            screenCenterX, screenCenterY
        );
        
        // Вычисляем длину дуги в градусах
        double arcLength = endAngle - startAngle;
        if (arcLength < 0) {
            arcLength += 360;
        }
        
        System.out.println("Создаем дугу: центр (" + centerX + "," + realCenterY + 
                          "), радиус " + radius + ", длина дуги: " + arcLength);
        
        // Устанавливаем радиус и длину дуги
        myArc.setRealRadius(radius);
        myArc.setRealArcLength(arcLength);
        
        // Сохраняем реальные координаты
        List<Double> realCoordinates = new ArrayList<>();
        realCoordinates.add(centerX);
        realCoordinates.add(-realCenterY);
        realCoordinates.add(endX);      // Меняем порядок точек
        realCoordinates.add(-realEndY);
        realCoordinates.add(startX);
        realCoordinates.add(-realStartY);
        myArc.setRealCoordinates(realCoordinates);
        
        // Устанавливаем цвет
        myArc.setColor(getColorByIndex(arc.getColorId()));
        
        // Добавляем на холст
        myArc.draw(targetPane);
        
        // Настройка стиля
        double lineWeight = arc.getLineWeight() > 0 ? arc.getLineWeight() / 100.0 : 1.0;
        String lineTypeName = arc.getLineTypeName() != null ? arc.getLineTypeName() : "CONTINUOUS";
        String styleName = getStyleNameByLineType(lineTypeName);
        
        // Определяем шаблон штрихов в зависимости от типа линии
        String upperLineType = lineTypeName.toUpperCase();
        List<Double> dashPattern = null;
        
        if (upperLineType.equals("DASHED") || upperLineType.equals("DASH") || upperLineType.equals("DASHDASH")) {
            dashPattern = List.of(5.0, 5.0);
        } else if (upperLineType.equals("DASHDOT")) {
            dashPattern = List.of(5.0, 2.0, 1.0, 2.0);
        } else if (upperLineType.equals("DASHDOTDOT")) {
            dashPattern = List.of(5.0, 2.0, 1.0, 2.0, 1.0, 2.0);
        } else if (upperLineType.equals("DOTTED") || upperLineType.equals("DOT")) {
            dashPattern = List.of(1.0, 2.0);
        }
        
        // Применяем стиль к дуге
        myArc.setupStyle(lineWeight, dashPattern, coordService.getCoordSystem().getScale());
        
        // Добавляем дугу в соответствующий стиль
        styleMenuContrl.addObjectToStyle(styleName, myArc);
    }
    
    /**
     * Возвращает шаблон штрихов по имени типа линии
     */
    private static List<Double> getDashPatternByLineType(String lineTypeName) {
        if (lineTypeName == null) {
            return null;
        }
        
        String upperLineType = lineTypeName.toUpperCase();
        if (upperLineType.equals("DASHED") || upperLineType.equals("DASH") || upperLineType.equals("DASHDASH")) {
            return List.of(5.0, 5.0);
        } else if (upperLineType.equals("DASHDOT")) {
            return List.of(5.0, 2.0, 1.0, 2.0);
        } else if (upperLineType.equals("DASHDOTDOT")) {
            return List.of(5.0, 2.0, 1.0, 2.0, 1.0, 2.0);
        } else if (upperLineType.equals("DOTTED") || upperLineType.equals("DOT")) {
            return List.of(1.0, 2.0);
        } else {
            return null; // CONTINUOUS или другие
        }
    }
    
    /**
     * Получает цвет JavaFX по индексу цвета AutoCAD
     */
    private static Color getColorByIndex(int colorIndex) {
        // Стандартные цвета AutoCAD по индексу
        switch (colorIndex) {
            case 1: return Color.RED;
            case 2: return Color.YELLOW;
            case 3: return Color.GREEN;
            case 4: return Color.CYAN;
            case 5: return Color.BLUE;
            case 6: return Color.MAGENTA;
            case 7: return Color.WHITE;
            case 8: return Color.DARKGRAY;
            case 9: return Color.LIGHTGRAY;
            default: return Color.BLACK; // 0 или другие значения
        }
    }
    
    /**
     * Определяет название стиля на основе типа линии DXF
     */
    private static String getStyleNameByLineType(String lineTypeName) {
        if (lineTypeName == null) {
            return "Основная тонкая";
        }
        
        String upperLineType = lineTypeName.toUpperCase();
        if (upperLineType.equals("DASHED") || upperLineType.equals("DASH") || upperLineType.equals("DASHDASH")) {
            return "Штриховая";
        } else if (upperLineType.equals("DASHDOT")) {
            return "Штрих-пунктирная";
        } else if (upperLineType.equals("DASHDOTDOT")) {
            return "Штрих-пунктирная с двумя точками";
        } else if (upperLineType.equals("CONTINUOUS")) {
            return "Основная тонкая";
        } else {
            return "Основная тонкая";
        }
    }
}