package presentation;

import domain.*;
import domain.Character;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel encargado de renderizar el nivel actual del juego.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class PanelJuego extends JPanel {
    private VentanaPrincipal ventana;
    private GameWHG gameOrchestrator;
    private final int TAMANO_CELDA = 40;
    private JLabel labelMuertes;
    private JLabel labelNivel;

    public PanelJuego(VentanaPrincipal ventana, GameWHG gameOrchestrator) {
        this.ventana = ventana;
        this.gameOrchestrator = gameOrchestrator;
        setLayout(new BorderLayout());
        setBackground(Color.decode("#E6E6FA"));

        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setBackground(Color.LIGHT_GRAY);

        JButton btnMenu = new JButton("MENU");
        btnMenu.addActionListener(e -> ventana.mostrarPanel("MENU"));

        labelNivel = new JLabel("Nivel: 1/30", JLabel.CENTER);
        labelNivel.setFont(new Font("Arial", Font.BOLD, 16));

        labelMuertes = new JLabel("Deaths: 0  ");
        labelMuertes.setFont(new Font("Arial", Font.BOLD, 16));

        barraSuperior.add(btnMenu, BorderLayout.WEST);
        barraSuperior.add(labelNivel, BorderLayout.CENTER);
        barraSuperior.add(labelMuertes, BorderLayout.EAST);

        add(barraSuperior, BorderLayout.NORTH);

        ControladorJuego controlador = new ControladorJuego(this, gameOrchestrator);
        addKeyListener(controlador);
        setFocusable(true);
    }

    public void actualizarInterfaz() {
        Level level = gameOrchestrator.getCurrentLevel();
        if (level != null) {
            labelMuertes.setText("Deaths: " + level.getCharacter().getDeaths() + "  ");
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        int offsetX = 100;
        int offsetY = 50;

        Level level = gameOrchestrator.getCurrentLevel();
        if (level == null) return;

        // 1. Zonas Seguras
        g2d.setColor(Color.decode("#B5E61D")); 
        for (Checkpoint cp : level.getCheckpoints()) {
            g2d.fillRect(offsetX + cp.getPositionX() * TAMANO_CELDA, offsetY + cp.getPositionY() * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);
        }
        Goal goal = level.getGoal();
        if (goal != null) {
            g2d.fillRect(offsetX + goal.getPositionX() * TAMANO_CELDA, offsetY + goal.getPositionY() * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);
        }

        // 2. Paredes
        g2d.setColor(Color.DARK_GRAY);
        for (Wall pared : level.getWalls()) {
            g2d.fillRect(offsetX + pared.getPositionX() * TAMANO_CELDA, offsetY + pared.getPositionY() * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(offsetX + pared.getPositionX() * TAMANO_CELDA, offsetY + pared.getPositionY() * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);
            g2d.setColor(Color.DARK_GRAY);
        }

        // 3. Monedas
        g2d.setColor(Color.YELLOW);
        for (Coin moneda : level.getCoins()) {
            if (!moneda.isCollected()) {
                int padding = 10;
                int radio = TAMANO_CELDA - (padding * 2);
                g2d.fillOval(offsetX + moneda.getPositionX() * TAMANO_CELDA + padding, offsetY + moneda.getPositionY() * TAMANO_CELDA + padding, radio, radio);
            }
        }

        // 4. Enemigos Básicos
        g2d.setColor(Color.BLUE);
        for (Obstacle enemigo : level.getObstacles()) {
            int padding = 5;
            int radio = TAMANO_CELDA - (padding * 2);
            g2d.fillOval(offsetX + enemigo.getPositionX() * TAMANO_CELDA + padding, offsetY + enemigo.getPositionY() * TAMANO_CELDA + padding, radio, radio);
        }

        // 5. Jugador
        g2d.setColor(Color.RED);
        Character pJugador = level.getCharacter();
        int pJug = 5;
        int tamJugador = TAMANO_CELDA - (pJug * 2);
        g2d.fillRect(offsetX + pJugador.getPositionX() * TAMANO_CELDA + pJug, offsetY + pJugador.getPositionY() * TAMANO_CELDA + pJug, tamJugador, tamJugador);
        
        g2d.setColor(Color.BLACK);
        g2d.drawRect(offsetX + pJugador.getPositionX() * TAMANO_CELDA + pJug, offsetY + pJugador.getPositionY() * TAMANO_CELDA + pJug, tamJugador, tamJugador);
    }
}
