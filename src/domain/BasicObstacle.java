package domain;

/**
 * Obstáculo básico, movimiento rectilíneo y velocidad 1.0x.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class BasicObstacle extends Obstacle {
    private boolean isHorizontal;

    public BasicObstacle(double positionX, double positionY, char direction, boolean isHorizontal) {
        super(positionX, positionY, 1.0, 1.0, 1.0 * BASE_SPEED, direction);
        this.isHorizontal = isHorizontal;
    }

    public boolean isHorizontal() { return isHorizontal; }

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
