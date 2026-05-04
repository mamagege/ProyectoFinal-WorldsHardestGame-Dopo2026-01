package domain;

/**
 * Interfaz que define las capacidades de movimiento para las entidades vivas.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public interface Movement {
    void move(char direction);
    void updatePosition();
    void updatePosition(int newPositionX, int newPositionY);
}
