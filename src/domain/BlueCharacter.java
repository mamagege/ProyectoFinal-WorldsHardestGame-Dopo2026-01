package domain;

/**
 * Variante azul del personaje. Tamaño 1.5x y velocidad 1.5x.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class BlueCharacter extends Character {
    public BlueCharacter(double positionX, double positionY) {
        super(positionX, positionY, 0.9375, 0.9375, 1.15 * BASE_SPEED);
    }
}
