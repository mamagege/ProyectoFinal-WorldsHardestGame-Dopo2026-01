package domain;

/**
 * Define el contrato de reglas de victoria y físicas de muerte
 * para cada modalidad de juego (SinglePlayer, PvP, PvSM).
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public interface GameStrategy {
    /**
     * Verifica las colisiones entre los jugadores y los elementos del entorno
     * (obstáculos, monedas, etc).
     */
    void checkCollisions(Level level);

    /**
     * Verifica si algún jugador ha cumplido la condición de victoria para 
     * dar por terminado el nivel.
     */
    void checkWinCondition(Level level);

    /**
     * Maneja lo que ocurre cuando un jugador específico colisiona con un obstáculo.
     */
    void handleDeath(Level level, Character character);
}
