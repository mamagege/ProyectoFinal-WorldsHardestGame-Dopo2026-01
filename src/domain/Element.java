package domain;

/**
 * Clase base abstracta para todos los elementos del juego.
 * Centraliza las coordenadas y dimensiones de cada objeto.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public abstract class Element {
    protected double positionX;
    protected double positionY;
    protected double width;
    protected double height;

    public Element(double positionX, double positionY, double width, double height) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
    }

    public double getPositionX() { return positionX; }
    public void setPositionX(double positionX) { this.positionX = positionX; }
    public double getPositionY() { return positionY; }
    public void setPositionY(double positionY) { this.positionY = positionY; }
    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
}
