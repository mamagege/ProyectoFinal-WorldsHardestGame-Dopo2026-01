package domain;

/**
 * Clase abstracta base para todos los elementos que existen en el tablero del juego.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public abstract class ElementoTablero {
    protected Posicion posicion;

    public ElementoTablero(Posicion posicion) {
        this.posicion = posicion;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }
}
