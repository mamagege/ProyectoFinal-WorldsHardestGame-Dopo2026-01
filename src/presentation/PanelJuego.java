package presentation;

import domain.*;
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
import java.awt.RenderingHints;
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
    private GameWHGGUI gameOrchestrator;
    private final int TAMANO_CELDA = 56; // 56px por unidad lógica → personaje rojo 1.0 ocupa ~75% de la baldosa
    private JLabel labelMuertes;
    private JLabel labelNivel;
    private JPanel barraSuperior;
    private Font gameFont;
    private double zoomLevel = 1.0;
    private Modality activeModality = null;
    private Image backgroundImage;        // Fondo del nivel de selección de modalidad
    private Image level1BackgroundImage;  // Fondo del nivel 1 real
    private Image modalityPlayerImage;
    private Font titleFont;
    private Font modalityDescFont;
    private ControladorJuego controlador; // Referencia directa para sincronización continua de estado del teclado

    // Sistema de partículas estéticas para la capa de presentación
    private java.util.List<VisualParticle> selectionParticles = new java.util.ArrayList<>();
    private java.util.Random random = new java.util.Random();

    public PanelJuego(VentanaPrincipal ventana, GameWHGGUI gameOrchestrator) {
        this.ventana = ventana;
        this.gameOrchestrator = gameOrchestrator;
        setLayout(new BorderLayout());
        setBackground(Color.decode("#E6E6FA"));

        barraSuperior = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        barraSuperior.setBackground(new Color(0, 0, 0, 100));
        barraSuperior.setOpaque(false);
        barraSuperior.setFocusable(false);

        JButton btnMenu = new JButton("MENU") {
            private boolean isHovered = false;
            
            {
                setFocusable(false);
                setOpaque(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setForeground(Color.WHITE);
                setFont(new Font("Arial", Font.BOLD, 14));
                setPreferredSize(new java.awt.Dimension(90, 35));
                
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo del botón: Oscuro semitransparente
                if (isHovered) {
                    g2.setColor(new Color(40, 30, 70, 210)); // Ligeramente más claro al hover
                } else {
                    g2.setColor(new Color(20, 15, 40, 180));
                }
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                
                // Borde suave con glow (morado/lavanda)
                if (isHovered) {
                    g2.setColor(new Color(200, 180, 255, 255)); // Brillo intenso al hover
                    g2.setStroke(new BasicStroke(2.0f));
                } else {
                    g2.setColor(new Color(150, 100, 220, 150));
                    g2.setStroke(new BasicStroke(1.5f));
                }
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btnMenu.addActionListener(e -> ventana.mostrarPanel("MENU"));

        labelNivel = new JLabel("Nivel: 1/30", JLabel.CENTER);
        labelNivel.setFont(new Font("Arial", Font.BOLD, 16));
        labelNivel.setForeground(Color.WHITE); // Asegura visibilidad sobre fondo oscuro
        labelNivel.setFocusable(false);

        labelMuertes = new JLabel("Deaths: 0  ");
        labelMuertes.setFont(new Font("Arial", Font.BOLD, 16));
        labelMuertes.setForeground(Color.WHITE);
        labelMuertes.setFocusable(false);

        // Wrappers seguros para evitar robos de foco del mouse
        JPanel btnWrap = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 10));
        btnWrap.setOpaque(false);
        btnWrap.setFocusable(false);
        btnWrap.add(btnMenu);

        JPanel centerWrap = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 10));
        centerWrap.setOpaque(false);
        centerWrap.setFocusable(false);
        centerWrap.add(labelNivel);

        JPanel lblWrap = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 15, 10));
        lblWrap.setOpaque(false);
        lblWrap.setFocusable(false);
        lblWrap.add(labelMuertes);

        barraSuperior.add(btnWrap, BorderLayout.WEST);
        barraSuperior.add(centerWrap, BorderLayout.CENTER);
        barraSuperior.add(lblWrap, BorderLayout.EAST);

        add(barraSuperior, BorderLayout.NORTH);

        this.controlador = new ControladorJuego(this, gameOrchestrator);
        addKeyListener(this.controlador);
        setFocusable(true);

        cargarFuentes();
        cargarBackground();
    }

    private void cargarFuentes() {
        try {
            try {
                File fontFile = new File("src/resources/fonts/Luvable.ttf");
                if (fontFile.exists()) {
                    gameFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(28f);
                } else {
                    throw new GameWHGException(GameWHGException.ERROR_CARGA_RECURSO);
                }

                File titleFontFile = new File("src/resources/fonts/HellraiserBloody.ttf");
                if (titleFontFile.exists()) {
                    titleFont = Font.createFont(Font.TRUETYPE_FONT, titleFontFile).deriveFont(50f);
                } else {
                    throw new GameWHGException(GameWHGException.ERROR_CARGA_RECURSO);
                }

                File modalFontFile = new File("src/resources/fonts/HelloWinds.otf");
                if (modalFontFile.exists()) {
                    modalityDescFont = Font.createFont(Font.TRUETYPE_FONT, modalFontFile).deriveFont(20f);
                } else {
                    throw new GameWHGException(GameWHGException.ERROR_CARGA_RECURSO);
                }
            } catch (FontFormatException | IOException e) {
                throw new GameWHGException(GameWHGException.ERROR_CARGA_RECURSO);
            }
        } catch (GameWHGException ex) {
            domain.Log.record(ex);
            System.err.println("Error de recurso: " + ex.getMessage());
            gameFont = new Font("Arial", Font.BOLD, 28);
            titleFont = new Font("Arial", Font.BOLD, 50);
            modalityDescFont = new Font("Serif", Font.PLAIN, 20);
        }
    }

    private void cargarBackground() {
        try {
            try {
                backgroundImage = ImageIO.read(new File("src/resources/images/fondo_limbo.png"));
                level1BackgroundImage = ImageIO.read(new File("src/resources/images/nivel1_fondo.png"));
                if (backgroundImage == null || level1BackgroundImage == null) {
                    throw new GameWHGException(GameWHGException.ERROR_CARGA_RECURSO);
                }
            } catch (IOException e) {
                throw new GameWHGException(GameWHGException.ERROR_CARGA_RECURSO);
            }
        } catch (GameWHGException ex) {
            domain.Log.record(ex);
            System.err.println("Error de recurso: " + ex.getMessage());
        }
    }

    public void resetKeyboard() {
        if (controlador != null) {
            controlador.resetKeyStates();
        }
    }

    public void actualizarInterfaz() {
        // GARANTÍA ABSOLUTA DE FOCO DE TECLADO:
        // Si este panel está visible pero ha perdido el foco por transiciones de Swing,
        // lo reclamamos en caliente al vuelo. Soluciona el congelamiento o lag de 5 segundos.
        if (isShowing() && !hasFocus()) {
            requestFocusInWindow();
        }

        // Forzar refresco de velocidad en cada frame. Esto soluciona el lag al cambiar de nivel
        // copiando instantáneamente el estado de las teclas pulsadas al nuevo objeto personaje.
        if (controlador != null) {
            controlador.updateVelocity();
        }

        Level level = gameOrchestrator.getCurrentLevel();
        if (level != null) {
            boolean isSel = level.isSelectionLevel();
            
            // La barra superior ahora siempre está visible para permitir el acceso al botón MENU
            if (!barraSuperior.isVisible()) {
                barraSuperior.setVisible(true);
                revalidate(); // Forzar re-layout
            }

            // Ocultar o mostrar las estadísticas dinámicamente para no ensuciar el Hub de Selección
            if (labelMuertes.isVisible() == isSel) {
                labelMuertes.setVisible(!isSel);
            }
            if (labelNivel.isVisible() == isSel) {
                labelNivel.setVisible(!isSel);
            }

            if (!isSel) {
                labelMuertes.setText("Deaths: " + level.getCharacter().getDeaths() + "  ");
                labelNivel.setText("Nivel: " + gameOrchestrator.getCurrentLevelIndex() + "/30");
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
        } else if (!level.isSelectionLevel()) {
            // Fondo del nivel real: nivel1_fondo.jpg para el nivel 1 (y futuros niveles)
            Image nivelFondo = level1BackgroundImage; // Por ahora solo nivel 1 tiene fondo específico
            if (nivelFondo != null) {
                g2d.drawImage(nivelFondo, 0, 0, getWidth(), getHeight(), this);
            }
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
            // lógica (48x27) ocupe EXACTAMENTE el 100% de la ventana
            oldTransform = g2d.getTransform();
            double scaleX = (double) vWidth / mapWidth;
            double scaleY = (double) vHeight / mapHeight;
            g2d.scale(scaleX, scaleY);

            // Al estar escalado a nivel ventana, las coordenadas locales nacen en 0,0
            offsetX = 0;
            offsetY = 0;
        } else {
            // Borde rojo inteligente: traza solo los bordes que separan baldosas de espacios vacios/paredes.
            // Esto produce un contorno que sigue exactamente la forma del camino en vez de un simple rectangulo.
            if (!level.getTiles().isEmpty()) {
                // Construir set de posiciones de baldosa para lookup O(1)
                java.util.Set<Long> tileSet = new java.util.HashSet<>();
                for (Tile t : level.getTiles()) {
                    int tx = (int) t.getPositionX();
                    int ty = (int) t.getPositionY();
                    tileSet.add((long) tx << 16 | (ty & 0xFFFF));
                }

                java.awt.geom.Path2D.Float borderPath = new java.awt.geom.Path2D.Float();

                for (Tile t : level.getTiles()) {
                    int tx = (int) t.getPositionX();
                    int ty = (int) t.getPositionY();
                    int px = offsetX + tx * TAMANO_CELDA;
                    int py = offsetY + ty * TAMANO_CELDA;
                    int cs = TAMANO_CELDA;

                    // Borde superior: si la celda de arriba no es baldosa
                    if (!tileSet.contains((long) tx << 16 | ((ty - 1) & 0xFFFF))) {
                        borderPath.moveTo(px, py);
                        borderPath.lineTo(px + cs, py);
                    }
                    // Borde inferior: si la celda de abajo no es baldosa
                    if (!tileSet.contains((long) tx << 16 | ((ty + 1) & 0xFFFF))) {
                        borderPath.moveTo(px, py + cs);
                        borderPath.lineTo(px + cs, py + cs);
                    }
                    // Borde izquierdo: si la celda de la izquierda no es baldosa
                    if (!tileSet.contains((long) (tx - 1) << 16 | (ty & 0xFFFF))) {
                        borderPath.moveTo(px, py);
                        borderPath.lineTo(px, py + cs);
                    }
                    // Borde derecho: si la celda de la derecha no es baldosa
                    if (!tileSet.contains((long) (tx + 1) << 16 | (ty & 0xFFFF))) {
                        borderPath.moveTo(px + cs, py);
                        borderPath.lineTo(px + cs, py + cs);
                    }
                }

                java.awt.Stroke prevStroke = g2d.getStroke();
                g2d.setColor(new Color(210, 30, 30));
                g2d.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.draw(borderPath);
                g2d.setStroke(prevStroke);
            }
        }

        if (level.isSelectionLevel()) {
            // 1. RENDERIZAR TITULO GRANDE DE MODALIDAD (HellraiserBloody)
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

            // 3. SISTEMA DE PARTICULAS DINAMICAS (FLARES FLOTANTES)
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

        // 2. PAREDES — no se renderizan visualmente; la física sigue activa en el dominio.
        // Solo en el nivel de selección seguimos sin dibujarlas.

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

        // 5. Jugadores o Animaciones de Explosión
        java.util.List<Character> characters = level.getTablero().getCharacters();
        if (characters != null) {
            for (Character pJugador : characters) {
                if (pJugador == null) continue;
                
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
                    int fullW = (int) (pJugador.getWidth() * TAMANO_CELDA);
                    int fullH = (int) (pJugador.getHeight() * TAMANO_CELDA);
                    int pw = fullW;
                    int ph = fullH;
                    int px = offsetX + (int) (pJugador.getPositionX() * TAMANO_CELDA);
                    int py = offsetY + (int) (pJugador.getPositionY() * TAMANO_CELDA);

                    // Borde grueso negro (Fondo)
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(px, py, pw, ph);

                    // Interior de color
                    int border = Math.max(2, pw / 16);
                    if (pJugador.hasArmor()) {
                        g2d.setColor(new Color(50, 190, 80)); // GREEN
                    } else if (pJugador instanceof BlueCharacter) {
                        g2d.setColor(new Color(60, 130, 220)); // BLUE
                    } else if (pJugador instanceof WhiteCharacter) {
                        g2d.setColor(Color.WHITE); // WHITE
                    } else {
                        g2d.setColor(new Color(220, 60, 60)); // RED
                    }
                    g2d.fillRect(px + border, py + border, pw - 2*border, ph - 2*border);

                    // Activar antialiasing para los ojos
                    Object antialiasHint = g2d.getRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING);
                    g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                    int eyeRadius = (int) (pw * 0.16);
                    int eyeY = py + (int) (ph * 0.35);

                    // Centros de los ojos
                    int eyeLeftCX = px + (int) (pw * 0.35);
                    int eyeRightCX = px + (int) (pw * 0.65);

                    // Dibujar escleróticas (blanco)
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(eyeLeftCX - eyeRadius, eyeY - eyeRadius, eyeRadius * 2, eyeRadius * 2);
                    g2d.fillOval(eyeRightCX - eyeRadius, eyeY - eyeRadius, eyeRadius * 2, eyeRadius * 2);
                    
                    // Dibujar bordes de los ojos
                    g2d.setColor(Color.BLACK);
                    java.awt.Stroke oldStroke = g2d.getStroke();
                    g2d.setStroke(new java.awt.BasicStroke(1.5f));
                    g2d.drawOval(eyeLeftCX - eyeRadius, eyeY - eyeRadius, eyeRadius * 2, eyeRadius * 2);
                    g2d.drawOval(eyeRightCX - eyeRadius, eyeY - eyeRadius, eyeRadius * 2, eyeRadius * 2);
                    g2d.setStroke(oldStroke);

                    // Dibujar pupilas (Mirando al frente de forma neutral/asustada pero estática)
                    int pupilRadius = (int) (eyeRadius * 0.45);
                    g2d.setColor(Color.BLACK);
                    g2d.fillOval(eyeLeftCX - pupilRadius, eyeY - pupilRadius, pupilRadius * 2, pupilRadius * 2);
                    g2d.fillOval(eyeRightCX - pupilRadius, eyeY - pupilRadius, pupilRadius * 2, pupilRadius * 2);

                    // Restaurar estado de renderizado
                    g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                            antialiasHint != null ? antialiasHint : java.awt.RenderingHints.VALUE_ANTIALIAS_DEFAULT);
                }
            }
        }
        
        // 6. Renderizar Tutorial flotante en el nivel 1
        if (!level.isSelectionLevel() && gameOrchestrator.getCurrentLevelIndex() == 1) {
            dibujarTutorial(g2d, vWidth, vHeight, offsetX, offsetY, mapWidth, mapHeight);
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
                g2d.setColor(new Color(46, 204, 113)); // Verde Místico Esmeralda
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

    private void dibujarTutorial(Graphics2D g2d, int vWidth, int vHeight, int offsetX, int offsetY, int mapWidth, int mapHeight) {
        // Definir el área del letrero del tutorial
        int tutW = 340;
        int tutH = 360;
        int tutX, tutY;

        // Posicionarlo dinámicamente a la izquierda si hay espacio, sino a la derecha, o fallback superior
        if (offsetX > 360) {
            tutX = (offsetX - tutW) / 2;
            tutY = offsetY + (mapHeight - tutH) / 2;
        } else if (vWidth - (offsetX + mapWidth) > 360) {
            tutX = offsetX + mapWidth + (vWidth - (offsetX + mapWidth) - tutW) / 2;
            tutY = offsetY + (mapHeight - tutH) / 2;
        } else {
            // Si no hay suficiente espacio lateral, colocarlo compacto
            tutX = 20;
            tutY = 85;
            tutW = 320;
            tutH = 340;
        }

        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 1. Fondo Premium translúcido tipo cristal oscuro
        g.setColor(new Color(20, 15, 35, 220));
        g.fillRoundRect(tutX, tutY, tutW, tutH, 20, 20);
        
        // Glow cian neón elegante
        g.setColor(new Color(0, 220, 255, 160));
        g.setStroke(new BasicStroke(2.5f));
        g.drawRoundRect(tutX + 2, tutY + 2, tutW - 4, tutH - 4, 18, 18);
        
        // 2. Título
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        String title = "★ TUTORIAL ★";
        g.setColor(new Color(0, 255, 200));
        g.drawString(title, tutX + (tutW - fm.stringWidth(title)) / 2, tutY + 45);
        
        // Línea divisoria sutil
        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(255, 255, 255, 60));
        g.drawLine(tutX + 30, tutY + 62, tutX + tutW - 30, tutY + 62);
        
        // 3. Cuerpo de Instrucciones
        int textX = tutX + 25;
        int textY = tutY + 100;
        
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(new Color(220, 220, 255));
        
        // Movimiento
        g.drawString("MOVIMIENTO:", textX, textY);
        
        // Teclado Visual WASD + Flechas
        dibujarTecladoGrafico(g, textX + 125, textY - 18);
        
        textY += 75;
        
        g.setFont(new Font("Arial", Font.PLAIN, 15));
        
        // Regla 1: Moneda
        g.setColor(Color.YELLOW);
        g.fillOval(textX, textY, 14, 14);
        g.setColor(Color.WHITE);
        g.drawString("Recoge la Moneda", textX + 22, textY + 13);
        textY += 35;
        
        // Regla 2: Meta
        g.setColor(new Color(142, 210, 127));
        g.fillRect(textX, textY, 14, 14);
        g.setColor(Color.WHITE);
        g.drawString("Llega sano a la Meta (Zona Verde)", textX + 22, textY + 13);
        textY += 35;
        
        // Regla 3: Obstáculos
        g.setColor(Color.BLUE);
        g.fillOval(textX, textY, 14, 14);
        g.setColor(new Color(255, 110, 110)); // Rojo coral brillante
        g.drawString("¡NO toques los círculos azules!", textX + 22, textY + 13);
        
        // Footer
        textY += 55;
        g.setFont(new Font("Arial", Font.ITALIC, 13));
        g.setColor(new Color(180, 180, 180));
        String info = "¡Demuestra tu destreza y sobrevive!";
        g.drawString(info, tutX + (tutW - g.getFontMetrics().stringWidth(info)) / 2, textY);
        
        g.dispose();
    }

    private void dibujarTecladoGrafico(Graphics2D g, int x, int y) {
        int keySize = 17;
        
        // 1. WASD
        g.setColor(new Color(255, 255, 255, 40));
        g.fillRoundRect(x + keySize + 2, y, keySize, keySize, 4, 4); // W
        g.fillRoundRect(x, y + keySize + 2, keySize, keySize, 4, 4); // A
        g.fillRoundRect(x + keySize + 2, y + keySize + 2, keySize, keySize, 4, 4); // S
        g.fillRoundRect(x + (keySize + 2) * 2, y + keySize + 2, keySize, keySize, 4, 4); // D
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 10));
        g.drawString("W", x + keySize + 6, y + 12);
        g.drawString("A", x + 4, y + keySize + 14);
        g.drawString("S", x + keySize + 6, y + keySize + 14);
        g.drawString("D", x + (keySize + 2) * 2 + 4, y + keySize + 14);

        // Divisor "o"
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(new Color(200, 200, 255));
        g.drawString("o", x + 62, y + keySize + 4);

        // 2. Flechas
        int fx = x + 80;
        g.setColor(new Color(255, 255, 255, 40));
        g.fillRoundRect(fx + keySize + 2, y, keySize, keySize, 4, 4); // Up
        g.fillRoundRect(fx, y + keySize + 2, keySize, keySize, 4, 4); // Left
        g.fillRoundRect(fx + keySize + 2, y + keySize + 2, keySize, keySize, 4, 4); // Down
        g.fillRoundRect(fx + (keySize + 2) * 2, y + keySize + 2, keySize, keySize, 4, 4); // Right
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 10));
        g.drawString("▲", fx + keySize + 4, y + 12);
        g.drawString("◀", fx + 3, y + keySize + 14);
        g.drawString("▼", fx + keySize + 4, y + keySize + 14);
        g.drawString("▶", fx + (keySize + 2) * 2 + 2, y + keySize + 14);
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
