package domain;

import java.util.List;

/**
 * Representa un nivel individual del juego.
 * Administra las reglas de la partida y orquesta el Game Loop interactuando con el Tablero.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class Level {
    private Tablero tablero;
    private boolean completed;
    private boolean isSelectionLevel = false;

    public Level(Tablero tablero) {
        this.tablero = tablero;
        this.completed = false;
    }

    /**
     * Motor lógico del nivel. Se llama en cada cuadro (frame) del Game Loop.
     */
    public void tick() {
        if (completed) return;

        Character character = tablero.getCharacter();

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
        for (Obstacle obstacle : tablero.getObstacles()) {
            double oldX = obstacle.getPositionX();
            double oldY = obstacle.getPositionY();
            
            // Realizar Movimiento Físico
            obstacle.updatePosition();
            
            // APLICAR CLAMP ESPACIAL ANTES DE LAS COLISIONES CON PAREDES INTERNAS
            obstacle.setPositionX(tablero.clampX(obstacle.getPositionX(), obstacle.getWidth()));
            obstacle.setPositionY(tablero.clampY(obstacle.getPositionY(), obstacle.getHeight()));

            for (Wall wall : tablero.getWalls()) {
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
        Character character = tablero.getCharacter();
        
        // 1. Mover y validar en X
        double oldX = character.getPositionX();
        character.updatePositionX();
        
        // APLICAR CLAMP ESPACIAL EN X
        character.setPositionX(tablero.clampCharacterX(character.getPositionX(), character.getWidth()));
        
        for (Wall wall : tablero.getWalls()) {
            if (CollisionDetector.checkCollision(character, wall)) {
                character.setPositionX(oldX);
                break;
            }
        }

        // 2. Mover y validar en Y
        double oldY = character.getPositionY();
        character.updatePositionY();
        
        // APLICAR CLAMP ESPACIAL EN Y
        character.setPositionY(tablero.clampCharacterY(character.getPositionY(), character.getHeight()));

        for (Wall wall : tablero.getWalls()) {
            if (CollisionDetector.checkCollision(character, wall)) {
                character.setPositionY(oldY);
                break;
            }
        }
    }

    private void checkCollisions() {
        Character character = tablero.getCharacter();
        
        // Colisiones con obstáculos mortales
        for (Obstacle obstacle : tablero.getObstacles()) {
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
        for (Coin coin : tablero.getCoins()) {
            if (!coin.isCollected() && CollisionDetector.checkCollision(character, coin)) {
                coin.collect();
            }
        }

        // Comprueba la condición de victoria
        if (allCoinsCollected() && tablero.getGoal() != null && CollisionDetector.checkCollision(character, tablero.getGoal())) {
            completed = true;
        }
    }

    private void resetCharacterPosition() {
        List<Checkpoint> checkpoints = tablero.getCheckpoints();
        Character character = tablero.getCharacter();
        
        if (!checkpoints.isEmpty()) {
            Checkpoint initial = checkpoints.get(0);
            character.setPositionX(initial.getPositionX());
            character.setPositionY(initial.getPositionY());
        }
        for (Coin coin : tablero.getCoins()) {
            coin.reset();
        }
    }

    private boolean allCoinsCollected() {
        for (Coin coin : tablero.getCoins()) {
            if (!coin.isCollected()) return false;
        }
        return true;
    }

    // Getters delegados al Tablero para asegurar COMPATIBILIDAD de refactorización con presentación.
    public Tablero getTablero() { return tablero; }
    
    public Character getCharacter() { return tablero.getCharacter(); }
    public List<Obstacle> getObstacles() { return tablero.getObstacles(); }
    public List<Coin> getCoins() { return tablero.getCoins(); }
    public List<Wall> getWalls() { return tablero.getWalls(); }
    public List<Checkpoint> getCheckpoints() { return tablero.getCheckpoints(); }
    public Goal getGoal() { return tablero.getGoal(); }
    public List<Tile> getTiles() { return tablero.getTiles(); }
    public List<ModalityZone> getModalityZones() { return tablero.getModalityZones(); }
    
    // Setters de soporte delegados
    public void setTiles(List<Tile> tiles) { tablero.setTiles(tiles); }
    public void setModalityZones(List<ModalityZone> modalityZones) { tablero.setModalityZones(modalityZones); }

    // Métodos de estado del juego
    public boolean isCompleted() { return completed; }
    public boolean isSelectionLevel() { return isSelectionLevel; }
    public void setSelectionLevel(boolean selectionLevel) { this.isSelectionLevel = selectionLevel; }
}
