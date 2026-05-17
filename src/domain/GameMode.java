package domain;

import java.util.List;

/**
 * Clase Abstracta que actúa como Template Method para los distintos Modos de Juego.
 * Provee implementaciones estándar (DRY) para la recolección de monedas y choques
 * con obstáculos, permitiendo que las clases hijas definan reglas de victoria/muerte específicas.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public abstract class GameMode implements GameStrategy {

    @Override
    public void checkCollisions(Level level) {
        Tablero tablero = level.getTablero();
        List<Character> characters = tablero.getCharacters();
        if (characters == null || characters.isEmpty()) return;

        for (Character character : characters) {
            if (character == null) continue;

            // 1. Colisiones con obstáculos mortales
            boolean isHit = false;
            for (Obstacle obstacle : tablero.getObstacles()) {
                if (CollisionDetector.checkCollision(character, obstacle)) {
                    isHit = true;
                    break;
                }
            }

            if (isHit) {
                handleDeath(level, character);
                continue;
            }

            // 2. Recolección de monedas (Común para todos los modos)
            for (Coin coin : tablero.getCoins()) {
                if (!coin.isCollected() && CollisionDetector.checkCollision(character, coin)) {
                    coin.collect();
                }
            }
        }

        // 3. Evaluar victoria luego de revisar todas las interacciones
        checkWinCondition(level);
    }

    /**
     * Resetea todas las monedas del tablero a su estado original.
     * Utilidad para modos que penalizan reiniciando el nivel completo (SinglePlayer).
     */
    protected void resetAllCoins(Tablero tablero) {
        for (Coin coin : tablero.getCoins()) {
            coin.reset();
        }
    }

    /**
     * Verifica si se han recogido todas las monedas del nivel.
     */
    protected boolean allCoinsCollected(Tablero tablero) {
        for (Coin coin : tablero.getCoins()) {
            if (!coin.isCollected()) return false;
        }
        return true;
    }
}
