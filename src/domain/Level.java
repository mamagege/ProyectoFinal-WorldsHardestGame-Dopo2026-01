package domain;

import java.util.List;

/**
 * Representa un nivel individual del juego.
 * Administra las entidades y aplica las reglas de colisión y victoria (AABB).
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
     * Motor lógico del nivel. Se llama en cada cuadro (frame) del Game Loop.
     */
    public void tick() {
        if (completed) return;

        // 1. Mover obstáculos y rebotar en paredes
        for (Obstacle obstacle : obstacles) {
            double oldX = obstacle.getPositionX();
            double oldY = obstacle.getPositionY();
            obstacle.updatePosition();
            for (Wall wall : walls) {
                if (checkAABBCollision(obstacle, wall)) {
                    obstacle.setPositionX(oldX);
                    obstacle.setPositionY(oldY);
                    if (obstacle instanceof BasicObstacle) ((BasicObstacle) obstacle).bounce();
                    if (obstacle instanceof FastObstacle) ((FastObstacle) obstacle).bounce();
                    break;
                }
            }
        }

        // 2. Mover personaje y validar paredes
        moveCharacter();

        // 3. Chequear colisiones (AABB)
        checkCollisions();
    }

    private void moveCharacter() {
        double oldX = character.getPositionX();
        double oldY = character.getPositionY();

        character.updatePosition();

        // Evita el movimiento a través de paredes usando AABB
        for (Wall wall : walls) {
            if (checkAABBCollision(character, wall)) {
                // Si choca, deshacemos el movimiento
                character.setPositionX(oldX);
                character.setPositionY(oldY);
                break;
            }
        }
    }

    private void checkCollisions() {
        // Colisiones con obstáculos mortales
        for (Obstacle obstacle : obstacles) {
            if (checkAABBCollision(character, obstacle)) {
                if (character.hasArmor()) {
                    character.removeArmor();
                    resetCharacterPosition(); 
                } else {
                    character.incrementDeaths();
                    resetCharacterPosition();
                }
                return;
            }
        }

        // Recolección de monedas
        for (Coin coin : coins) {
            if (!coin.isCollected() && checkAABBCollision(character, coin)) {
                coin.collect();
            }
        }

        // Comprueba la condición de victoria
        if (allCoinsCollected() && checkAABBCollision(character, goal)) {
            completed = true;
        }
    }

    /**
     * Verifica la intersección de dos rectángulos (Axis-Aligned Bounding Box).
     */
    public boolean checkAABBCollision(Element e1, Element e2) {
        return e1.getPositionX() < e2.getPositionX() + e2.getWidth() &&
               e1.getPositionX() + e1.getWidth() > e2.getPositionX() &&
               e1.getPositionY() < e2.getPositionY() + e2.getHeight() &&
               e1.getPositionY() + e1.getHeight() > e2.getPositionY();
    }

    private void resetCharacterPosition() {
        if (!checkpoints.isEmpty()) {
            Checkpoint initial = checkpoints.get(0);
            character.setPositionX(initial.getPositionX());
            character.setPositionY(initial.getPositionY());
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
