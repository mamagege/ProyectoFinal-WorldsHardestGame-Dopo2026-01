package presentation;

import domain.*;
import domain.Character; // Para resolver ambiguedad

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;

/**
 * Orquestador principal de The DOPO Hardest Game.
 * Implementa el patrón Prepare aislando la inicialización del juego.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class GameWHG {
    private Level currentLevel;
    private List<Level> levels;
    private VentanaPrincipal mainWindow;
    private Timer gameLoopTimer;

    private int currentLevelIndex = 0;

    public GameWHG() {
        prepareElements();
        prepareBoard();
        prepareActions();
    }

    private void prepareElements() {
        // Inyecta el orquestador a los elementos de la interfaz gráfica
        mainWindow = new VentanaPrincipal(this);
    }

    private void prepareBoard() {
        levels = new ArrayList<>();
        levels.add(buildLevel1());
        levels.add(buildLevel2());
        currentLevel = levels.get(currentLevelIndex);
    }

    private Level buildLevel1() {
        Character character = new RedCharacter(1.125, 1.125);

        List<Obstacle> obstacles = new ArrayList<>();
        obstacles.add(new FastObstacle(5.125, 5.125, 'R', true));
        obstacles.add(new BasicObstacle(4.125, 3.125, 'D', false));
        obstacles.add(new PatrolObstacle(7.125, 7.125, 2.0));

        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(8.25, 3.25, 0.5, 0.5));

        List<Wall> walls = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            walls.add(new Wall(i, 0, 1.0, 1.0));
            walls.add(new Wall(i, 10, 1.0, 1.0));
            walls.add(new Wall(0, i, 1.0, 1.0));
            walls.add(new Wall(10, i, 1.0, 1.0));
        }

        List<Checkpoint> checkpoints = new ArrayList<>();
        checkpoints.add(new Checkpoint(1.0, 1.0, 1.0, 1.0));

        Goal goal = new Goal(8.0, 8.0, 1.0, 1.0);

        Level level1 = new Level(character, obstacles, coins, walls, checkpoints, goal);
        
        // Cuadrícula (Damero gris claro y rojo claro)
        List<Tile> tiles = new ArrayList<>();
        for (int x = 1; x < 10; x++) {
            for (int y = 1; y < 10; y++) {
                String colorHex = ((x + y) % 2 == 0) ? "#D3D3D3" : "#F08080";
                tiles.add(new Tile(x, y, 1.0, 1.0, colorHex));
            }
        }
        level1.setTiles(tiles);

        return level1;
    }

    public void resetGame() {
        currentLevelIndex = 0;
        prepareBoard();
    }

    private boolean isPathLevel2(int x, int y) {
        if (x >= 0 && x <= 2 && y >= 2 && y <= 3) return true; // Start
        if (x >= 15 && x <= 17 && y >= 9 && y <= 10) return true; // Goal
        if (x >= 3 && x <= 7 && y >= 2 && y <= 3) return true;
        if (x >= 4 && x <= 7 && y == 0) return true;
        if ((x == 4 || x == 7) && y == 1) return true;
        if (x >= 4 && x <= 5 && y == 4) return true;
        if (x >= 4 && x <= 7 && y == 5) return true;
        if (x >= 6 && x <= 7 && y == 6) return true;
        if (x >= 4 && x <= 7 && y == 7) return true;
        if (x >= 4 && x <= 5 && y == 8) return true;
        if (x >= 4 && x <= 14 && y >= 9 && y <= 10) return true;
        if (x >= 9 && x <= 10 && y >= 0 && y <= 8) return true;
        return false;
    }

    private Level buildLevel2() {
        Character character = new RedCharacter(0.5, 2.5);

        List<Obstacle> obstacles = new ArrayList<>();
        // Enemigo en el bucle superior
        obstacles.add(new BasicObstacle(7.125, 3.125, 'D', false));
        // Pasillo vertical (5 enemigos estáticos)
        obstacles.add(new BasicObstacle(9.125, 0.125, 'R', true));
        obstacles.add(new BasicObstacle(10.125, 2.125, 'L', true));
        obstacles.add(new BasicObstacle(9.125, 4.125, 'R', true));
        obstacles.add(new BasicObstacle(10.125, 6.125, 'L', true));
        obstacles.add(new BasicObstacle(9.125, 8.125, 'R', true));
        // Pasillo inferior (2 enemigos estáticos)
        obstacles.add(new BasicObstacle(12.125, 9.125, 'D', false));
        obstacles.add(new BasicObstacle(13.125, 10.125, 'U', false));
        
        for (Obstacle obs : obstacles) {
            obs.setSpeed(0.0);
        }

        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(5.25, 0.25, 0.5, 0.5));
        coins.add(new Coin(10.25, 0.25, 0.5, 0.5));

        List<Wall> walls = new ArrayList<>();
        for (int x = -1; x <= 18; x++) {
            for (int y = -1; y <= 11; y++) {
                if (!isPathLevel2(x, y)) {
                    walls.add(new Wall(x, y, 1.0, 1.0));
                }
            }
        }

        List<Checkpoint> checkpoints = new ArrayList<>();
        checkpoints.add(new Checkpoint(0, 2, 3.0, 2.0));

        Goal goal = new Goal(15, 9, 3.0, 2.0);

        Level level2 = new Level(character, obstacles, coins, walls, checkpoints, goal);
        
        List<Tile> tiles = new ArrayList<>();
        for (int x = 0; x <= 17; x++) {
            for (int y = 0; y <= 10; y++) {
                if (isPathLevel2(x, y)) {
                    // Excluir Start y Goal de tener damero (tienen fondo verde propio de sus zonas)
                    if ((x >= 0 && x <= 2 && y >= 2 && y <= 3) || 
                        (x >= 15 && x <= 17 && y >= 9 && y <= 10)) {
                        continue;
                    }
                    String colorHex = ((x + y) % 2 == 0) ? "#D3D3D3" : "#F08080";
                    tiles.add(new Tile(x, y, 1.0, 1.0, colorHex));
                }
            }
        }
        level2.setTiles(tiles);

        return level2;
    }

    private void prepareActions() {
        // Timer configurado a ~60 FPS (16 ms)
        gameLoopTimer = new Timer(16, e -> tick());
        gameLoopTimer.start();
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public void tick() {
        if (currentLevel != null) {
            currentLevel.tick();
            if (currentLevel.isCompleted()) {
                currentLevelIndex++;
                if (currentLevelIndex < levels.size()) {
                    currentLevel = levels.get(currentLevelIndex);
                } else {
                    currentLevelIndex--; // Mantenerse en el último nivel si se acaba el juego
                }
            }
        }
        if (mainWindow != null && mainWindow.getPanelJuego() != null) {
            mainWindow.getPanelJuego().actualizarInterfaz();
        }
    }

    public void exit() {
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWHG game = new GameWHG();
            game.mainWindow.setVisible(true);
        });
    }
}
