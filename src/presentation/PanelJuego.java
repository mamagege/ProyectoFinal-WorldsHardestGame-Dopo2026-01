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
    private Font modalityDescFont;

    // Sistema de partículas estéticas para la capa de presentación
    private java.util.List<VisualParticle> selectionParticles = new java.util.ArrayList<>();
    private java.util.Random random = new java.util.Random();

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

        try {
            File modalFontFile = new File("src/resources/fonts/HelloWinds.otf");
            if (modalFontFile.exists()) {
                modalityDescFont = Font.createFont(Font.TRUETYPE_FONT, modalFontFile).deriveFont(20f);
            } else {
                modalityDescFont = new Font("Serif", Font.PLAIN, 20);
            }
        } catch (FontFormatException | IOException e) {
            modalityDescFont = new Font("Serif", Font.PLAIN, 20);
        }
    }

    private void cargarBackground() {
        try {
            backgroundImage = ImageIO.read(new File("src/resources/images/fondo_limbo.png"));
        } catch (IOException e) {
            System.err.println("Advertencia: No se pudo cargar fondo_limbo.png");
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

        // 1. Fondo de pantalla (Ocupa siempre el 100% de la ventana física)
        if (level.isSelectionLevel() && backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Dimensiones de dibujo
        int vWidth = getWidth();
        int vHeight = getHeight();

        // Cálculo de límites para centrado y posicionamiento
        int maxX = 0;
        int maxY = 0;
        for (Wall w : level.getWalls()) {
            if (w.getPositionX() > maxX)
                maxX = (int) w.getPositionX();
            if (w.getPositionY() > maxY)
                maxY = (int) w.getPositionY();
        }
        // Si no hay paredes (Selection level invisible), forzar dimensiones de
        // referencia
        if (maxX == 0) {
            maxX = 21;
            maxY = 13;
        }

        int mapWidth = (maxX + 1) * TAMANO_CELDA;
        int mapHeight = (maxY + 1) * TAMANO_CELDA;

        // Centrado dinámico usando el espacio real
        int offsetX = Math.max(20, (vWidth - mapWidth) / 2);
        int offsetY = Math.max(50, (vHeight - mapHeight) / 2);

        java.awt.geom.AffineTransform oldTransform = null;

        // SISTEMA RESPONSIVE TOTAL PARA EL HUB DE SELECCIÓN
        if (level.isSelectionLevel()) {
            // BLOQUEO DE PERSPECTIVA ABSOLUTO: Escalar el contexto para que la cuadrícula
            // lógica
            // (48x27) ocupe EXACTAMENTE el 100% de la ventana, alineándose con el fondo
            // estirado.
            oldTransform = g2d.getTransform();
            double scaleX = (double) vWidth / mapWidth;
            double scaleY = (double) vHeight / mapHeight;
            g2d.scale(scaleX, scaleY);

            // Al estar escalado a nivel ventana, las coordenadas locales nacen en 0,0
            offsetX = 0;
            offsetY = 0;
        }

        if (level.isSelectionLevel()) {
            // 1. RENDERIZAR TÍTULO GRANDE DE MODALIDAD (HellraiserBloody)
            if (titleFont != null) {
                g2d.setFont(titleFont.deriveFont(Font.PLAIN, 70f));
                String title = "SELECCIONA LA MODALIDAD";
                FontMetrics fm = g2d.getFontMetrics();
                int titleX = (vWidth - fm.stringWidth(title)) / 2;
                int titleY = 60;

                // Si estamos escalados, el texto debe colocarse en coordenadas lógicas
                if (level.isSelectionLevel()) {
                    titleX = (mapWidth - fm.stringWidth(title)) / 2;
                }

                // Sombras densas para legibilidad
                g2d.setColor(Color.BLACK);
                g2d.drawString(title, titleX + 4, titleY + 4);
                g2d.setColor(new Color(139, 0, 0)); // Rojo sangre oscuro
                g2d.drawString(title, titleX, titleY);
            }

            // 2. RENDERIZAR TEXTOS SOBRE LOS PORTALES (HelloWinds)
            if (modalityDescFont != null) {
                g2d.setFont(modalityDescFont.deriveFont(Font.BOLD, 32f));
                for (ModalityZone mz : level.getModalityZones()) {
                    String label = "";
                    switch (mz.getModality()) {
                        case PLAYER:
                            label = "PLAYER";
                            break;
                        case PVP:
                            label = "PLAYER VS PLAYER";
                            break;
                        case PVSM:
                            label = "PLAYER VS MACHINE";
                            break;
                    }
                    int mzX = offsetX + (int) (mz.getPositionX() * TAMANO_CELDA);
                    int mzY = offsetY + (int) (mz.getPositionY() * TAMANO_CELDA);
                    int mzW = (int) (mz.getWidth() * TAMANO_CELDA);

                    FontMetrics fmLabel = g2d.getFontMetrics();
                    int labX = mzX + (mzW / 2) - (fmLabel.stringWidth(label) / 2);
                    int labY = mzY - 10; // Posicionado encima del portal

                    // Sombra robusta
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(label, labX + 2, labY + 2);
                    // Color principal Blanco para contraste sobre la selva
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(label, labX, labY);
                }
            }

            // 3. SISTEMA DE PARTÍCULAS DINÁMICAS (FLARES FLOTANTES)
            for (ModalityZone mz : level.getModalityZones()) {
                Color pColor;
                switch (mz.getModality()) {
                    case PLAYER:
                        pColor = new Color(231, 76, 60);
                        break;
                    case PVP:
                        pColor = new Color(46, 204, 113); // Verde Místico Esmeralda
                        break;
                    case PVSM:
                        pColor = new Color(52, 152, 219);
                        break;
                    default:
                        pColor = Color.WHITE;
                }
                // ALTA INTENSIDAD: Spawn aumentado al 80% para efecto mágico rebosante y
                // llamativo
                if (random.nextDouble() < 0.8) {
                    double pX = offsetX + (mz.getPositionX() * TAMANO_CELDA)
                            + random.nextDouble() * (mz.getWidth() * TAMANO_CELDA);
                    double pY = offsetY + (mz.getPositionY() * TAMANO_CELDA)
                            + random.nextDouble() * (mz.getHeight() * TAMANO_CELDA);

                    // Movimiento más caótico, enérgico y con dispersión lateral (vX)
                    double vX = (random.nextDouble() - 0.5) * 1.2;
                    double vY = -(random.nextDouble() * 1.2 + 0.5); // Ascenso significativamente más veloz

                    // Vida útil expandida para crear ráfagas densas de energía
                    selectionParticles.add(new VisualParticle(pX, pY, vX, vY, 60 + random.nextInt(60), pColor));
                }
            }

            // Renderizado con Transparencia Gradual (Fade out)
            java.awt.Composite originalComposite = g2d.getComposite();
            java.util.Iterator<VisualParticle> iter = selectionParticles.iterator();
            while (iter.hasNext()) {
                VisualParticle p = iter.next();
                float ratio = (float) p.life / (float) p.maxLife;
                float safeAlpha = Math.max(0.0f, Math.min(1.0f, ratio));
                g2d.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, safeAlpha));
                g2d.setColor(p.color);
                g2d.fillOval((int) p.x, (int) p.y, p.size, p.size);

                // Actualización de física visual
                if (!p.update()) {
                    iter.remove();
                }
            }
            // Restaurar Composite original para no afectar el resto del renderizado
            g2d.setComposite(originalComposite);
        } else {
            // Garbage Collector preventivo de efectos visuales
            if (!selectionParticles.isEmpty()) {
                selectionParticles.clear();
            }
        }

        // 0. Baldosas (Tiles) del Damero - OMITIR EN NIVEL SELECCIÓN
        if (!level.isSelectionLevel()) {
            for (Tile tile : level.getTiles()) {
                g2d.setColor(Color.decode(tile.getColorHex()));
                g2d.fillRect(offsetX + (int) (tile.getPositionX() * TAMANO_CELDA),
                        offsetY + (int) (tile.getPositionY() * TAMANO_CELDA),
                        (int) (tile.getWidth() * TAMANO_CELDA),
                        (int) (tile.getHeight() * TAMANO_CELDA));
            }
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

        // 1.1 Modality Zones (Invisibles en render, solo lógica de colisión)
        // 1.1 Modality Zones (Invisibles en render en el Hub de Selección, visibles en
        // niveles estándar)
        if (!level.isSelectionLevel()) {
            for (ModalityZone mz : level.getModalityZones()) {
                switch (mz.getModality()) {
                    case PLAYER:
                        g2d.setColor(new Color(231, 76, 60));
                        break;
                    case PVP:
                        g2d.setColor(new Color(46, 204, 113)); // Verde Místico Esmeralda
                        break;
                    case PVSM:
                        g2d.setColor(new Color(52, 152, 219));
                        break;
                    default:
                        g2d.setColor(Color.MAGENTA);
                }

                int mzX = offsetX + (int) (mz.getPositionX() * TAMANO_CELDA);
                int mzY = offsetY + (int) (mz.getPositionY() * TAMANO_CELDA);
                int mzW = (int) (mz.getWidth() * TAMANO_CELDA);
                int mzH = (int) (mz.getHeight() * TAMANO_CELDA);

                g2d.fillRect(mzX, mzY, mzW, mzH);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(mzX, mzY, mzW, mzH);
            }
        }

        // La animación de zoom y delineado se omite para el nivel de selección puro
        // imagen
        activeModality = null;
        for (ModalityZone mz : level.getModalityZones()) {
            if (CollisionDetector.checkCollision(level.getCharacter(), mz)) {
                activeModality = mz.getModality();
            }
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

            // Activar antialiasing para garantizar geometría fluida y centrado sub-pixel
            // perfecto
            Object antialiasHint = g2d.getRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING);
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

            // --- DETALLE COSMÉTICO: ROSTRO ASUSTADO ---
            int eyeSize = (int) (pw * 0.28);
            int eyeY = py + (int) (ph * 0.22);

            // Ojo Izquierdo (Blanco + Borde)
            g2d.setColor(Color.WHITE);
            int eyeLeftX = px + (int) (pw * 0.15);
            g2d.fillOval(eyeLeftX, eyeY, eyeSize, eyeSize);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(eyeLeftX, eyeY, eyeSize, eyeSize);

            // Ojo Derecho (Blanco + Borde)
            g2d.setColor(Color.WHITE);
            int eyeRightX = px + pw - (int) (pw * 0.15) - eyeSize;
            g2d.fillOval(eyeRightX, eyeY, eyeSize, eyeSize);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(eyeRightX, eyeY, eyeSize, eyeSize);

            // Pupilas (ESTRICTAMENTE CENTRADAS MATEMÁTICAMENTE)
            int pupilSize = Math.max(2, (int) Math.round(eyeSize / 3.0));
            int pOffset = (int) Math.round((eyeSize - pupilSize) / 2.0);
            g2d.fillOval(eyeLeftX + pOffset, eyeY + pOffset, pupilSize, pupilSize);
            g2d.fillOval(eyeRightX + pOffset, eyeY + pOffset, pupilSize, pupilSize);

            // Boca Triste (Curva muy pronunciada y más baja)
            g2d.setColor(Color.BLACK);
            java.awt.Stroke oldStroke = g2d.getStroke();
            g2d.setStroke(new java.awt.BasicStroke(2.8f));
            int mouthW = (int) (pw * 0.28);
            int mouthH = (int) (ph * 0.30);
            int mouthX = px + (pw - mouthW) / 2;
            int mouthY = eyeY + eyeSize + (int) (ph * 0.15);
            g2d.drawArc(mouthX, mouthY, mouthW, mouthH, 0, 180);

            // Restaurar estados de renderizado
            g2d.setStroke(oldStroke);
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    antialiasHint != null ? antialiasHint : java.awt.RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        }

        // Restaurar el contexto a su transformación nativa al terminar el frame
        if (oldTransform != null) {
            g2d.setTransform(oldTransform);
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
            case PLAYER:
                if (modalityPlayerImage != null) {
                    g2d.drawImage(modalityPlayerImage, x, y, size, size, this);
                } else {
                    g2d.setColor(new Color(231, 76, 60));
                    g2d.fillOval(x, y, size, size);
                }
                break;
            case PVP:
                g2d.setColor(new Color(241, 196, 15));
                g2d.fillOval(x, y, size, size);
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, size / 3));
                String pvpText = "PVP";
                int pvpTextW = g2d.getFontMetrics().stringWidth(pvpText);
                g2d.drawString(pvpText, centerX - pvpTextW / 2, centerY + size / 10);
                break;
            case PVSM:
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

    private static class VisualParticle {
        double x, y, vx, vy;
        int life, maxLife;
        Color color;
        int size;

        public VisualParticle(double x, double y, double vx, double vy, int maxLife, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.maxLife = maxLife;
            this.life = maxLife;
            this.color = color;
            this.size = 6 + new java.util.Random().nextInt(9); // Partículas un 150% más grandes y radiantes
        }

        public boolean update() {
            x += vx;
            y += vy;
            life--;
            return life > 0;
        }
    }
}
