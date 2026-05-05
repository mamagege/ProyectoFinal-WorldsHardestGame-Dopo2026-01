package presentation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa el menú de inicio del juego con efecto Glitch alternante.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class PanelMenu extends JPanel {
    private VentanaPrincipal ventana;

    // Recursos
    private Image backgroundImage;
    private Image glitchBackgroundImage;
    private Image normalImage; // estrella.png
    private Image glitchImage; // calavera.png
    private Font normalFont; // Luvable.ttf
    private Font glitchFont; // HorrorCorps.ttf

    // Animación y Estado
    private Timer animationTimer;
    private long startTime;
    private boolean isGlitchActive = false;
    private Timer glitchTimer;
    private Timer fastGlitchTimer;

    // Contenedor de botones
    private List<JButton> menuButtons = new ArrayList<>();

    public PanelMenu(VentanaPrincipal ventana) {
        this.ventana = ventana;

        // 1. Carga de Recursos (Fuentes e Imágenes)
        try {
            backgroundImage = ImageIO.read(new File("src/resources/images/fondo_arcoiris.png"));
            glitchBackgroundImage = ImageIO.read(new File("src/resources/images/fondo_muro.png"));
        } catch (IOException e) {
            System.err.println("Advertencia: No se pudieron cargar los fondos");
        }

        try {
            normalImage = ImageIO.read(new File("src/resources/images/estrella.png"));
            glitchImage = ImageIO.read(new File("src/resources/images/calavera.png"));
        } catch (IOException e) {
            System.err.println("Advertencia: No se pudieron cargar estrella.png o calavera.png");
        }

        try {
            File fontLuvable = new File("src/resources/fonts/Luvable.ttf");
            normalFont = Font.createFont(Font.TRUETYPE_FONT, fontLuvable).deriveFont(60f);
        } catch (FontFormatException | IOException e) {
            System.err.println("Advertencia: No se pudo cargar Luvable.ttf");
            normalFont = new Font(Font.SANS_SERIF, Font.BOLD, 60);
        }

        try {
            File fontHorror = new File("src/resources/fonts/HorrorCorps.ttf");
            glitchFont = Font.createFont(Font.TRUETYPE_FONT, fontHorror).deriveFont(60f);
        } catch (FontFormatException | IOException e) {
            System.err.println("Advertencia: No se pudo cargar HorrorCorps.ttf");
            glitchFont = new Font(Font.SANS_SERIF, Font.BOLD, 60);
        }

        // 2. Configurar Layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(0, 0, 40, 0);

        // Panel contenedor para los botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 50, 15));
        buttonPanel.setOpaque(false);

        // 3. Instanciar botones dinámicos
        JButton btnNuevaPartida = crearBoton("NUEVA PARTIDA");
        JButton btnContinuar = crearBoton("CONTINUAR");
        JButton btnOpciones = crearBoton("OPCIONES");
        JButton btnSalir = crearBoton("SALIR");

        // Configurar acciones
        btnNuevaPartida.addActionListener(e -> ventana.mostrarPanel("JUEGO"));
        btnSalir.addActionListener(e -> System.exit(0));

        // Añadir botones al panel
        buttonPanel.add(btnNuevaPartida);
        buttonPanel.add(btnOpciones);
        buttonPanel.add(btnContinuar);
        buttonPanel.add(btnSalir);

        // Registrar botones para actualizarlos globalmente si es necesario
        menuButtons.add(btnNuevaPartida);
        menuButtons.add(btnOpciones);
        menuButtons.add(btnContinuar);
        menuButtons.add(btnSalir);

        add(buttonPanel, gbc);

        // 4. Lógica de Estado y Temporizadores
        fastGlitchTimer = new Timer(1150, e -> { // Dura 1 segundo más (1150ms)
            isGlitchActive = false;
            repaint(); // Redibuja el panel y por ende los botones transparentes
            for (JButton btn : menuButtons) {
                btn.repaint(); // Asegurar repintado individual de los botones
            }
        });
        fastGlitchTimer.setRepeats(false);

        glitchTimer = new Timer(2500, e -> { // ~2.5s entre parpadeos
            isGlitchActive = true;
            repaint();
            for (JButton btn : menuButtons) {
                btn.repaint();
            }
            fastGlitchTimer.start();
        });
        glitchTimer.start();

        // Iniciar timer para la animación a 60 FPS (~16ms)
        startTime = System.currentTimeMillis();
        animationTimer = new Timer(16, e -> repaint());
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        // Renderizado de alta calidad (Anti-aliasing activado)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String title = "THE DOPO HARDEST GAME";

        // 5. Renderizado Condicional
        if (!isGlitchActive) {
            // ESTADO NORMAL
            if (backgroundImage != null) {
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g2d.setColor(new Color(15, 15, 15));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

            if (normalImage != null) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                double amplitud = 8.0;
                double velocidad = 400.0;
                int yOffset = (int) (Math.sin(elapsedTime / velocidad) * amplitud);

                int drawHeight = 220;
                int drawWidth = (normalImage.getWidth(null) * drawHeight) / Math.max(1, normalImage.getHeight(null));

                int x = (getWidth() - drawWidth) / 2;
                int y = 40 + yOffset;
                g2d.drawImage(normalImage, x, y, drawWidth, drawHeight, this);
            }

            // Título Normal: Luvable.ttf, Blanco con relieve Negro
            g2d.setFont(normalFont);
            FontMetrics fm = g2d.getFontMetrics();
            int startX = (getWidth() - fm.stringWidth(title)) / 2;
            int textY = 320;

            // Relieve negro (8 direcciones usando grosor de 2px para mayor visibilidad)
            g2d.setColor(Color.BLACK);
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    if (dx != 0 || dy != 0) {
                        g2d.drawString(title, startX + dx, textY + dy);
                    }
                }
            }
            // Texto principal blanco
            g2d.setColor(Color.WHITE);
            g2d.drawString(title, startX, textY);

        } else {
            // ESTADO GLITCH
            if (glitchBackgroundImage != null) {
                g2d.drawImage(glitchBackgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g2d.setColor(new Color(15, 0, 0)); // Fondo contrastante/oscuro fallback
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

            // Estática macabra sutil
            g2d.setColor(new Color(0, 0, 0, 180));
            for (int i = 0; i < 40; i++) {
                g2d.fillRect((int) (Math.random() * getWidth()), (int) (Math.random() * getHeight()),
                        (int) (Math.random() * getWidth()), (int) (Math.random() * 15));
            }

            if (glitchImage != null) {
                int drawHeight = 250;
                int drawWidth = (glitchImage.getWidth(null) * drawHeight) / Math.max(1, glitchImage.getHeight(null));
                int x = (getWidth() - drawWidth) / 2 + (int) (Math.random() * 30 - 15);
                int y = 50 + (int) (Math.random() * 20 - 10);
                g2d.drawImage(glitchImage, x, y, drawWidth, drawHeight, this);
            }

            // Título Glitch: HorrorCorps.ttf, BOLD, Blanco puro sin relieve
            g2d.setFont(glitchFont.deriveFont(Font.BOLD, 60f));
            FontMetrics fm = g2d.getFontMetrics();
            int startX = (getWidth() - fm.stringWidth(title)) / 2 + (int) (Math.random() * 20 - 10);
            int textY = 320 + (int) (Math.random() * 10 - 5);

            g2d.setColor(Color.WHITE);
            g2d.drawString(title, startX, textY);
        }

        g2d.dispose();
    }

    /**
     * Crea un botón dinámico sobrescribiendo su paintComponent para adaptarse
     * al estado del glitch internamente, encapsulando la lógica visual.
     */
    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            public Dimension getPreferredSize() {
                // Prevenir cortes dándole al Layout el tamaño real del texto
                FontMetrics fm = getFontMetrics(glitchFont != null ? glitchFont.deriveFont(Font.BOLD, 36f)
                        : new Font(Font.SANS_SERIF, Font.BOLD, 36));
                int w = fm.stringWidth(getText()) + 40; // 40px de padding horizontal
                int h = fm.getHeight() + 20; // 20px padding vertical
                return new Dimension(w, h);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                String text = getText();

                if (!isGlitchActive) {
                    // Estado Normal: Luvable.ttf, blanco con relieve negro
                    g2d.setFont(normalFont.deriveFont(32f));
                    FontMetrics fm = g2d.getFontMetrics();
                    int w = fm.stringWidth(text);
                    int h = fm.getAscent();
                    int x = (getWidth() - w) / 2;
                    int y = (getHeight() + h) / 2 - 4;

                    // Relieve Negro (más notorio)
                    g2d.setColor(Color.BLACK);
                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dy = -2; dy <= 2; dy++) {
                            if (dx != 0 || dy != 0) {
                                g2d.drawString(text, x + dx, y + dy);
                            }
                        }
                    }

                    // Hover color (Rojo) o Blanco
                    if (getModel().isRollover()) {
                        g2d.setColor(new Color(255, 50, 50));
                    } else {
                        g2d.setColor(Color.WHITE);
                    }
                    g2d.drawString(text, x, y);

                } else {
                    // Estado Glitch: HorrorCorps.ttf, BOLD, Blanco puro, sin relieve
                    g2d.setFont(glitchFont.deriveFont(Font.BOLD, 36f));
                    FontMetrics fm = g2d.getFontMetrics();
                    int w = fm.stringWidth(text);
                    int h = fm.getAscent();
                    // Ligero temblor en los botones
                    int x = (getWidth() - w) / 2 + (int) (Math.random() * 6 - 3);
                    int y = (getHeight() + h) / 2 - 4 + (int) (Math.random() * 6 - 3);

                    g2d.setColor(Color.WHITE);
                    g2d.drawString(text, x, y);
                }

                g2d.dispose();
            }
        };

        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        boton.setBorderPainted(false);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // El hover se maneja a través del getModel().isRollover() en el paintComponent
        // Añadimos MouseListener para forzar el repintado inmediato al pasar el mouse
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.repaint();
            }
        });

        return boton;
    }
}
