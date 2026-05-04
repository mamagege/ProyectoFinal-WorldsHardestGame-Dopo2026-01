package domain;

/**
 * Representa una zona segura inicial o un punto de control intermedio.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class Checkpoint extends Zone {
    public Checkpoint(int positionX, int positionY, int width, int height) {
        super(positionX, positionY, width, height);
    }
}
