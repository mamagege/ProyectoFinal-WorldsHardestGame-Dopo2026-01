package domain;

/**
 * Variante verde del personaje. Armadura inicial y velocidad base.
 * Si recibe daño, pierde la armadura y reduce su velocidad.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class GreenCharacter extends Character {
    public GreenCharacter(double positionX, double positionY) {
        super(positionX, positionY, 0.75, 0.75, 1.0 * BASE_SPEED);
        this.hasArmor = true;
    }

    @Override
    public void removeArmor() {
        super.removeArmor();
        this.speed = 0.7 * BASE_SPEED;
    }
}
