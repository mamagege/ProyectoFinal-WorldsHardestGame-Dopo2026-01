package domain;

import java.util.List;

/**
 * Lógica del modo de juego Player vs Player.
 * - 2 Jugadores interactuando en el mismo nivel.
 * - Victorias cruzadas (P1 debe llegar a la derecha, P2 a la izquierda).
 * - Muertes aisladas (Si P1 muere, P2 sigue).
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class PvPMode extends GameMode {

    @Override
    public void checkWinCondition(Level level) {
        Tablero tablero = level.getTablero();
        List<Character> characters = tablero.getCharacters();
        if (characters == null || characters.size() < 2) return;
        
        Character p1 = characters.get(0);
        Character p2 = characters.get(1);

        // El Jugador 1 gana si toca el Goal original (Derecha)
        if (tablero.getGoal() != null && CollisionDetector.checkCollision(p1, tablero.getGoal())) {
            level.setCompleted(true);
            System.out.println("¡Jugador 1 ha ganado!");
            return;
        }

        // El Jugador 2 gana si toca el Checkpoint 0 original (Izquierda)
        if (!tablero.getCheckpoints().isEmpty()) {
            Checkpoint p2Goal = tablero.getCheckpoints().get(0);
            if (CollisionDetector.checkCollision(p2, p2Goal)) {
                level.setCompleted(true);
                System.out.println("¡Jugador 2 ha ganado!");
                return;
            }
        }
    }

    @Override
    public void handleDeath(Level level, Character character) {
        if (character.hasArmor()) {
            character.removeArmor();
        } else {
            character.incrementDeaths();
        }
        resetCharacter(level.getTablero(), character);
    }

    /**
     * Auxiliar para reiniciar al personaje a su spawn específico en PvP.
     */
    public static void resetCharacter(Tablero tablero, Character character) {
        List<Character> characters = tablero.getCharacters();
        if (characters == null || characters.isEmpty()) return;

        if (character == characters.get(0)) {
            // Player 1 (Spawn = Izquierda = Checkpoint 0)
            if (!tablero.getCheckpoints().isEmpty()) {
                Checkpoint spawnP1 = tablero.getCheckpoints().get(0);
                character.setPositionX(spawnP1.getPositionX());
                character.setPositionY(spawnP1.getPositionY());
            }
        } else if (characters.size() > 1 && character == characters.get(1)) {
            // Player 2 (Spawn = Derecha = Goal)
            if (tablero.getGoal() != null) {
                Goal spawnP2 = tablero.getGoal();
                character.setPositionX(spawnP2.getPositionX());
                character.setPositionY(spawnP2.getPositionY());
            }
        }
    }
}
