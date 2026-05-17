package domain;

import java.util.List;

/**
 * Lógica del modo de juego de un solo jugador.
 * Implementa las reglas clásicas de Worlds Hardest Game.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class SinglePlayerMode extends GameMode {

    @Override
    public void checkWinCondition(Level level) {
        Tablero tablero = level.getTablero();
        List<Character> characters = tablero.getCharacters();
        if (characters == null || characters.isEmpty()) return;
        
        Character player = characters.get(0);
        
        // El jugador debe recoger todas las monedas y llegar al Goal
        if (allCoinsCollected(tablero) && tablero.getGoal() != null) {
            if (CollisionDetector.checkCollision(player, tablero.getGoal())) {
                level.setCompleted(true);
            }
        }
    }

    @Override
    public void handleDeath(Level level, Character character) {
        if (character.hasArmor()) {
            character.removeArmor();
            resetCharacterToSpawn(level.getTablero(), character);
            resetAllCoins(level.getTablero());
        } else {
            character.triggerExplosion();
            // La animación de explosión tomará el control en Level.tick()
            // y luego sumará la muerte y reseteará.
        }
    }

    /**
     * Auxiliar para reiniciar al personaje al Checkpoint más reciente.
     */
    public static void resetCharacterToSpawn(Tablero tablero, Character character) {
        List<Checkpoint> checkpoints = tablero.getCheckpoints();
        if (!checkpoints.isEmpty()) {
            Checkpoint initial = checkpoints.get(0);
            character.setPositionX(initial.getPositionX());
            character.setPositionY(initial.getPositionY());
        }
    }
}
