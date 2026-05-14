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
    
    // Límites dinámicos para clamping de obstáculos (por defecto cubren todo el tablero)
    private double minX = 0;
    private double maxX;
    private double minY = 0;
    private double maxY;

    // Límites de baldosas para clamping EXCLUSIVO del personaje (delineado rojo)
    private double tileMinX = -1;
    private double tileMaxX = -1;
    private double tileMinY = -1;
    private double tileMaxY = -1;

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
     * Permite establecer límites restrictivos invisibles dentro del tablero (para obstáculos).
     */
    public void setClampingBounds(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    /**
     * Define los límites exactos del área de baldosas para restringir al personaje
     * exactamente al delineado rojo (independiente de los límites del tablero).
     */
    public void setTileBounds(double minX, double maxX, double minY, double maxY) {
        this.tileMinX = minX;
        this.tileMaxX = maxX;
        this.tileMinY = minY;
        this.tileMaxY = maxY;
    }

    /**
     * Clamp para obstáculos: usa los límites del tablero completo.
     */
    public double clampX(double x, double elementWidth) {
        return Math.max(minX, Math.min(x, maxX - elementWidth));
    }

    /**
     * Clamp para obstáculos: usa los límites del tablero completo.
     */
    public double clampY(double y, double elementHeight) {
        return Math.max(minY, Math.min(y, maxY - elementHeight));
    }

    /**
     * Clamp EXCLUSIVO del personaje: usa los límites de las baldosas (= delineado rojo).
     * Si no se configuraron tileBounds, cae sobre clampX.
     */
    public double clampCharacterX(double x, double elementWidth) {
        if (tileMinX >= 0) {
            return Math.max(tileMinX, Math.min(x, tileMaxX - elementWidth));
        }
        return clampX(x, elementWidth);
    }

    /**
     * Clamp EXCLUSIVO del personaje: usa los límites de las baldosas (= delineado rojo).
     * Si no se configuraron tileBounds, cae sobre clampY.
     */
    public double clampCharacterY(double y, double elementHeight) {
        if (tileMinY >= 0) {
            return Math.max(tileMinY, Math.min(y, tileMaxY - elementHeight));
        }
        return clampY(y, elementHeight);
    }

    // Getters y Setters de Componentes
    public double getBoardWidth() { return boardWidth; }
    public double getBoardHeight() { return boardHeight; }
    
    public double getMinX() { return minX; }
    public double getMaxX() { return maxX; }
    public double getMinY() { return minY; }
    public double getMaxY() { return maxY; }
    public Character getCharacter() { return character; }
    public void setCharacter(Character character) { this.character = character; }
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
