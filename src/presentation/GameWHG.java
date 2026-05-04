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
        
        // Configuración inicial del Nivel 1 con variantes
        Character character = new RedCharacter(1.0, 1.0);
        
        List<Obstacle> obstacles = new ArrayList<>();
        obstacles.add(new FastObstacle(5.0, 5.0, 'R', true));
        obstacles.add(new BasicObstacle(4.0, 3.0, 'D', false));
        obstacles.add(new PatrolObstacle(7.0, 7.0, 2.0));
        
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(8.0, 3.0, 1.0, 1.0));
        
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

        currentLevel = new Level(character, obstacles, coins, walls, checkpoints, goal);
        levels.add(currentLevel);
    }

    private void prepareActions() {
        // Timer configurado a ~60 FPS (16 ms)
        gameLoopTimer = new Timer(16, e -> tick());
        gameLoopTimer.start();
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void tick() {
        if (currentLevel != null) {
            currentLevel.tick();
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
