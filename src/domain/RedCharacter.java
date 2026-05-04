package domain;

/**
 * Variante roja del personaje. Tamaño normal y velocidad normal.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class RedCharacter extends Character {
    public RedCharacter(double positionX, double positionY) {
        super(positionX, positionY, 1.0, 1.0, 1.0 * BASE_SPEED);
    }
}
