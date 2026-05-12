package presentation;

import domain.*;
import domain.Character; 
import data.TxtLevelLoader;

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
    private LevelLoader levelLoader;

    private int currentLevelIndex = 0;
    private Modality currentModality = Modality.PLAYER;

    public GameWHG() {
        this.levelLoader = new TxtLevelLoader(); // Inyección Directa de Dependencia
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
        // El Nivel 0 (Selección) es un HUB visual especial cargado internamente
        levels.add(buildSelectionLevel());

        // CARGA DINÁMICA: Escanear disco para niveles 1+ secuencialmente
        for (int i = 1; i <= 30; i++) {
            try {
                levels.add(levelLoader.loadLevel(i));
            } catch (LevelLoadException e) {
                // Si no existe el archivo de nivel consecutivo, la colección termina limpiamente.
                break;
            }
        }
        
        if (currentLevelIndex < levels.size()) {
            currentLevel = levels.get(currentLevelIndex);
        }
    }

    private Level buildSelectionLevel() {
        // Personaje Blanco inicializado en el centro de la tierra del mapa
        Character character = new WhiteCharacter(11.0, 7.0);

        List<Obstacle> obstacles = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        List<Wall> walls = new ArrayList<>(); // No hay paredes visibles, el tablero restringe.
        List<Checkpoint> checkpoints = new ArrayList<>();
        Goal goal = null;

        // 1. Configurar el contenedor espacial Tablero
        Tablero tablero = new Tablero(22, 14, character, obstacles, coins, walls, checkpoints, goal);

        // 2. Restringir el movimiento matemáticamente a los bordes de la tierra de la
        // imagen (Clamp)
        // Los valores se definieron tras analizar la perspectiva visual de
        // 'fondo_vacios.png'
        // Límites desplazados 5px (-0.125u) hacia la izquierda
        tablero.setClampingBounds(-1, 18.875, 1.8, 15.295);

        Level selectionLevel = new Level(tablero);
        selectionLevel.setSelectionLevel(true);

        // 3. Crear e inyectar las 3 zonas invisibles coincidiendo con los portales
        List<ModalityZone> modalityZones = new ArrayList<>();

        // Rojo (Portal Arriba-Izquierda) - Movido 10px Izq, 5px Arriba (-0.25u /
        // -0.125u)
        modalityZones.add(new ModalityZone(3.36, 4.705, 1.2, 1.2, Modality.PLAYER));

        // Amarillo (Portal Arriba-Derecha) - Movido 5px Arriba adicionales (-0.125u)
        modalityZones.add(new ModalityZone(14.85, 4.58, 1.2, 1.2, Modality.PVP));

        // Azul (Portal Abajo-Centro) - Movido 5px Derecha (+0.125u)
        modalityZones.add(new ModalityZone(9.48, 11.9, 1.2, 1.2, Modality.PVSM));

        selectionLevel.setModalityZones(modalityZones);
        selectionLevel.setTiles(new ArrayList<>()); // Sin cuadrícula visual clásica

        return selectionLevel;
    }

    public void resetGame() {
        currentLevelIndex = 0;
        prepareBoard();
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
            // Si es el nivel de selección, monitoreamos la entrada a ModalityZones
            if (currentLevel.isSelectionLevel()) {
                for (ModalityZone zone : currentLevel.getModalityZones()) {
                    if (CollisionDetector.checkCollision(currentLevel.getCharacter(), zone)) {
                        this.currentModality = zone.getModality();
                        System.out.println("Modalidad seleccionada: " + this.currentModality);
                        // Cargar el primer nivel de juego real (Index 1)
                        currentLevelIndex = 1;
                        currentLevel = levels.get(currentLevelIndex);
                        return;
                    }
                }
            }

            currentLevel.tick();
            if (currentLevel.isCompleted()) {
                if (currentLevel.isSelectionLevel()) {
                    // Salirse al menú principal y resetear estado
                    resetGame();
                    mainWindow.mostrarPanel("MENU");
                    return;
                }
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
            game.mainWindow.validate(); // Forzar cálculo del Layout antes de mostrarlo en pantalla
            game.mainWindow.setVisible(true);
        });
    }
}
