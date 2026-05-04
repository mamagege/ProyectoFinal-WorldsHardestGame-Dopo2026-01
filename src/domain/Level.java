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

        if (character.isExploding()) {
            character.updateExplosion();
            if (!character.isExploding()) {
                // La animación terminó
                character.incrementDeaths();
                resetCharacterPosition();
            }
            return; // Pausa el juego mientras ocurre la animación
        }

        // 1. Mover obstáculos y rebotar en paredes
        for (Obstacle obstacle : obstacles) {
            double oldX = obstacle.getPositionX();
            double oldY = obstacle.getPositionY();
            obstacle.updatePosition();
            for (Wall wall : walls) {
                if (CollisionDetector.checkCollision(obstacle, wall)) {
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

        // Evita el movimiento a través de paredes usando hitboxes
        for (Wall wall : walls) {
            if (CollisionDetector.checkCollision(character, wall)) {
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
            if (CollisionDetector.checkCollision(character, obstacle)) {
                if (character.hasArmor()) {
                    character.removeArmor();
                    resetCharacterPosition(); 
                } else {
                    character.triggerExplosion(); // Inicia animación en vez de morir instántaneamente
                }
                return;
            }
        }

        // Recolección de monedas
        for (Coin coin : coins) {
            if (!coin.isCollected() && CollisionDetector.checkCollision(character, coin)) {
                coin.collect();
            }
        }

        // Comprueba la condición de victoria
        if (allCoinsCollected() && CollisionDetector.checkCollision(character, goal)) {
            completed = true;
        }
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
