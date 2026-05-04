package domain;

/**
 * Representa un obstáculo o enemigo móvil dentro del juego.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public abstract class Obstacle extends Alive {
    public Obstacle(double positionX, double positionY, double width, double height, double speed, char direction) {
        super(positionX, positionY, width, height, speed, direction);
    }

    @Override
    public void move(char direction) {
        this.direction = direction;
    }

    @Override
    public boolean isCircular() {
        return true;
    }
}
