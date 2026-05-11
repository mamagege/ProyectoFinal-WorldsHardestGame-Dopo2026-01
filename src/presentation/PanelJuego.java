package presentation;

import domain.*;
import domain.CollisionDetector;
import domain.Character;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.awt.FontFormatException;
import java.awt.BasicStroke;
import java.awt.geom.Area;
import java.awt.Rectangle;
import javax.imageio.ImageIO;

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
    private JPanel barraSuperior;
    private Font gameFont;
    private double zoomLevel = 1.0;
    private Modality activeModality = null;
    private Image backgroundImage;
    private Image modalityPlayerImage;
    private Font titleFont;

    public PanelJuego(VentanaPrincipal ventana, GameWHG gameOrchestrator) {
        this.ventana = ventana;
        this.gameOrchestrator = gameOrchestrator;
        setLayout(new BorderLayout());
        setBackground(Color.decode("#E6E6FA"));

        barraSuperior = new JPanel(new BorderLayout());
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

        cargarFuentes();
        cargarBackground();
    }

    private void cargarFuentes() {
        try {
            File fontFile = new File("src/resources/fonts/Luvable.ttf");
            if (fontFile.exists()) {
                gameFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(28f);
            } else {
                gameFont = new Font("Arial", Font.BOLD, 28); // Fallback
            }
        } catch (FontFormatException | IOException e) {
            gameFont = new Font("Arial", Font.BOLD, 28);
        }

        try {
            File titleFontFile = new File("src/resources/fonts/HellraiserBloody.ttf");
            if (titleFontFile.exists()) {
                titleFont = Font.createFont(Font.TRUETYPE_FONT, titleFontFile).deriveFont(50f);
            } else {
                titleFont = new Font("Arial", Font.BOLD, 50);
            }
        } catch (FontFormatException | IOException e) {
            titleFont = new Font("Arial", Font.BOLD, 50);
        }
    }

    private void cargarBackground() {
        try {
            backgroundImage = ImageIO.read(new File("src/resources/images/fondo_selva_oscura.png"));
        } catch (IOException e) {
            System.err.println("Advertencia: No se pudo cargar fondo_selva_oscura.png");
        }

        try {
            modalityPlayerImage = ImageIO.read(new File("src/resources/images/modalidad_player.png"));
        } catch (IOException e) {
            System.err.println("Advertencia: No se pudo cargar modalidad_player.png");
        }
    }

    public void actualizarInterfaz() {
        Level level = gameOrchestrator.getCurrentLevel();
        if (level != null) {
            // Ocultar barra superior si estamos en el nivel de selección
            boolean visible = !level.isSelectionLevel();
            if (barraSuperior.isVisible() != visible) {
                barraSuperior.setVisible(visible);
                revalidate(); // Forzar re-layout al cambiar visibilidad
            }
            
            if (visible) {
                labelMuertes.setText("Deaths: " + level.getCharacter().getDeaths() + "  ");
                labelNivel.setText("Nivel: " + (gameOrchestrator.getCurrentLevelIndex() + 1) + "/30");
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        Level level = gameOrchestrator.getCurrentLevel();
        if (level == null)
            return;

        // Si es el nivel de selección, dibujamos la imagen de fondo escalada
        if (level.isSelectionLevel() && backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            
            if (titleFont != null) {
                String title = "Selecciona la modalidad";
                g2d.setFont(titleFont);
                FontMetrics fm = g2d.getFontMetrics();
                int titleX = (getWidth() - fm.stringWidth(title)) / 2;
                int titleY = 60;
                g2d.setColor(Color.BLACK);
                g2d.drawString(title, titleX + 2, titleY + 2);
                g2d.setColor(Color.WHITE);
                g2d.drawString(title, titleX, titleY);
            }
        }

        int maxX = 0;
        int maxY = 0;
        for (Wall w : level.getWalls()) {
            if (w.getPositionX() > maxX)
                maxX = (int) w.getPositionX();
            if (w.getPositionY() > maxY)
                maxY = (int) w.getPositionY();
        }
        int mapWidth = (maxX + 1) * TAMANO_CELDA;
        int mapHeight = (maxY + 1) * TAMANO_CELDA;

        int offsetX = Math.max(20, (getWidth() - mapWidth) / 2);
        int offsetY = Math.max(50, (getHeight() - mapHeight) / 2);

        // 0. Baldosas (Tiles) del Damero
        for (Tile tile : level.getTiles()) {
            g2d.setColor(Color.decode(tile.getColorHex()));
            g2d.fillRect(offsetX + (int) (tile.getPositionX() * TAMANO_CELDA),
                    offsetY + (int) (tile.getPositionY() * TAMANO_CELDA),
                    (int) (tile.getWidth() * TAMANO_CELDA),
                    (int) (tile.getHeight() * TAMANO_CELDA));
        }



        // 1. Zonas Seguras (Verdes)
        g2d.setColor(new Color(142, 210, 127)); // #8ed27f (Exacto al mockup)
        for (Checkpoint cp : level.getCheckpoints()) {
            g2d.fillRect(offsetX + (int) (cp.getPositionX() * TAMANO_CELDA),
                    offsetY + (int) (cp.getPositionY() * TAMANO_CELDA),
                    (int) (cp.getWidth() * TAMANO_CELDA),
                    (int) (cp.getHeight() * TAMANO_CELDA));
        }
        Goal goal = level.getGoal();
        if (goal != null) {
            g2d.fillRect(offsetX + (int) (goal.getPositionX() * TAMANO_CELDA),
                    offsetY + (int) (goal.getPositionY() * TAMANO_CELDA),
                    (int) (goal.getWidth() * TAMANO_CELDA),
                    (int) (goal.getHeight() * TAMANO_CELDA));
        }

        // 1.1 Zonas de Modalidad
        activeModality = null;
        for (ModalityZone mz : level.getModalityZones()) {
            // Color según la modalidad
            switch (mz.getModality()) {
                case NORMAL: g2d.setColor(new Color(231, 76, 60)); break; // Rojo Suave
                case PVP: g2d.setColor(new Color(241, 196, 15)); break; // Amarillo Mostaza
                case PVSM: g2d.setColor(new Color(52, 152, 219)); break; // Azul Claro
                default: g2d.setColor(Color.MAGENTA);
            }
            
            int mzX = offsetX + (int) (mz.getPositionX() * TAMANO_CELDA);
            int mzY = offsetY + (int) (mz.getPositionY() * TAMANO_CELDA);
            int mzW = (int) (mz.getWidth() * TAMANO_CELDA);
            int mzH = (int) (mz.getHeight() * TAMANO_CELDA);
            
            g2d.fillRect(mzX, mzY, mzW, mzH);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(mzX, mzY, mzW, mzH); // Bordes

            // Verificar si el jugador está en esta zona
            if (CollisionDetector.checkCollision(level.getCharacter(), mz)) {
                activeModality = mz.getModality();
            }

            // Dibujar icono arriba de la zona
            int iconX = mzX + mzW / 2;
            int iconY = mzY - 80;
            drawModalityIcon(g2d, mz.getModality(), iconX, iconY, activeModality == mz.getModality());
        }

        // Animación de zoom suave
        if (activeModality != null) {
            zoomLevel = Math.min(1.2, zoomLevel + 0.02);
        } else {
            zoomLevel = Math.max(1.0, zoomLevel - 0.02);
        }

        // 2. PAREDES (Solo se dibujan como bloques si NO es nivel de selección)
        if (!level.isSelectionLevel()) {
            for (Wall pared : level.getWalls()) {
                int px = offsetX + (int) (pared.getPositionX() * TAMANO_CELDA);
                int py = offsetY + (int) (pared.getPositionY() * TAMANO_CELDA);
                int pw = (int) (pared.getWidth() * TAMANO_CELDA);
                int ph = (int) (pared.getHeight() * TAMANO_CELDA);

                g2d.setColor(Color.DARK_GRAY);
                g2d.fillRect(px, py, pw, ph);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(px, py, pw, ph);
            }
        } else {
            // 2.1 DELINEADO CONTINUO PARA EL NIVEL DE SELECCIÓN
            Area mapBoundary = new Area();
            
            // Sumar tiles
            for (Tile t : level.getTiles()) {
                mapBoundary.add(new Area(new Rectangle(offsetX + (int)(t.getPositionX() * TAMANO_CELDA), offsetY + (int)(t.getPositionY() * TAMANO_CELDA), (int)(t.getWidth() * TAMANO_CELDA), (int)(t.getHeight() * TAMANO_CELDA))));
            }
            // Sumar checkpoints
            for (Checkpoint cp : level.getCheckpoints()) {
                mapBoundary.add(new Area(new Rectangle(offsetX + (int)(cp.getPositionX() * TAMANO_CELDA), offsetY + (int)(cp.getPositionY() * TAMANO_CELDA), (int)(cp.getWidth() * TAMANO_CELDA), (int)(cp.getHeight() * TAMANO_CELDA))));
            }
            // Sumar Goal
            if (goal != null) {
                 mapBoundary.add(new Area(new Rectangle(offsetX + (int)(goal.getPositionX() * TAMANO_CELDA), offsetY + (int)(goal.getPositionY() * TAMANO_CELDA), (int)(goal.getWidth() * TAMANO_CELDA), (int)(goal.getHeight() * TAMANO_CELDA))));
            }
            // Sumar Modality Zones
            for (ModalityZone mz : level.getModalityZones()) {
                 mapBoundary.add(new Area(new Rectangle(offsetX + (int)(mz.getPositionX() * TAMANO_CELDA), offsetY + (int)(mz.getPositionY() * TAMANO_CELDA), (int)(mz.getWidth() * TAMANO_CELDA), (int)(mz.getHeight() * TAMANO_CELDA))));
            }
            
            // Dibujar borde continuo
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2.5f)); // Grosor delineado
            g2d.draw(mapBoundary);
        }

        // 3. Monedas
        g2d.setColor(Color.YELLOW);
        for (Coin moneda : level.getCoins()) {
            if (!moneda.isCollected()) {
                int px = offsetX + (int) (moneda.getPositionX() * TAMANO_CELDA);
                int py = offsetY + (int) (moneda.getPositionY() * TAMANO_CELDA);
                int w = (int) (moneda.getWidth() * TAMANO_CELDA);
                int h = (int) (moneda.getHeight() * TAMANO_CELDA);
                g2d.fillOval(px, py, w, h);
            }
        }

        // 4. Enemigos Básicos
        g2d.setColor(Color.BLUE);
        for (Obstacle enemigo : level.getObstacles()) {
            int px = offsetX + (int) (enemigo.getPositionX() * TAMANO_CELDA);
            int py = offsetY + (int) (enemigo.getPositionY() * TAMANO_CELDA);
            int w = (int) (enemigo.getWidth() * TAMANO_CELDA);
            int h = (int) (enemigo.getHeight() * TAMANO_CELDA);
            g2d.fillOval(px, py, w, h);
        }

        // 5. Jugador o Animación de Explosión
        Character pJugador = level.getCharacter();

        if (pJugador.isExploding()) {
            g2d.setColor(Color.RED);
            for (Particle p : pJugador.getFragments()) {
                int px = offsetX + (int) (p.getPositionX() * TAMANO_CELDA);
                int py = offsetY + (int) (p.getPositionY() * TAMANO_CELDA);
                int pw = (int) (p.getWidth() * TAMANO_CELDA);
                int ph = (int) (p.getHeight() * TAMANO_CELDA);
                g2d.fillRect(px, py, pw, ph);
            }
        } else {
            if (pJugador.hasArmor()) {
                g2d.setColor(Color.GREEN); // Mostrar armadura si tiene
            } else if (pJugador instanceof BlueCharacter) {
                g2d.setColor(Color.CYAN);
            } else if (pJugador instanceof WhiteCharacter) {
                g2d.setColor(Color.WHITE);
            } else {
                g2d.setColor(Color.RED);
            }

            int fullW = (int) (pJugador.getWidth() * TAMANO_CELDA);
            int fullH = (int) (pJugador.getHeight() * TAMANO_CELDA);
            int pw = (int) (fullW * 0.9);
            int ph = (int) (fullH * 0.9);
            int px = offsetX + (int) (pJugador.getPositionX() * TAMANO_CELDA) + (fullW - pw) / 2;
            int py = offsetY + (int) (pJugador.getPositionY() * TAMANO_CELDA) + (fullH - ph) / 2;

            g2d.fillRect(px, py, pw, ph);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(px, py, pw, ph);
        }
    }

    private void drawModalityIcon(Graphics2D g2d, Modality modality, int centerX, int centerY, boolean isActive) {
        int size = 80;
        if (isActive) {
            size = (int) (80 * zoomLevel);
        }

        int x = centerX - size / 2;
        int y = centerY - size / 2;

        switch (modality) {
            case NORMAL:
                // Imagen para Modalidad Player
                if (modalityPlayerImage != null) {
                    g2d.drawImage(modalityPlayerImage, x, y, size, size, this);
                } else {
                    g2d.setColor(new Color(231, 76, 60));
                    g2d.fillOval(x, y, size, size);
                }
                break;
            case PVP:
                // Icono de espadas para PVP
                g2d.setColor(new Color(241, 196, 15));
                g2d.fillOval(x, y, size, size);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, size / 2));
                String pvpText = "⚔";
                int pvpTextW = g2d.getFontMetrics().stringWidth(pvpText);
                g2d.drawString(pvpText, centerX - pvpTextW / 2, centerY + size / 6);
                break;
            case PVSM:
                // Icono de robot para PVSM
                g2d.setColor(new Color(52, 152, 219));
                g2d.fillOval(x, y, size, size);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, size / 2));
                String pvsmText = "🤖";
                int pvsmTextW = g2d.getFontMetrics().stringWidth(pvsmText);
                g2d.drawString(pvsmText, centerX - pvsmTextW / 2, centerY + size / 6);
                break;
        }
    }
}
