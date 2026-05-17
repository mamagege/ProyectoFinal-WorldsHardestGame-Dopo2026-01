package presentation;

import domain.*;
import domain.Character;
import domain.GameWHGException;
import data.TxtLevelLoader;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Orquestador principal de The DOPO Hardest Game.
 * Implementa el patrón Prepare aislando la inicialización del juego.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class GameWHGGUI {
    private VentanaPrincipal mainWindow;
    private Timer gameLoopTimer;
    private GameWHG gameFacade;

    public GameWHGGUI() {
        this.gameFacade = new GameWHG();
        prepareElements();
        prepareBoard();
        prepareActions();
    }

    private void prepareElements() {
        // Inyecta el orquestador a los elementos de la interfaz gráfica
        mainWindow = new VentanaPrincipal(this);
    }

    private void prepareBoard() {
        try {
            gameFacade.prepareLevels();
        } catch (GameWHGException ex) {
            domain.Log.record(ex);
            System.err.println(ex.getMessage());
            boolean isVisible = mainWindow != null && mainWindow.isVisible();
            if (isVisible) {
                javax.swing.JOptionPane.showMessageDialog(null, ex.getMessage(), "Error al cargar nivel",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void resetGame() {
        gameFacade.resetGame();
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
    public void iniciarJuegoConPersonaje(int p1Type, int p2Type) {
        try {
            gameFacade.startGameWithCharacters(p1Type, p2Type);
            mainWindow.getPanelSplashLimbo().startSequence();
            mainWindow.mostrarPanel("SPLASH_LIMBO");
        } catch (GameWHGException e) {
            domain.Log.record(e);
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
        return gameFacade.getCurrentLevel();
    }

    public int getCurrentLevelIndex() {
        return gameFacade.getCurrentLevelIndex();
    }

    public GameWHG getGameFacade() {
        return gameFacade;
    }

    public VentanaPrincipal getMainWindow() {
        return mainWindow;
    }

    public Modality getCurrentModality() {
        return gameFacade.getCurrentModality();
    }

    public void tick() {
        try {
            gameFacade.updateGameTick();

            if (gameFacade.shouldGoSelectionMenu()) {
                mainWindow.mostrarPanel("SELECCION");
                return;
            }
            if (gameFacade.shouldGoMainMenu()) {
                mainWindow.mostrarPanel("MENU");
                return;
            }
        } catch (GameWHGException e) {
            domain.Log.record(e);
            boolean isVisible = mainWindow != null && mainWindow.isVisible();
            if (isVisible) {
                javax.swing.JOptionPane.showMessageDialog(null, e.getMessage());
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
            GameWHGGUI game = new GameWHGGUI();
            game.mainWindow.validate(); // Forzar cálculo del Layout antes de mostrarlo en pantalla
            game.mainWindow.setVisible(true);
        });
    }
}
