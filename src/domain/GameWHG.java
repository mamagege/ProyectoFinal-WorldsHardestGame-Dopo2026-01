package domain;

import data.TxtLevelLoader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Facade (Fachada) del Patrón de Diseño para el Modelo del Dominio.
 * Centraliza y orquesta todas las reglas de negocio, carga de niveles y lógicas de físicas.
 * La capa de presentación se comunica exclusivamente a través de esta clase.
 *
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class GameWHG {
    private List<Level> levels;
    private Level currentLevel;
    private int currentLevelIndex;
    private Modality currentModality;
    private LevelLoader levelLoader;
    
    // Banderas de estado para la UI
    private boolean goSelectionMenuFlag = false;
    private boolean goMainMenuFlag = false;

    public GameWHG() {
        this.levelLoader = new TxtLevelLoader();
        this.levels = new ArrayList<>();
        this.currentModality = Modality.PLAYER;
        this.currentLevelIndex = 0;
    }

    /**
     * Construye y carga todos los niveles disponibles.
     */
    public void prepareLevels() throws GameWHGException {
        levels.clear();
        levels.add(buildSelectionLevel());

        for (int i = 1; i <= 30; i++) {
            File file = new File("src/resources/levels/level" + i + ".txt");
            if (!file.exists()) {
                break;
            }
            try {
                levels.add(levelLoader.loadLevel(i));
            } catch (LevelLoadException e) {
                if (e.getMessage() != null && e.getMessage().contains("sintaxis")) {
                    throw new GameWHGException(GameWHGException.ERROR_SINTAXIS_NIVEL);
                } else if (e.getMessage() != null && e.getMessage().contains("Consistencia")) {
                    throw new GameWHGException(GameWHGException.ERROR_CONEXION_TABLERO);
                } else {
                    throw new GameWHGException(GameWHGException.ERROR_FALLO_LECTURA_NIVEL);
                }
            }
        }
        
        if (!levels.isEmpty()) {
            currentLevel = levels.get(0);
            currentLevel.setStrategy(new SinglePlayerMode()); // HUB Selection is basically SinglePlayer
        }
    }

    /**
     * Resetea la orquestación de niveles.
     */
    public void resetGame() {
        currentLevelIndex = 0;
        if (!levels.isEmpty()) {
            currentLevel = levels.get(0);
        }
        this.currentModality = Modality.PLAYER;
    }

    /**
     * Actualiza la lógica de un fotograma (16ms)
     * @throws GameWHGException si ocurre un error lógico (como intentar avanzar sin monedas).
     */
    public void updateGameTick() throws GameWHGException {
        if (currentLevel == null) return;
        
        goSelectionMenuFlag = false;
        goMainMenuFlag = false;

        if (currentLevel.isSelectionLevel()) {
            for (ModalityZone zone : currentLevel.getModalityZones()) {
                if (CollisionDetector.checkCollision(currentLevel.getCharacter(), zone)) {
                    System.out.println("[DEBUG] Collision detected with ModalityZone: " + zone.getModality());
                    this.currentModality = zone.getModality();
                    System.out.println("Modalidad seleccionada: " + this.currentModality);
                    // Resetear posición y velocidad
                    currentLevel.getCharacter().setPositionX(24.0);
                    currentLevel.getCharacter().setPositionY(22.0);
                    currentLevel.getCharacter().setVelocity(0.0, 0.0);
                    goSelectionMenuFlag = true;
                    return;
                }
            }
        }

        currentLevel.tick();

        if (currentLevel.isCompleted()) {
            if (currentLevel.isSelectionLevel()) {
                resetGame();
                goMainMenuFlag = true;
                return;
            }

            currentLevelIndex++;
            if (currentLevelIndex < levels.size()) {
                currentLevel = levels.get(currentLevelIndex);
            } else {
                currentLevelIndex--; 
                throw new GameWHGException(GameWHGException.ERROR_LIMITE_NIVELES);
            }
        }
    }

    /**
     * Inicia el juego inyectando los personajes seleccionados en todos los niveles.
     */
    public void startGameWithCharacters(int p1Type, int p2Type) throws GameWHGException {
        if (p1Type < 0 || p1Type > 2 || (currentModality == Modality.PVP && (p2Type < 0 || p2Type > 2))) {
            throw new GameWHGException(GameWHGException.ERROR_PERSONAJE_INVALIDO);
        }

        for (Level lvl : levels) {
            lvl.softReset();
            // Inyectar estrategia
            if (currentModality == Modality.PVP) {
                lvl.setStrategy(new PvPMode());
            } else if (currentModality == Modality.PVSM) {
                lvl.setStrategy(new PvSMMode());
            } else {
                lvl.setStrategy(new SinglePlayerMode());
            }
        }

        for (int i = 1; i < levels.size(); i++) {
            Level lvl = levels.get(i);
            Tablero tablero = lvl.getTablero();
            
            double spawnX = 1.0, spawnY = 1.0;
            if (!tablero.getCheckpoints().isEmpty()) {
                spawnX = tablero.getCheckpoints().get(0).getPositionX();
                spawnY = tablero.getCheckpoints().get(0).getPositionY();
            }
            
            List<Character> chars = new ArrayList<>();
            chars.add(createCharacter(p1Type, spawnX, spawnY));

            if (currentModality == Modality.PVP) {
                double p2SpawnX = 40.0, p2SpawnY = 20.0;
                if (tablero.getGoal() != null) {
                    p2SpawnX = tablero.getGoal().getPositionX();
                    p2SpawnY = tablero.getGoal().getPositionY();
                }
                chars.add(createCharacter(p2Type, p2SpawnX, p2SpawnY));
            }
            
            tablero.setCharacters(chars);
        }

        currentLevelIndex = 1;
        if (currentLevelIndex < levels.size()) {
            currentLevel = levels.get(currentLevelIndex);
        }
    }

    private Character createCharacter(int type, double x, double y) {
        switch (type) {
            case 1: return new domain.BlueCharacter(x, y);
            case 2: return new domain.GreenCharacter(x, y);
            default: return new domain.RedCharacter(x, y);
        }
    }

    /**
     * Traduce los inputs de la presentación a velocidades físicas.
     */
    public void movePlayer(int playerId, double vx, double vy) {
        if (currentLevel == null || currentLevel.isCompleted()) return;
        List<Character> chars = currentLevel.getTablero().getCharacters();
        if (chars != null && playerId >= 0 && playerId < chars.size()) {
            chars.get(playerId).setVelocity(vx, vy);
        }
    }

    // Getters de Sólo Lectura para la Vista
    public Level getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public Modality getCurrentModality() {
        return currentModality;
    }
    
    public double getPlayerSpeed(int playerId) {
        if (currentLevel != null) {
            List<Character> chars = currentLevel.getTablero().getCharacters();
            if (chars != null && playerId >= 0 && playerId < chars.size()) {
                return chars.get(playerId).getSpeed();
            }
        }
        return 0;
    }

    // Flags de navegacion de UI
    public boolean shouldGoSelectionMenu() { return goSelectionMenuFlag; }
    public boolean shouldGoMainMenu() { return goMainMenuFlag; }

    // Métodos Auxiliares de Construcción Interna
    private Level buildSelectionLevel() {
        Character character = new WhiteCharacter(24.0, 22.0);
        List<Obstacle> obstacles = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        List<Checkpoint> checkpoints = new ArrayList<>();
        Goal goal = null;
        List<Wall> walls = new ArrayList<>();
        for (int x = 0; x < 48; x++) {
            for (int y = 0; y < 27; y++) {
                if (!isWalkableSelection(x, y)) {
                    walls.add(new Wall(x, y, 1.0, 1.0));
                }
            }
        }
        Tablero tablero = new Tablero(48, 27, character, obstacles, coins, walls, checkpoints, goal);
        Level selectionLevel = new Level(tablero);
        selectionLevel.setSelectionLevel(true);

        List<ModalityZone> modalityZones = new ArrayList<>();
        modalityZones.add(new ModalityZone(3.5, 5.0, 3.5, 7.0, Modality.PLAYER));
        modalityZones.add(new ModalityZone(19.0, 3.5, 4.0, 6.5, Modality.PVP));
        modalityZones.add(new ModalityZone(42.5, 5.0, 3.5, 7.0, Modality.PVSM));
        selectionLevel.setModalityZones(modalityZones);
        selectionLevel.setTiles(new ArrayList<>());
        return selectionLevel;
    }

    private boolean isWalkableSelection(int x, int y) {
        if (y >= 18 && y <= 26) {
            int leftBound = 12 + (y - 18) / 2;
            int rightBound = 35 - (y - 18) / 2;
            if (y >= 23 && x >= 30) return false;
            if (x >= leftBound && x <= rightBound) return true;
        }
        if (y >= 12 && y <= 19 && x >= 6 && x <= 16) {
            int diff = y - x;
            if (diff >= 3 && diff <= 7) return true;
        }
        if (y >= 6 && y <= 12 && x >= 2 && x <= 8) return true;
        if (y >= 9 && y <= 18 && x >= 19 && x <= 23) return true;
        if (y >= 3 && y <= 9 && x >= 19 && x <= 23) return true;
        if (y >= 12 && y <= 19 && x >= 31 && x <= 41) {
            int sum = x + y;
            if (sum >= 47 && sum <= 54) return true;
        }
        if (y >= 6 && y <= 12 && x >= 39 && x <= 46) return true;
        return false;
    }
}
