package domain;

import java.util.List;

/**
 * Representa un nivel individual del juego.
 * Administra las entidades y aplica las reglas de colisión y victoria.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class Level {
    private Character character;
    private List<Obstacle> obstacles;
    private List<Coin> coins;
    private List<Wall> walls;
    private List<Checkpoint> checkpoints;
    private Goal goal;
    
    private boolean completed;

    public Level(Character character, List<Obstacle> obstacles, List<Coin> coins, 
                 List<Wall> walls, List<Checkpoint> checkpoints, Goal goal) {
        this.character = character;
        this.obstacles = obstacles;
        this.coins = coins;
        this.walls = walls;
        this.checkpoints = checkpoints;
        this.goal = goal;
        this.completed = false;
    }

    /**
     * Intenta mover al personaje validando los límites y colisiones.
     */
    public void moveCharacter(int targetX, int targetY) {
        if (completed) return;
        
        // Evita el movimiento a través de paredes
        for (Wall wall : walls) {
            if (wall.getPositionX() == targetX && wall.getPositionY() == targetY) {
                return; // Movimiento bloqueado
            }
        }

        // Actualiza la posición
        character.updatePosition(targetX, targetY);
        
        checkCollisions();
    }

    private void checkCollisions() {
        // Colisiones con obstáculos mortales
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getPositionX() == character.getPositionX() && 
                obstacle.getPositionY() == character.getPositionY()) {
                
                character.incrementDeaths();
                resetCharacterPosition();
                return;
            }
        }

        // Recolección de monedas
        for (Coin coin : coins) {
            if (!coin.isCollected() && 
                coin.getPositionX() == character.getPositionX() && 
                coin.getPositionY() == character.getPositionY()) {
                coin.collect();
            }
        }

        // Comprueba la condición de victoria
        if (allCoinsCollected() && 
            goal.getPositionX() == character.getPositionX() && 
            goal.getPositionY() == character.getPositionY()) {
            completed = true;
        }
    }

    private void resetCharacterPosition() {
        if (!checkpoints.isEmpty()) {
            Checkpoint initial = checkpoints.get(0);
            character.updatePosition(initial.getPositionX(), initial.getPositionY());
        }
    }

    private boolean allCoinsCollected() {
        for (Coin coin : coins) {
            if (!coin.isCollected()) return false;
        }
        return true;
    }

    // Getters
    public Character getCharacter() { return character; }
    public List<Obstacle> getObstacles() { return obstacles; }
    public List<Coin> getCoins() { return coins; }
    public List<Wall> getWalls() { return walls; }
    public List<Checkpoint> getCheckpoints() { return checkpoints; }
    public Goal getGoal() { return goal; }
    public boolean isCompleted() { return completed; }
}
