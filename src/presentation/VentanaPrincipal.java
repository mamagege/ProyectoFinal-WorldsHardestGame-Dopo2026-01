package presentation;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.CardLayout;
import java.awt.Dimension;

/**
 * Contenedor principal de la aplicación.
 * Utiliza CardLayout para alternar entre los diferentes paneles como Menú,
 * Juego, Opciones, etc.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class VentanaPrincipal extends JFrame {
    private CardLayout cardLayout;
    private PanelMenu panelMenu;
    private PanelJuego panelJuego;

    public VentanaPrincipal() {
        setTitle("The DOPO Hardest Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(800, 600));
        setLocationRelativeTo(null); // Centrar en pantalla
        setResizable(false);

        // Configurar CardLayout
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        // Inicializar paneles
        panelMenu = new PanelMenu(this);
        panelJuego = new PanelJuego(this);

        // Añadir paneles al contenedor principal
        add(panelMenu, "MENU");
        add(panelJuego, "JUEGO");

        // Mostrar menú inicialmente
        mostrarPanel("MENU");
    }

    /**
     * Alterna la vista al panel especificado por su nombre.
     * 
     * @param nombrePanel Nombre del panel a mostrar
     */
    public void mostrarPanel(String nombrePanel) {
        cardLayout.show(getContentPane(), nombrePanel);

        // Es necesario solicitar el foco cuando se cambia al panel de juego
        // para que el KeyListener pueda escuchar los eventos del teclado
        if (nombrePanel.equals("JUEGO")) {
            panelJuego.requestFocusInWindow();
        }
    }

    /**
     * Método principal para ejecutar el juego.
     */
    public static void main(String[] args) {
        // Ejecutar en el hilo de despacho de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
