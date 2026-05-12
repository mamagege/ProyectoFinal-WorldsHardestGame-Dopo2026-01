package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa el contenedor espacial de todas las entidades del juego.
 * Se encarga de definir las dimensiones absolutas del nivel y proveer
 * mecanismos de restricción de movimiento (Clamp) cumpliendo SRP.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class Tablero {
    private double boardWidth;
    private double boardHeight;
    
    // Límites dinámicos para clamping (por defecto cubren todo el tablero)
    private double minX = 0;
    private double maxX;
    private double minY = 0;
    private double maxY;

    private Character character;
    private List<Obstacle> obstacles;
    private List<Coin> coins;
    private List<Wall> walls;
    private List<Checkpoint> checkpoints;
    private Goal goal;
    private List<Tile> tiles;
    private List<ModalityZone> modalityZones;

    public Tablero(double boardWidth, double boardHeight, Character character, List<Obstacle> obstacles,
                   List<Coin> coins, List<Wall> walls, List<Checkpoint> checkpoints, Goal goal) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.maxX = boardWidth;
        this.maxY = boardHeight;
        this.character = character;
        this.obstacles = obstacles != null ? obstacles : new ArrayList<>();
        this.coins = coins != null ? coins : new ArrayList<>();
        this.walls = walls != null ? walls : new ArrayList<>();
        this.checkpoints = checkpoints != null ? checkpoints : new ArrayList<>();
        this.goal = goal;
        this.tiles = new ArrayList<>();
        this.modalityZones = new ArrayList<>();
    }

    /**
     * Permite establecer límites restrictivos invisibles dentro del tablero.
     */
    public void setClampingBounds(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    /**
     * Restringe la coordenada X para mantener el elemento dentro de los límites definidos.
     */
    public double clampX(double x, double elementWidth) {
        return Math.max(minX, Math.min(x, maxX - elementWidth));
    }

    /**
     * Restringe la coordenada Y para mantener el elemento dentro de los límites definidos.
     */
    public double clampY(double y, double elementHeight) {
        return Math.max(minY, Math.min(y, maxY - elementHeight));
    }

    // Getters y Setters de Componentes
    public double getBoardWidth() { return boardWidth; }
    public double getBoardHeight() { return boardHeight; }
    
    public double getMinX() { return minX; }
    public double getMaxX() { return maxX; }
    public double getMinY() { return minY; }
    public double getMaxY() { return maxY; }
    public Character getCharacter() { return character; }
    public List<Obstacle> getObstacles() { return obstacles; }
    public List<Coin> getCoins() { return coins; }
    public List<Wall> getWalls() { return walls; }
    public List<Checkpoint> getCheckpoints() { return checkpoints; }
    public Goal getGoal() { return goal; }
    
    public List<Tile> getTiles() { return tiles; }
    public void setTiles(List<Tile> tiles) { this.tiles = tiles; }
    
    public List<ModalityZone> getModalityZones() { return modalityZones; }
    public void setModalityZones(List<ModalityZone> modalityZones) { this.modalityZones = modalityZones; }
}
