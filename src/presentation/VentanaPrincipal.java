package presentation;

import javax.swing.JFrame;
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
    private GameWHG gameOrchestrator;

    public VentanaPrincipal(GameWHG gameOrchestrator) {
        this.gameOrchestrator = gameOrchestrator;
        setTitle("The DOPO Hardest Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(800, 600));
        setLocationRelativeTo(null); 
        setResizable(false);

        cardLayout = new CardLayout();
        setLayout(cardLayout);

        panelMenu = new PanelMenu(this);
        panelJuego = new PanelJuego(this, gameOrchestrator);

        add(panelMenu, "MENU");
        add(panelJuego, "JUEGO");

        mostrarPanel("MENU");
    }

    public void mostrarPanel(String nombrePanel) {
        cardLayout.show(getContentPane(), nombrePanel);
        if (nombrePanel.equals("JUEGO")) {
            panelJuego.requestFocusInWindow();
        }
    }

    public PanelJuego getPanelJuego() {
        return panelJuego;
    }
}
