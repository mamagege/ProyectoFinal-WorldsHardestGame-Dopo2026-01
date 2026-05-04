package presentation;

import domain.*;
import domain.Character;

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
    private int boardWidth;
    private int boardHeight;
    private int score;
    private int deaths;
    private Level currentLevel;
    private List<Level> levels;

    private VentanaPrincipal mainWindow;

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

        // Configuración inicial del Nivel 1
        Character character = new Character(1, 1, 1, 1, 1);

        List<Obstacle> obstacles = new ArrayList<>();
        obstacles.add(new Obstacle(5, 5, 1, 1, 1, 'R', true));
        obstacles.add(new Obstacle(4, 3, 1, 1, 1, 'D', false));

        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(8, 3, 1, 1));

        List<Wall> walls = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            walls.add(new Wall(i, 0, 1, 1));
            walls.add(new Wall(i, 10, 1, 1));
            walls.add(new Wall(0, i, 1, 1));
            walls.add(new Wall(10, i, 1, 1));
        }

        List<Checkpoint> checkpoints = new ArrayList<>();
        checkpoints.add(new Checkpoint(1, 1, 1, 1));

        Goal goal = new Goal(8, 8, 1, 1);

        currentLevel = new Level(character, obstacles, coins, walls, checkpoints, goal);
        levels.add(currentLevel);
    }

    private void prepareActions() {
        // Mapeo de eventos o acciones principales del orquestador
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void tick() {
        // Delega la actualización de la interfaz gráfica
        if (mainWindow != null) {
            mainWindow.getPanelJuego().actualizarInterfaz();
        }
    }

    public void exit() {
        System.exit(0);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            GameWHG game = new GameWHG();
            game.mainWindow.setVisible(true);
        });
    }
}
