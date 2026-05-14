package domain;

/**
 * Variante blanca del personaje para el nivel de selección.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class WhiteCharacter extends Character {
    public WhiteCharacter(double positionX, double positionY) {
        super(positionX, positionY, 1.125, 1.125, 1.5 * BASE_SPEED);
    }
}
