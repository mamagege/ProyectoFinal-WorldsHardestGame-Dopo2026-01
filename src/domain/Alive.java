package domain;

/**
 * Clase abstracta para las entidades que pueden moverse dentro del juego.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public abstract class Alive extends Element implements Movement {
    protected int speed;
    protected char direction;

    public Alive(int positionX, int positionY, int width, int height, int speed, char direction) {
        super(positionX, positionY, width, height);
        this.speed = speed;
        this.direction = direction;
    }

    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }
    public char getDirection() { return direction; }
    public void setDirection(char direction) { this.direction = direction; }

    @Override
    public void updatePosition(int newPositionX, int newPositionY) {
        this.positionX = newPositionX;
        this.positionY = newPositionY;
    }
}
