package domain;

/**
 * Clase abstracta para las entidades que pueden moverse dentro del juego.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public abstract class Alive extends Element implements Movement {
    public static final double BASE_SPEED = 0.11; // Ajustado a velocidad de tick por cuadro

    protected double speed;
    protected char direction;

    public Alive(double positionX, double positionY, double width, double height, double speed, char direction) {
        super(positionX, positionY, width, height);
        this.speed = speed;
        this.direction = direction;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public char getDirection() {
        return direction;
    }

    public void setDirection(char direction) {
        this.direction = direction;
    }

    @Override
    public void updatePosition(double newPositionX, double newPositionY) {
        this.positionX = newPositionX;
        this.positionY = newPositionY;
    }
}
