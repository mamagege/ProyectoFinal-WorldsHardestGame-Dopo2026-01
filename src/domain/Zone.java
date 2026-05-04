package domain;

/**
 * Clase base abstracta para las distintas zonas seguras.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public abstract class Zone extends Element {
    public Zone(double positionX, double positionY, double width, double height) {
        super(positionX, positionY, width, height);
    }
}
