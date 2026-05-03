package domain;

import java.util.List;

/**
 * El Gestor/Fachada del juego.
 * Contiene y coordina todos los elementos del nivel.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class JuegoDOPO {
    private Jugador jugador;
    private List<EnemigoBasico> enemigos;
    private List<Moneda> monedas;
    private List<Pared> paredes;
    private List<ZonaSegura> zonasSeguras;
    private boolean nivelCompletado;

    public JuegoDOPO(Jugador jugador, List<EnemigoBasico> enemigos, List<Moneda> monedas, List<Pared> paredes,
            List<ZonaSegura> zonasSeguras) {
        this.jugador = jugador;
        this.enemigos = enemigos;
        this.monedas = monedas;
        this.paredes = paredes;
        this.zonasSeguras = zonasSeguras;
        this.nivelCompletado = false;
    }

    /**
     * Mueve el jugador a la nueva posición dada.
     * Valida que la nueva posición no sea una pared y luego verifica colisiones.
     * 
     * @param nuevaPosicion La posición a la que se desea mover el jugador.
     */
    public void moverJugador(Posicion nuevaPosicion) {
        if (nivelCompletado) {
            return; // El juego ha terminado
        }

        if (!esPared(nuevaPosicion)) {
            jugador.setPosicion(nuevaPosicion);
            verificarColisiones();
        }
    }

    /**
     * Verifica si una posición dada está ocupada por una pared.
     */
    private boolean esPared(Posicion pos) {
        for (Pared pared : paredes) {
            if (pared.getPosicion().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Lógica principal de colisiones (Reglas del juego).
     */
    private void verificarColisiones() {
        // 1. Si el jugador toca un EnemigoBasico, suma una muerte y reinicia
        for (EnemigoBasico enemigo : enemigos) {
            if (enemigo.getPosicion().equals(jugador.getPosicion())) {
                jugador.registrarMuerte();
                reiniciarJugador();
                return; // Corta la ejecución para no recolectar monedas en este mismo turno
            }
        }

        // 2. Si el jugador toca una Moneda, la recolecta
        for (Moneda moneda : monedas) {
            if (!moneda.isRecolectada() && moneda.getPosicion().equals(jugador.getPosicion())) {
                moneda.recolectar();
            }
        }

        // 3. Condición de victoria
        if (todasMonedasRecolectadas() && estaEnZonaFinal()) {
            this.nivelCompletado = true;
        }
    }

    /**
     * Reinicia la posición del jugador a la zona segura inicial.
     */
    private void reiniciarJugador() {
        for (ZonaSegura zona : zonasSeguras) {
            if (zona.isEsInicial()) {
                jugador.setPosicion(zona.getPosicion());
                break;
            }
        }
    }

    /**
     * Verifica si todas las monedas han sido recolectadas.
     */
    private boolean todasMonedasRecolectadas() {
        for (Moneda moneda : monedas) {
            if (!moneda.isRecolectada()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica si el jugador se encuentra actualmente en una zona segura final.
     */
    private boolean estaEnZonaFinal() {
        for (ZonaSegura zona : zonasSeguras) {
            if (zona.isEsFinal() && zona.getPosicion().equals(jugador.getPosicion())) {
                return true;
            }
        }
        return false;
    }

    // Getters
    public Jugador getJugador() {
        return jugador;
    }

    public List<EnemigoBasico> getEnemigos() {
        return enemigos;
    }

    public List<Moneda> getMonedas() {
        return monedas;
    }

    public List<Pared> getParedes() {
        return paredes;
    }

    public List<ZonaSegura> getZonasSeguras() {
        return zonasSeguras;
    }

    public boolean isNivelCompletado() {
        return nivelCompletado;
    }
}
