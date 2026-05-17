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
    private GameStrategy strategy;

    public Level(Tablero tablero) {
        this.tablero = tablero;
        this.completed = false;
        this.strategy = new SinglePlayerMode(); // Default strategy
    }

    public void setStrategy(GameStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Motor lógico del nivel. Se llama en cada cuadro (frame) del Game Loop.
     */
    public void tick() {
        if (completed) return;

        // Si algún personaje está explotando, pausamos la física temporalmente
        for (Character character : tablero.getCharacters()) {
            if (character.isExploding()) {
                character.updateExplosion();
                if (!character.isExploding()) {
                    // Animación finalizada
                    character.incrementDeaths();
                    if (strategy != null && strategy instanceof PvPMode) {
                        PvPMode.resetCharacter(tablero, character);
                    } else {
                        SinglePlayerMode.resetCharacterToSpawn(tablero, character);
                        for (Coin coin : tablero.getCoins()) {
                            coin.reset();
                        }
                    }
                }
                return; // Pausa el juego mientras ocurre la animación (de cualquier jugador)
            }
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

        // 2. Mover personajes y validar paredes
        moveCharacters();

        // 3. Chequear colisiones usando la estrategia
        if (strategy != null) {
            strategy.checkCollisions(this);
        }
    }

    private void moveCharacters() {
        for (Character character : tablero.getCharacters()) {
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
    }

    // Los métodos antiguos de colisiones, victoria y reinicio 
    // se han delegado a las clases GameMode y GameStrategy.

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
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // Métodos de estado del juego
    public void softReset() {
        this.completed = false;
        if (tablero != null && tablero.getCoins() != null) {
            for (Coin coin : tablero.getCoins()) {
                coin.reset();
            }
        }
    }

    public boolean isCompleted() { return completed; }
    public boolean isSelectionLevel() { return isSelectionLevel; }
    public void setSelectionLevel(boolean selectionLevel) { this.isSelectionLevel = selectionLevel; }
}
