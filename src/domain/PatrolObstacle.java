package domain;

/**
 * Obstáculo que se mueve en círculos usando seno y coseno.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class PatrolObstacle extends Obstacle {
    private double originX;
    private double originY;
    private double radius;
    private double angle;

    public PatrolObstacle(double originX, double originY, double radius) {
        super(originX + radius, originY, 1.0, 1.0, 1.0 * BASE_SPEED, 'N');
        this.originX = originX;
        this.originY = originY;
        this.radius = radius;
        this.angle = 0;
    }

    @Override
    public void updatePosition() {
        // Incrementa el ángulo basado en la velocidad
        // Dividimos entre 20 para hacer el giro suave
        angle += (speed / 20.0);
        if (angle >= Math.PI * 2) {
            angle -= Math.PI * 2;
        }

        positionX = originX + Math.cos(angle) * radius;
        positionY = originY + Math.sin(angle) * radius;
    }
}
