package domain;

/**
 * Representa una moneda amarilla que el jugador debe recolectar.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class Moneda extends ElementoTablero {
    private boolean recolectada;

    public Moneda(Posicion posicion) {
        super(posicion);
        this.recolectada = false;
    }

    public boolean isRecolectada() {
        return recolectada;
    }

    /**
     * Marca la moneda como recolectada.
     */
    public void recolectar() {
        this.recolectada = true;
    }
}
