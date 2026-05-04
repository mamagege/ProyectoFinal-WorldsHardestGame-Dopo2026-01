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
            g2d.fillRect(offsetX + (int)(cp.getPositionX() * TAMANO_CELDA), 
                         offsetY + (int)(cp.getPositionY() * TAMANO_CELDA), 
                         (int)(cp.getWidth() * TAMANO_CELDA), 
                         (int)(cp.getHeight() * TAMANO_CELDA));
        }
        Goal goal = level.getGoal();
        if (goal != null) {
            g2d.fillRect(offsetX + (int)(goal.getPositionX() * TAMANO_CELDA), 
                         offsetY + (int)(goal.getPositionY() * TAMANO_CELDA), 
                         (int)(goal.getWidth() * TAMANO_CELDA), 
                         (int)(goal.getHeight() * TAMANO_CELDA));
        }

        // 2. Paredes
        for (Wall pared : level.getWalls()) {
            int px = offsetX + (int)(pared.getPositionX() * TAMANO_CELDA);
            int py = offsetY + (int)(pared.getPositionY() * TAMANO_CELDA);
            int pw = (int)(pared.getWidth() * TAMANO_CELDA);
            int ph = (int)(pared.getHeight() * TAMANO_CELDA);
            
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(px, py, pw, ph);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(px, py, pw, ph);
        }

        // 3. Monedas
        g2d.setColor(Color.YELLOW);
        for (Coin moneda : level.getCoins()) {
            if (!moneda.isCollected()) {
                int px = offsetX + (int)(moneda.getPositionX() * TAMANO_CELDA);
                int py = offsetY + (int)(moneda.getPositionY() * TAMANO_CELDA);
                int w = (int)(moneda.getWidth() * TAMANO_CELDA);
                int h = (int)(moneda.getHeight() * TAMANO_CELDA);
                g2d.fillOval(px, py, w, h);
            }
        }

        // 4. Enemigos Básicos
        g2d.setColor(Color.BLUE);
        for (Obstacle enemigo : level.getObstacles()) {
            int px = offsetX + (int)(enemigo.getPositionX() * TAMANO_CELDA);
            int py = offsetY + (int)(enemigo.getPositionY() * TAMANO_CELDA);
            int w = (int)(enemigo.getWidth() * TAMANO_CELDA);
            int h = (int)(enemigo.getHeight() * TAMANO_CELDA);
            g2d.fillOval(px, py, w, h);
        }

        // 5. Jugador o Animación de Explosión
        Character pJugador = level.getCharacter();
        
        if (pJugador.isExploding()) {
            g2d.setColor(Color.RED);
            for (Particle p : pJugador.getFragments()) {
                int px = offsetX + (int)(p.getPositionX() * TAMANO_CELDA);
                int py = offsetY + (int)(p.getPositionY() * TAMANO_CELDA);
                int pw = (int)(p.getWidth() * TAMANO_CELDA);
                int ph = (int)(p.getHeight() * TAMANO_CELDA);
                g2d.fillRect(px, py, pw, ph);
            }
        } else {
            if (pJugador.hasArmor()) {
                g2d.setColor(Color.GREEN); // Mostrar armadura si tiene
            } else if (pJugador instanceof BlueCharacter) {
                g2d.setColor(Color.CYAN);
            } else {
                g2d.setColor(Color.RED);
            }
            
            int px = offsetX + (int)(pJugador.getPositionX() * TAMANO_CELDA);
            int py = offsetY + (int)(pJugador.getPositionY() * TAMANO_CELDA);
            int pw = (int)(pJugador.getWidth() * TAMANO_CELDA);
            int ph = (int)(pJugador.getHeight() * TAMANO_CELDA);
            
            g2d.fillRect(px, py, pw, ph);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(px, py, pw, ph);
        }
    }
}
