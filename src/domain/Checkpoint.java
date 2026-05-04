package domain;

/**
 * Representa una zona segura inicial o un punto de control intermedio.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class Checkpoint extends Zone {
    public Checkpoint(double positionX, double positionY, double width, double height) {
        super(positionX, positionY, width, height);
    }
}
