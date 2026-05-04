package domain;

/**
 * Clase base abstracta para todos los elementos del juego.
 * Centraliza las coordenadas y dimensiones de cada objeto.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public abstract class Element {
    protected int positionX;
    protected int positionY;
    protected int width;
    protected int height;

    public Element(int positionX, int positionY, int width, int height) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
    }

    public int getPositionX() { return positionX; }
    public void setPositionX(int positionX) { this.positionX = positionX; }
    public int getPositionY() { return positionY; }
    public void setPositionY(int positionY) { this.positionY = positionY; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
}
