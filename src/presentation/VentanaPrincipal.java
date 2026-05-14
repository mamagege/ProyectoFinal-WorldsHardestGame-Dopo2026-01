package presentation;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.CardLayout;
import java.awt.Dimension;

/**
 * Contenedor principal de la aplicación.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class VentanaPrincipal extends JFrame {
    private CardLayout cardLayout;
    private PanelMenu panelMenu;
    private PanelJuego panelJuego;
    private PanelSplashScreen panelSplashIntro;
    private PanelSplashScreen panelSplashLimbo;
    private PanelSeleccionPersonaje panelSeleccion;
    private GameWHG gameOrchestrator;

    public VentanaPrincipal(GameWHG gameOrchestrator) {
        this.gameOrchestrator = gameOrchestrator;
        setTitle("The DOPO Hardest Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Sincronizar tamaño inicial con la pantalla para eliminar el efecto visual de expansión
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize); 
        
        setLocationRelativeTo(null); 
        setUndecorated(true); // Elimina bordes y barra de título para Pantalla Completa Real
        setResizable(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH); 

        cardLayout = new CardLayout();
        setLayout(cardLayout);

        panelMenu = new PanelMenu(this);
        panelJuego = new PanelJuego(this, gameOrchestrator);
        panelSplashIntro = new PanelSplashScreen(this, "pantalla_carga_inicio.png", "MENU");
        panelSplashLimbo = new PanelSplashScreen(this, "1.Limbro.png", "JUEGO");
        panelSeleccion = new PanelSeleccionPersonaje(this, gameOrchestrator);

        // Bloquear las dimensiones preferidas internas al tamaño de la pantalla
        // Esto evita que el Layout Manager ajuste el tamaño en el primer frame
        panelMenu.setPreferredSize(screenSize);
        panelJuego.setPreferredSize(screenSize);
        panelSplashIntro.setPreferredSize(screenSize);
        panelSplashLimbo.setPreferredSize(screenSize);
        panelSeleccion.setPreferredSize(screenSize);

        add(panelSplashIntro, "SPLASH_INTRO");
        add(panelSplashLimbo, "SPLASH_LIMBO");
        add(panelMenu, "MENU");
        add(panelJuego, "JUEGO");
        add(panelSeleccion, "SELECCION");

        mostrarPanel("SPLASH_INTRO");
        panelSplashIntro.startSequence(); // Iniciar animación de arranque
    }

    public void mostrarPanel(String nombrePanel) {
        cardLayout.show(getContentPane(), nombrePanel);
        if (nombrePanel.equals("JUEGO")) {
            panelJuego.resetKeyboard(); // Erradicar fantasmgeo de teclas de pantallas previas
            SwingUtilities.invokeLater(() -> panelJuego.requestFocusInWindow());
        }
    }

    public PanelJuego getPanelJuego() {
        return panelJuego;
    }

    public PanelSplashScreen getPanelSplashLimbo() {
        return panelSplashLimbo;
    }

    public PanelSeleccionPersonaje getPanelSeleccion() {
        return panelSeleccion;
    }

    public GameWHG getGameOrchestrator() {
        return gameOrchestrator;
    }
}
