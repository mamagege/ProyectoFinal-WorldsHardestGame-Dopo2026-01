package domain;

/**
 * Obstáculo que sigue una lista de puntos de paso (waypoints) en orden,
 * moviéndose SOLO horizontal o verticalmente entre ellos (sin diagonales).
 * Ideal para recorridos anti-horarios o en forma de U sobre el mapa.
 *
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class WaypointObstacle extends Obstacle {

    private final double[][] waypoints; // [ [x0,y0], [x1,y1], ... ]
    private int currentTarget;

    /**
     * @param waypoints Matriz Nx2 con las coordenadas lógicas de cada punto de paso.
     *                  El obstáculo comienza en waypoints[0] y va avanzando.
     */
    public WaypointObstacle(double[][] waypoints) {
        super(waypoints[0][0], waypoints[0][1], 0.75, 0.75, 1.0 * BASE_SPEED, 'N');
        this.waypoints = waypoints;
        this.currentTarget = 1 % waypoints.length; // Primer destino
    }

    @Override
    public void updatePosition() {
        if (waypoints == null || waypoints.length < 2) return;

        double targetX = waypoints[currentTarget][0];
        double targetY = waypoints[currentTarget][1];

        double dx = targetX - positionX;
        double dy = targetY - positionY;

        // Movimiento ESTRICTAMENTE horizontal o vertical, nunca diagonal.
        // Si el destino comparte la X actual, moverse solo en Y (y viceversa).
        if (Math.abs(dx) >= Math.abs(dy)) {
            // Movimiento horizontal
            if (Math.abs(dx) > speed * 0.5) {
                positionX += Math.signum(dx) * speed;
            } else {
                positionX = targetX; // Snap al punto de paso
                positionY = targetY;
                currentTarget = (currentTarget + 1) % waypoints.length;
            }
        } else {
            // Movimiento vertical
            if (Math.abs(dy) > speed * 0.5) {
                positionY += Math.signum(dy) * speed;
            } else {
                positionX = targetX; // Snap al punto de paso
                positionY = targetY;
                currentTarget = (currentTarget + 1) % waypoints.length;
            }
        }
    }
}
