package ru.ruslan.spring.cad.Savers;

import com.jsevy.jdxf.DXFDocument;
import com.jsevy.jdxf.DXFGraphics;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import java.awt.BasicStroke;
import ru.ruslan.spring.cad.Models.*;
import ru.ruslan.spring.cad.Services.CoordService;
import ru.ruslan.spring.cad.Interfaces.Stylized;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.awt.Color;

public class DXFExporter {

    public static void exportToDXF(Pane node, String filePath) {
        try {
            // Создаем DXF документ
            DXFDocument dxfDoc = new DXFDocument("JavaFX Export");
            DXFGraphics dxfGraphics = dxfDoc.getGraphics();

            // Обрабатываем все объекты
            for (Node obj : node.getChildren()) {
                // Преобразуем JavaFX ноду в DXF
                convertNodeToDXF(obj, dxfGraphics);
            }

            String stringOutput = dxfDoc.toDXFString();

            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(stringOutput);
            fileWriter.flush();
            fileWriter.close();

            System.out.println("DXF файл успешно создан: " + filePath);

        } catch (IOException e) {
            System.err.println("Ошибка при экспорте в DXF: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Непредвиденная ошибка при экспорте в DXF: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void convertNodeToDXF(javafx.scene.Node node, DXFGraphics dxfGraphics) {
        try {
            // Сохраняем текущие параметры отрисовки
            java.awt.Stroke originalStroke = dxfGraphics.getStroke();
            Color originalColor = dxfGraphics.getColor();
            
            // Применяем стиль линии для Figure объектов
            if (node instanceof Figure) {
                Figure figure = (Figure) node;
                
                // Устанавливаем цвет (по умолчанию черный)
                Color awtColor = Color.BLACK;
                if (figure.getColor() != null) {
                    javafx.scene.paint.Color fxColor = figure.getColor();
                    awtColor = new Color(
                            (float) fxColor.getRed(),
                            (float) fxColor.getGreen(),
                            (float) fxColor.getBlue(),
                            (float) fxColor.getOpacity()
                    );
                }
                dxfGraphics.setColor(awtColor);
                
                // Устанавливаем параметры линии
                float strokeWidth = (float) figure.getWidth();
                float[] dashPattern = null;
                
                // Если объект реализует Stylized, получаем параметры штрихов
                if (node instanceof Stylized) {
                    // Проверяем типы объектов и получаем их штрихи
                    if (node instanceof MyLine) {
                        MyLine line = (MyLine) node;
                        javafx.scene.shape.Line javaFxLine = null;
                        
                        // Находим Line в дочерних элементах
                        for (Node child : line.getChildren()) {
                            if (child instanceof javafx.scene.shape.Line) {
                                javaFxLine = (javafx.scene.shape.Line) child;
                                System.out.println(javaFxLine);
                                break;
                            }
                        }
                        
                        if (javaFxLine != null && !javaFxLine.getStrokeDashArray().isEmpty()) {
                            dashPattern = new float[javaFxLine.getStrokeDashArray().size()];
                            for (int i = 0; i < dashPattern.length; i++) {
                                dashPattern[i] = javaFxLine.getStrokeDashArray().get(i).floatValue();
                                System.out.println(dashPattern[i]);
                            }
                        }
                    } 
                    else if (node instanceof MyCircle) {
                        MyCircle myCircle = (MyCircle) node;
                        javafx.scene.shape.Circle javaFxCircle = null;
                        
                        // Находим Circle в дочерних элементах
                        for (Node child : myCircle.getChildren()) {
                            if (child instanceof javafx.scene.shape.Circle) {
                                javaFxCircle = (javafx.scene.shape.Circle) child;
                                break;
                            }
                        }
                        
                        if (javaFxCircle != null && !javaFxCircle.getStrokeDashArray().isEmpty()) {
                            dashPattern = new float[javaFxCircle.getStrokeDashArray().size()];
                            for (int i = 0; i < dashPattern.length; i++) {
                                dashPattern[i] = javaFxCircle.getStrokeDashArray().get(i).floatValue();
                            }
                        }
                    }
                    else if (node instanceof MyArc) {
                        MyArc myArc = (MyArc) node;
                        javafx.scene.shape.Arc javaFxArc = myArc.getArc();
                        
                        if (javaFxArc != null && !javaFxArc.getStrokeDashArray().isEmpty()) {
                            dashPattern = new float[javaFxArc.getStrokeDashArray().size()];
                            for (int i = 0; i < dashPattern.length; i++) {
                                dashPattern[i] = javaFxArc.getStrokeDashArray().get(i).floatValue();
                            }
                        }
                        // Устанавливаем толщину линии для дуги
                        strokeWidth = (float) javaFxArc.getStrokeWidth(); 
                        System.out.println("Толщина дуги: " + strokeWidth);
                    }
                    else if (node instanceof Rectangle) {
                        Rectangle rect = (Rectangle) node;
                        // Для прямоугольника ищем первую линию и берем ее параметры
                        MyLine line = null;
                        for (Node child : rect.getChildren()) {
                            if (child instanceof MyLine) {
                                line = (MyLine) child;
                                break;
                            }
                        }
                        
                        if (line != null) {
                            javafx.scene.shape.Line javaFxLine = null;
                            for (Node lineChild : line.getChildren()) {
                                if (lineChild instanceof javafx.scene.shape.Line) {
                                    javaFxLine = (javafx.scene.shape.Line) lineChild;
                                    break;
                                }
                            }
                            
                            if (javaFxLine != null) {
                                // Устанавливаем толщину линии
                                strokeWidth = (float) javaFxLine.getStrokeWidth();
                                
                                // Получаем параметры штриховки
                                if (!javaFxLine.getStrokeDashArray().isEmpty()) {
                                    dashPattern = new float[javaFxLine.getStrokeDashArray().size()];
                                    for (int i = 0; i < dashPattern.length; i++) {
                                        dashPattern[i] = javaFxLine.getStrokeDashArray().get(i).floatValue();
                                    }
                                }
                            }
                        }
                    }
                    else if (node instanceof Polygon) {
                        Polygon polygon = (Polygon) node;
                        // Получаем Group с линиями
                        Group linesGroup = polygon.getLines();
                        
                        if (linesGroup != null && !linesGroup.getChildren().isEmpty()) {
                            // Берем первую линию из полигона
                            Node firstChild = linesGroup.getChildren().get(0);
                            if (firstChild instanceof MyLine) {
                                MyLine line = (MyLine) firstChild;
                                
                                // Находим JavaFX Line внутри MyLine
                                javafx.scene.shape.Line javaFxLine = null;
                                for (Node lineChild : line.getChildren()) {
                                    if (lineChild instanceof javafx.scene.shape.Line) {
                                        javaFxLine = (javafx.scene.shape.Line) lineChild;
                                        break;
                                    }
                                }
                                
                                if (javaFxLine != null) {
                                    // Устанавливаем толщину линии
                                    strokeWidth = (float) javaFxLine.getStrokeWidth();
                                    System.out.println("Толщина полигона: " + strokeWidth);
                                    
                                    // Получаем параметры штриховки
                                    if (!javaFxLine.getStrokeDashArray().isEmpty()) {
                                        dashPattern = new float[javaFxLine.getStrokeDashArray().size()];
                                        for (int i = 0; i < dashPattern.length; i++) {
                                            dashPattern[i] = javaFxLine.getStrokeDashArray().get(i).floatValue();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Создаем и устанавливаем BasicStroke
                if (dashPattern != null) {
                    BasicStroke stroke = new BasicStroke(
                        strokeWidth,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER,
                        10.0f,
                        dashPattern,
                        0.0f
                    );
                    System.out.println("basicStroke");
                    dxfGraphics.setStroke(stroke);
                } else {
                    BasicStroke stroke = new BasicStroke(strokeWidth);
                    dxfGraphics.setStroke(stroke);
                }
            }
            
            // Экспортируем объект в зависимости от его типа
            if (node instanceof MyLine) {
                exportMyLine((MyLine) node, dxfGraphics);
            } else if (node instanceof MyCircle) {
                exportMyCircle((MyCircle) node, dxfGraphics);
            } else if (node instanceof Rectangle) {
                exportRectangle((Rectangle) node, dxfGraphics);
            } else if (node instanceof Polygon) {
                exportPolygon((Polygon) node, dxfGraphics);
            } else if (node instanceof MyArc) {
                exportMyArc((MyArc) node, dxfGraphics);
            } else if (node instanceof MySpline) {
                //exportMySpline((MySpline) node, dxfGraphics);
            } else if (node instanceof LineForSpline) {
                //exportLineForSpline((LineForSpline) node, dxfGraphics);
            } else if (node instanceof Group) {
                exportGroup((Group) node, dxfGraphics);
            } else if (node instanceof Pane) {
                exportPane((Pane) node, dxfGraphics);
            }
            
            // Восстанавливаем исходные параметры отрисовки
            dxfGraphics.setStroke(originalStroke);
            dxfGraphics.setColor(originalColor);
        } catch (Exception e) {
            System.err.println("Ошибка при экспорте объекта " + node.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void exportMyLine(MyLine line, DXFGraphics dxfGraphics) {
        dxfGraphics.drawLine(
                line.getRealX1(), -line.getRealY1(),
                line.getRealX2(), -line.getRealY2()
        );
    }
    
    private static void exportMyCircle(MyCircle circle, DXFGraphics dxfGraphics) {
        dxfGraphics.drawOval(
                (circle.getRealCentX() - circle.getRealRadius()),
                ((-circle.getRealCentY() - circle.getRealRadius())),
                (circle.getRealRadius() * 2),
                (circle.getRealRadius() * 2)
        );
    }
    
    private static void exportRectangle(Rectangle rect, DXFGraphics dxfGraphics) {
        List<Double> coord = rect.getRealCoordinates();
        for (int i = 0; i < coord.size(); i += 2) {
            dxfGraphics.drawLine(
                    coord.get(i), -coord.get(i + 1),
                    coord.get((i + 2) % coord.size()), -coord.get((i + 3) % coord.size())
            );
        }
    }
    
    private static void exportPolygon(Polygon polygon, DXFGraphics dxfGraphics) {
        List<Double> coord = polygon.getRealCoordinates();

        int nPoints = coord.size() / 2;
        double[] xPoints = new double[nPoints];
        double[] yPoints = new double[nPoints];
        for (int i = 0, j=0; i < coord.size(); i += 2, j++) {
            xPoints[j] = coord.get(i);
            yPoints[j] = -coord.get(i + 1);
        }

        dxfGraphics.drawPolygon(xPoints, yPoints, nPoints);
    }
    
    private static void exportMyArc(MyArc arc, DXFGraphics dxfGraphics) {
        // Получаем РЕАЛЬНЫЕ координаты объекта из сохраненных данных
        List<Double> realCoords = arc.getRealCoordinates();
        
        if (realCoords != null && realCoords.size() >= 6) {
            // Получаем координаты центра, начальной и конечной точек
            // В реальных координатах порядок: 
            // [центр_X, центр_Y, начальная_точка_X, начальная_точка_Y, конечная_точка_X, конечная_точка_Y]
            double centerX = realCoords.get(0);
            double centerY = realCoords.get(1);
            double startX = realCoords.get(2);
            double startY = realCoords.get(3);
            double endX = realCoords.get(4);
            double endY = realCoords.get(5);
            
            // Получаем радиус дуги из сохраненного значения
            double radius = arc.getRealRadius();
            
            // Получаем длину дуги из сохраненного свойства
            double arcLength = arc.getRealArcLength();
            
            // Вычисляем начальный угол дуги относительно центра
            double startAngle = Math.toDegrees(Math.atan2(startY - centerY, startX - centerX));
            
            // Нормализуем угол в диапазоне [0, 360)
            startAngle = (startAngle + 360) % 360;
            
            // Определяем направление дуги из сохраненного объекта Arc
            javafx.scene.shape.Arc javafxArc = arc.getArc();
            boolean isNegativeDirection = javafxArc.getLength() < 0;
            
            // Если дуга идет в отрицательном направлении, инвертируем длину
            double dxfArcLength = arcLength;
            if (isNegativeDirection) {
                dxfArcLength = -arcLength;
            }
            
            // Рисуем дугу в DXF
            // В DXF ось Y направлена вверх, поэтому инвертируем Y-координаты
            dxfGraphics.drawArc(
                centerX - radius, -centerY - radius, 
                radius * 2, radius * 2,
                startAngle, dxfArcLength
            );
        } else {
            // Если реальные координаты не доступны, выводим сообщение об ошибке
            System.err.println("Ошибка: не удалось получить реальные координаты для дуги");
        }
    }
    
    private static void exportGroup(Group group, DXFGraphics dxfGraphics) {
        for (Node child : group.getChildren()) {
            convertNodeToDXF(child, dxfGraphics);
        }
    }
    
    private static void exportPane(Pane pane, DXFGraphics dxfGraphics) {
        for (Node child : pane.getChildren()) {
            convertNodeToDXF(child, dxfGraphics);
        }
    }
}
