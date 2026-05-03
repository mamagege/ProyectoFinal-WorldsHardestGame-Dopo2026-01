package domain;

/**
 * Representa el "Cuadrado Rojo" controlado por el usuario.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class Jugador extends ElementoTablero {
    private int muertes;
    private final int velocidad;
    private final int tamano;

    public Jugador(Posicion posicion) {
        super(posicion);
        this.muertes = 0;
        this.velocidad = 1; // Velocidad de 1x
        this.tamano = 1;    // Tamaño de 1x
    }

    public int getMuertes() {
        return muertes;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public int getTamano() {
        return tamano;
    }

    /**
     * Incrementa el contador de muertes del jugador.
     */
    public void registrarMuerte() {
        this.muertes++;
    }
}
