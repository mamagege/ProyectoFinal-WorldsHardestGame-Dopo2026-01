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

        // CARGA DINAMICA: Escanear disco para niveles 1+ secuencialmente
        for (int i = 1; i <= 30; i++) {
            try {
                levels.add(levelLoader.loadLevel(i));
            } catch (LevelLoadException e) {
                // Si no existe el archivo de nivel consecutivo, la colección termina
                // limpiamente.
                break;
            }
        }

        if (currentLevelIndex < levels.size()) {
            currentLevel = levels.get(currentLevelIndex);
        }
    }

    private boolean isWalkableSelection(int x, int y) {
        // 1. Plaza Central Inferior (Con forma trapezoidal para excluir rocas
        // inferiores)
        if (y >= 18 && y <= 26) {
            // Estrechar la base lateralmente conforme bajamos en perspectiva Y
            int leftBound = 12 + (y - 18) / 2;
            int rightBound = 35 - (y - 18) / 2;

            // Recorte manual extremo para el cúmulo de rocas abajo a la derecha
            if (y >= 23 && x >= 30)
                return false;

            if (x >= leftBound && x <= rightBound)
                return true;
        }

        // 2. Camino y Escaleras hacia el Portal RED (Restricción a banda diagonal 45°)
        if (y >= 12 && y <= 19 && x >= 6 && x <= 16) {
            int diff = y - x;
            // Esto recorta perfectamente los dos triángulos rojos que marcaste (abajo y
            // arriba de la escalera)
            if (diff >= 3 && diff <= 7)
                return true;
        }
        // Plataforma final del portal Rojo - DESPEJADA para libre tránsito (Círculo
        // rojo solicitado)
        if (y >= 6 && y <= 12 && x >= 2 && x <= 8)
            return true;

        // 3. Camino y Escaleras hacia el Portal GREEN (Centro) - EXPANDIDO A LA DERECHA
        // (Zona roja solicitada)
        if (y >= 9 && y <= 18 && x >= 19 && x <= 23)
            return true;
        // Plataforma final del portal Verde
        if (y >= 3 && y <= 9 && x >= 19 && x <= 23)
            return true;

        // 4. Camino y Escaleras hacia el Portal BLUE (Restricción a banda diagonal
        // inversa)
        if (y >= 12 && y <= 19 && x >= 31 && x <= 41) {
            int sum = x + y;
            // Recorta las rocas derechas. Relajado a >=47 para habilitar el triángulo
            // superior izquierdo del escalón.
            if (sum >= 47 && sum <= 54)
                return true;
        }
        // Plataforma final del portal Azul - DESPEJADA COMPLETA para el jugador
        // (Círculo rojo solicitado)
        if (y >= 6 && y <= 12 && x >= 39 && x <= 46)
            return true;

        return false;
    }

    private Level buildSelectionLevel() {
        // Spawn del personaje en el centro de la plaza inferior (Plano 48x27)
        Character character = new WhiteCharacter(24.0, 22.0);

        List<Obstacle> obstacles = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        List<Checkpoint> checkpoints = new ArrayList<>();
        Goal goal = null;

        // Paredes invisibles que delimitan las escaleras y plataformas del fondo
        List<Wall> walls = new ArrayList<>();
        for (int x = 0; x < 48; x++) {
            for (int y = 0; y < 27; y++) {
                if (!isWalkableSelection(x, y)) {
                    walls.add(new Wall(x, y, 1.0, 1.0));
                }
            }
        }

        // 1. Configurar el contenedor espacial Tablero con dimensiones 16:9 nativas
        Tablero tablero = new Tablero(48, 27, character, obstacles, coins, walls, checkpoints, goal);

        Level selectionLevel = new Level(tablero);
        selectionLevel.setSelectionLevel(true);

        // 2. Crear e inyectar las 3 zonas de detección centradas en los portales
        // visuales
        // AJUSTADO: Posiciones y tamaños expandidos para cubrir todo el arco de
        // energía.
        List<ModalityZone> modalityZones = new ArrayList<>();

        // Portal Rojo (Izquierda): Expandido verticalmente para tragar todo el arco
        // (como marcaste en Morado)
        modalityZones.add(new ModalityZone(3.5, 5.0, 3.5, 7.0, Modality.PLAYER));
        // Portal Verde (Centro superior): Crecido para llegar hasta la base de piedra
        modalityZones.add(new ModalityZone(19.0, 3.5, 4.0, 6.5, Modality.PVP));
        // Portal Azul (Derecha): Crecido hacia abajo para tocar el piso de la
        // plataforma
        modalityZones.add(new ModalityZone(42.5, 5.0, 3.5, 7.0, Modality.PVSM));

        selectionLevel.setModalityZones(modalityZones);
        selectionLevel.setTiles(new ArrayList<>());

        return selectionLevel;
    }

    public void resetGame() {
        currentLevelIndex = 0;
        prepareBoard();
    }

    /**
     * Metodo invocado por PanelSeleccionPersonaje cuando el jugador elige un
     * personaje.
     * Sustituye el personaje en todos los niveles cargados, resetea el estado y
     * navega
     * al juego a traves de la cinematica de precarga (SPLASH_LIMBO).
     *
     * @param tipoPersonaje 0=Rojo(Rafael), 1=Azul(Leonardo), 2=Verde(MiguelAngelo)
     */
    public void iniciarJuegoConPersonaje(int tipoPersonaje) {
        try {
            if (tipoPersonaje < 0 || tipoPersonaje > 2) {
                throw new GameWHGException(GameWHGException.ERROR_PERSONAJE_INVALIDO);
            }

            // 1. Crear el personaje elegido en posicion inicial de referencia (spawn)
            Character personajeElegido;
            switch (tipoPersonaje) {
                case PanelSeleccionPersonaje.PERSONAJE_AZUL:
                    personajeElegido = new domain.BlueCharacter(1.0, 1.0);
                    break;
                case PanelSeleccionPersonaje.PERSONAJE_VERDE:
                    personajeElegido = new domain.GreenCharacter(1.0, 1.0);
                    break;
                case PanelSeleccionPersonaje.PERSONAJE_ROJO:
                default:
                    personajeElegido = new domain.RedCharacter(1.0, 1.0);
                    break;
            }

            // 2. Propagar el personaje elegido a todos los niveles reales (index 1+)
            // Sustituimos la recarga costosa de disco por un reset en memoria ultra veloz.
            // Esto elimina instantáneamente el micro-congelamiento de los botones.
            for (Level lvl : levels) {
                lvl.softReset();
            }

            // Patchear el personaje en cada nivel cargado (los que heredan de
            // TxtLevelLoader)
            for (int i = 1; i < levels.size(); i++) {
                Level lvl = levels.get(i);
                // Clonar posicion spawn del checkpoint 0 (si existe)
                double spawnX = 1.0, spawnY = 1.0;
                if (!lvl.getCheckpoints().isEmpty()) {
                    spawnX = lvl.getCheckpoints().get(0).getPositionX();
                    spawnY = lvl.getCheckpoints().get(0).getPositionY();
                }
                // Crear instancia fresca del personaje elegido en el spawn correcto
                Character c;
                switch (tipoPersonaje) {
                    case PanelSeleccionPersonaje.PERSONAJE_AZUL:
                        c = new domain.BlueCharacter(spawnX, spawnY);
                        break;
                    case PanelSeleccionPersonaje.PERSONAJE_VERDE:
                        c = new domain.GreenCharacter(spawnX, spawnY);
                        break;
                    default:
                        c = new domain.RedCharacter(spawnX, spawnY);
                        break;
                }
                lvl.getTablero().setCharacter(c);
            }

            // 3. Ir al primer nivel de juego real
            currentLevelIndex = 1;
            if (currentLevelIndex < levels.size()) {
                currentLevel = levels.get(currentLevelIndex);
            }

            // 4. Mostrar el juego directamente sin repetir el splash limbo
            mainWindow.mostrarPanel("JUEGO");
        } catch (GameWHGException e) {
            boolean isVisible = mainWindow != null && mainWindow.isVisible();
            if (isVisible) {
                javax.swing.JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
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
                        // Resetear posición del personaje para evitar multiples colisiones mientras se
                        // está en otra pantalla
                        currentLevel.getCharacter().setPositionX(24.0);
                        currentLevel.getCharacter().setPositionY(22.0);

                        // Ir a la pantalla de selección de personaje
                        mainWindow.mostrarPanel("SELECCION");
                        return;
                    }
                }
            }

            currentLevel.tick();
            if (currentLevel.isCompleted()) {
                try {
                    if (currentLevel.isSelectionLevel()) {
                        // Salirse al menú principal y resetear estado
                        resetGame();
                        mainWindow.mostrarPanel("MENU");
                        return;
                    }
                    
                    // Validación por regla de negocio: ERROR_AVANCE_PREMATURO
                    boolean allCoinsCollected = true;
                    if (currentLevel.getCoins() != null) {
                        for (Coin coin : currentLevel.getCoins()) {
                            if (!coin.isCollected()) {
                                allCoinsCollected = false;
                                break;
                            }
                        }
                    }
                    if (!allCoinsCollected) {
                        currentLevel.softReset(); // Revertir el estado de completado
                        throw new GameWHGException(GameWHGException.ERROR_AVANCE_PREMATURO);
                    }
                    
                    currentLevelIndex++;
                    if (currentLevelIndex < levels.size()) {
                        currentLevel = levels.get(currentLevelIndex);
                    } else {
                        currentLevelIndex--; // Mantenerse en el último nivel si se acaba el juego
                    }
                } catch (GameWHGException e) {
                    boolean isVisible = mainWindow != null && mainWindow.isVisible();
                    if (isVisible) {
                        javax.swing.JOptionPane.showMessageDialog(null, e.getMessage());
                    }
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
