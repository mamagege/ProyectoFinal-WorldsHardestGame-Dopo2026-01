package domain;

/**
 * Representa un obstáculo o enemigo móvil dentro del juego.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class Obstacle extends Alive {
    private boolean isHorizontal;

    public Obstacle(int positionX, int positionY, int width, int height, int speed, char initialDirection, boolean isHorizontal) {
        super(positionX, positionY, width, height, speed, initialDirection);
        this.isHorizontal = isHorizontal;
    }

    public boolean isHorizontal() { return isHorizontal; }

    @Override
    public void move(char direction) {
        this.direction = direction;
        updatePosition();
    }

    @Override
    public void updatePosition() {
        if (isHorizontal) {
            positionX += (direction == 'R' ? speed : -speed);
        } else {
            positionY += (direction == 'D' ? speed : -speed);
        }
    }
    
    public void bounce() {
        if (isHorizontal) {
            direction = (direction == 'R') ? 'L' : 'R';
        } else {
            direction = (direction == 'D') ? 'U' : 'D';
        }
    }
}
