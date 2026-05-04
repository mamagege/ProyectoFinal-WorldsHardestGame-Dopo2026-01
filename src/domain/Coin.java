package domain;

/**
 * Representa una moneda que el personaje debe recolectar.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class Coin extends Element {
    private boolean collected;

    public Coin(int positionX, int positionY, int width, int height) {
        super(positionX, positionY, width, height);
        this.collected = false;
    }

    public boolean isCollected() { return collected; }
    public void collect() { this.collected = true; }
}
