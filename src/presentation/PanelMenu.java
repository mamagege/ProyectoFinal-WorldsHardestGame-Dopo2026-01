package presentation;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Representa el menú de inicio del juego.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class PanelMenu extends JPanel {
    private VentanaPrincipal ventana;

    public PanelMenu(VentanaPrincipal ventana) {
        this.ventana = ventana;
        
        // BoxLayout alinea los componentes de forma vertical (Y_AXIS)
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.decode("#B2D8D8")); // Un color de fondo suave

        // Título del juego
        JLabel titulo = new JLabel("WORLD'S HARDEST GAME");
        titulo.setFont(new Font("Arial", Font.BOLD, 40));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(60, 0, 60, 0));

        // Instanciar botones
        JButton btnPlay = crearBoton("PLAY");
        JButton btnCargar = crearBoton("CARGAR");
        JButton btnOptions = crearBoton("OPTIONS");
        JButton btnHowToPlay = crearBoton("HOW TO PLAY");
        JButton btnSalir = crearBoton("SALIR");

        // Configurar acción del botón PLAY para ir a la vista de juego
        btnPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.mostrarPanel("JUEGO");
            }
        });

        // Configurar acción del botón SALIR
        btnSalir.addActionListener(e -> System.exit(0));

        // Añadir componentes al panel con espacios entre ellos
        add(titulo);
        add(btnPlay);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(btnCargar);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(btnOptions);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(btnHowToPlay);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(btnSalir);
    }

    /**
     * Método auxiliar para centralizar la configuración visual de los botones.
     */
    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.BOLD, 20));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(250, 50));
        boton.setFocusPainted(false);
        return boton;
    }
}
