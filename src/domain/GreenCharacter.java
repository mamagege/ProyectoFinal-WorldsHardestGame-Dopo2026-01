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
        super(positionX, positionY, 0.9375, 0.9375, 0.40 * BASE_SPEED);
        this.hasArmor = true;
    }

    @Override
    public void removeArmor() {
        super.removeArmor();
        // Al recibir daño: más grande y más lento
        this.width = 1.125;
        this.height = 1.125;
        this.speed = 0.25 * BASE_SPEED;
    }
}
