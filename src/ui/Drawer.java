package ui;

import Jama.Matrix;
import model.Lab2Model;
import org.eclipse.swt.graphics.*;

public class Drawer {
    private Window window;
    private GC gc;

    public Drawer(Window window, GC gc) {
        this.window = window;
        this.gc = gc;
    }


    public void runLab2(Lab2Model lab2) {
        lab2.run();
        drawFilledCircles(lab2.getPoints(), 2, new Color(window.getDisplay(),new RGB(0,0,0)));
        drawPolyline(lab2.getOutPoints());//результат
        Matrix p = lab2.getPoints();
        Matrix t = lab2.getTangents();
        Color foregroundOld = gc.getForeground();
        gc.setForeground(new Color(window.getDisplay(), new RGB(72, 61, 139)));
        for (int i = 0; i < t.getRowDimension(); i++){
            gc.drawLine((int)p.get(i, 0), (int)p.get(i, 1), (int)(p.get(i, 0) + t.get(i, 0)), (int)(p.get(i, 1) + t.get(i, 1)));
        }
        gc.setForeground(foregroundOld);
    }

    /**
     * Рисует многоугольник, описываемый матрицей вида
     *|x1 y1 ...|  (точка 1)
     *|x2 y2 ...|  (точка 2)
     */
    private void drawPolygon(Matrix m){
        if (m == null || m.getColumnDimension() < 2){
            System.out.println("Wrong matrix when drawing the polygon");
            return;
        }
        int[] coordArray = new int[m.getRowDimension()*2];//an array with the coordinates (x1 y1 x2 y2 x3 y3...)
        for (int i = 0; i < m.getRowDimension(); i++ ){
            for (int j = 0; j < 2; j++){
                coordArray[i*2 + j] = (int) m.get(i, j);
            }
        }
        gc.drawPolygon(coordArray);
    }

    /**
     * Рисует ломаную, описываемую матрицей вида
     *|x1 y1 ...|  (точка 1)
     *|x2 y2 ...|  (точка 2)
     */
    private void drawPolyline(Matrix m){
        if (m == null || m.getColumnDimension() < 2){
            System.out.println("Отрисовка ломаной: неправильно заданная матрица!");
            return;
        }
        int[] coordArray = new int[m.getRowDimension()*2];
        for (int i = 0; i < m.getRowDimension(); i++ ){
            for (int j = 0; j < 2; j++){
                coordArray[i*2 + j] = (int) m.get(i, j);
            }
        }
        gc.drawPolyline(coordArray);
    }

    /*
     * Рисует линии из 2 точек, описываемые матрицей вида
     *|x1 y1 ...|  (точка 1) линия 1
     *|x2 y2 ...|  (точка 2) линия 1
     *|x3 y3 ...|  (точка 1) линия 2
     *|x4 y4 ...|  (точка 2) линия 2
     */
    private void draw2PointLines(Matrix m){
        if (m == null || m.getColumnDimension() < 2 || (m.getRowDimension() & 0x1) != 0){
            System.out.println("Отрисовка линий из 2 точек: неправильно заданная матрица!");
            return;
        }
//		int[] coordArray = new int[m.getRowDimension()*2];//������ � ������������ (x1 y1 x2 y2 x3 y3...)
        for (int i = 0; i < m.getRowDimension(); i+=2 ){
            gc.drawLine((int)m.get(i,0), (int)m.get(i,1), (int)m.get(i + 1, 0), (int)m.get(i + 1, 1));
        }
    }

    /*
     * Рисует точки, описываемые матрицей вида
     *|x1 y1 ...|  (точка 1)
     *|x2 y2 ...|  (точка 2)
     *...
     */
    private void drawFilledCircles(Matrix m, int circleRadius, Color color){
        if (m == null || m.getColumnDimension() < 2){
            System.out.println("Отрисовка точек: неправильно заданная матрица!");
            return;
        }
        Color back = gc.getBackground();
        gc.setBackground(color);
        for (int i = 0; i < m.getRowDimension(); i++ ){
            gc.fillOval((int) m.get(i, 0) - circleRadius, (int) m.get(i, 1) - circleRadius, 2*circleRadius, 2*circleRadius);
        }
        gc.setBackground(back);
        for (int i = 0; i < m.getRowDimension(); i++ ){
            gc.drawText(Integer.toString(i), (int) m.get(i, 0) - 2*circleRadius, (int) m.get(i, 1), true);
        }
    }

    private void drawPoint(Point point){
        gc.drawPoint(point.x, point.y);
        gc.drawOval(point.x -2, point.y -2, 4, 4);
    }

    public void drawCoordinatesGrid(){
        int step = 50;
        int sizeX = window.getSize().x;
        int sizeY = window.getSize().y;
        int i;
        int displacement = 0; //shift this many displacement pixels lower and to the right
        for (i = displacement; i < sizeX; i+= step){
            gc.drawText(((Integer)(i - displacement)).toString(), i, displacement); //integer - строка перевода в разные типы
        }
        for (i = displacement; i < window.getSize().y; i+= step){
            gc.drawText(((Integer)(i - displacement)).toString(), displacement, i); //integer - строка перевода в разные типы
        }
        //gray
        Color foregroundOld = gc.getForeground();
//        java.awt.Color color = new java.awt.Color(142, 145, 180); //I used this to comfortably choose color in idea
        gc.setForeground(new Color(window.getDisplay(), new RGB(142, 145, 180)));
        for (i = displacement; i < sizeX; i+= step){
            gc.drawLine(i, displacement, i, sizeY);
        }
        for (i = displacement; i < window.getSize().y; i+= step){
            gc.drawLine(displacement, i, sizeX, i);
        }
        gc.setForeground(foregroundOld);
    }

    void clear() {
        Color foregroundOld = gc.getForeground();
        gc.setForeground(gc.getBackground());
        gc.fillRectangle(0, 0, 6000, 6000);
        gc.setForeground(foregroundOld);
        drawCoordinatesGrid();
    }

    void drawDefaultTriangle(Matrix m) {
        Color foregroundOld = gc.getForeground();
//                java.awt.Color color = new java.awt.Color(0, 128, 0); //I used this to comfortably choose color in idea
        gc.setForeground(new Color(window.getDisplay(), new RGB(0, 90, 0)));
        drawPolygon(m);
        gc.setForeground(foregroundOld);
    }

    void drawPreviousTriangle(Matrix m) {
        Color foregroundOld = gc.getForeground();
        gc.setForeground(new Color(window.getDisplay(), new RGB(72, 61, 139)));
        drawPolygon(m);
        gc.setForeground(foregroundOld);
    }

    void drawCurrentTriangle(Matrix m) {
        drawPolygon(m);
    }

    Window getWindow() {
        return window;
    }
}
