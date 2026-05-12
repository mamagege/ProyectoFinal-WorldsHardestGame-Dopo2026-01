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
 * Representa el menú de inicio del juego con una transición cinemática.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class PanelMenu extends JPanel {
    private VentanaPrincipal ventana;

    // Estados del Menú
    public enum MenuAnimationState {
        NORMAL,
        GLITCH_TRANSITION,
        MACABRE,
        NORMALIZATION_TRANSITION
    }

    // Recursos
    private Image backgroundImage;
    private Image macabreBackgroundImage;
    private Image normalImage; // estrella.png
    private Image macabreImage; // Estrella_macabra.png
    private Font normalFont; // Luvable.ttf
    private Font macabreFont; // HorrorCorps.ttf

    // Animación y Estado
    private MenuAnimationState currentState = MenuAnimationState.NORMAL;
    private Timer animationTimer;
    private Timer stateCycleTimer;
    private long startTime;
    private long glitchStartTime;
    private long macabreStartTime;
    private int transitionDuration;

    // Contenedor de botones
    private List<JButton> menuButtons = new ArrayList<>();

    public PanelMenu(VentanaPrincipal ventana) {
        this.ventana = ventana;

        // 1. Carga de Recursos (Fuentes e Imágenes)
        try {
            // Carga optimizada para GIFs animados usando ImageIcon para preservar la reproducción secuencial
            backgroundImage = new ImageIcon("src/resources/images/fondo_animado.gif").getImage();
            macabreBackgroundImage = ImageIO.read(new File("src/resources/images/fondo_macabro.png"));
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudieron cargar los fondos (Animado / Macabro)");
        }

        try {
            normalImage = ImageIO.read(new File("src/resources/images/estrella.png"));
            macabreImage = ImageIO.read(new File("src/resources/images/Estrella_macabra.png"));
        } catch (IOException e) {
            System.err.println("Advertencia: No se pudo cargar estrella.png o Estrella_macabra.png");
        }

        try {
            File fontFile = new File("src/resources/fonts/HellraiserBloody.ttf");
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            
            // Unificar la tipografía HellraiserBloody globalmente para ambos estados
            normalFont = baseFont.deriveFont(60f);
            macabreFont = baseFont.deriveFont(60f);
        } catch (FontFormatException | IOException e) {
            System.err.println("Advertencia: No se pudo cargar HellraiserBloody.ttf");
            normalFont = new Font(Font.SANS_SERIF, Font.BOLD, 60);
            macabreFont = normalFont;
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
        btnNuevaPartida.addActionListener(e -> {
            ventana.getGameOrchestrator().resetGame();
            ventana.mostrarPanel("JUEGO");
        });
        btnSalir.addActionListener(e -> System.exit(0));

        // Añadir botones al panel
        buttonPanel.add(btnNuevaPartida);
        buttonPanel.add(btnOpciones);
        buttonPanel.add(btnContinuar);
        buttonPanel.add(btnSalir);

        // Registrar botones para actualizarlos globalmente
        menuButtons.add(btnNuevaPartida);
        menuButtons.add(btnOpciones);
        menuButtons.add(btnContinuar);
        menuButtons.add(btnSalir);

        add(buttonPanel, gbc);

        // 4. Ciclo automático desactivado - Controlado ahora por Mouse Hover

        // Iniciar timer para la animación a 60 FPS (~16ms)
        startTime = System.currentTimeMillis();
        animationTimer = new Timer(16, e -> repaint());
        animationTimer.start();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Renderizado de alta calidad
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String title = "THE DOPO HARDEST GAME";

        // Renderizado Condicional basado en el Estado
        if (currentState == MenuAnimationState.NORMAL) {
            renderNormal(g2d, title);
        } else if (currentState == MenuAnimationState.GLITCH_TRANSITION) {
            renderGlitch(g2d, title);
        } else if (currentState == MenuAnimationState.MACABRE) {
            renderMacabre(g2d, title);
        } else if (currentState == MenuAnimationState.NORMALIZATION_TRANSITION) {
            renderNormalization(g2d, title);
        }

        g2d.dispose();
    }

    private void renderNormal(Graphics2D g2d, String title) {
        // Fondo Arcoiris
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(new Color(15, 15, 15));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // Estrella Flotante
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

        // Título Multilínea con Distribución Cinematográfica
        drawMultiLineTitle(g2d, Color.WHITE, Color.BLACK, 0, 0, 290);
    }

    private void renderGlitch(Graphics2D g2d, String title) {
        long currentTime = System.currentTimeMillis();
        double progress = Math.max(0.0, Math.min(1.0, (double) (currentTime - glitchStartTime) / transitionDuration));
        double intensity = progress; // De 0.0 a 1.0

        // 1. Dibujar fondo base (suavizado entre Normal y Macabre hasta el final)
        float alphaMacabre = (float) Math.max(0, Math.min(1.0, (progress - 0.3) / 0.7)); // Empieza a los 30% y termina al 100%
        g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        if (alphaMacabre > 0) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaMacabre));
            g2d.drawImage(macabreBackgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        // 2. Dibujar Estrellas (Interpolación de tamaño, posición y transparencia)
        if (normalImage != null && macabreImage != null) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            double amplitud = 8.0;
            double velocidad = 400.0;
            int yOffset = (int) (Math.sin(elapsedTime / velocidad) * amplitud);

            // Interpolación de valores
            int currentHeight = (int) (220 + (300 - 220) * progress);
            int currentY = (int) (40 + (20 - 40) * progress) + yOffset;
            
            // 1. Dibujar Estrella Normal Fragmentándose
            if (progress < 0.9) {
                float fragAlpha = (progress > 0.6) ? (float)(1.0 - (progress - 0.6)/0.3) : 1.0f;
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0, fragAlpha)));
                
                int drawWidthN = (normalImage.getWidth(null) * currentHeight) / Math.max(1, normalImage.getHeight(null));
                int slices = 10;
                int sliceH = currentHeight / slices;
                for (int i = 0; i < slices; i++) {
                    // Desplazamiento explosivo hacia afuera
                    int fragOffset = (int) (Math.random() * 50 * progress);
                    int xFrag = (getWidth() - drawWidthN) / 2 + (Math.random() > 0.5 ? fragOffset : -fragOffset);
                    
                    g2d.drawImage(normalImage, xFrag, currentY + i * sliceH, xFrag + drawWidthN, currentY + (i + 1) * sliceH,
                            0, i * (normalImage.getHeight(null)/slices), normalImage.getWidth(null), (i + 1) * (normalImage.getHeight(null)/slices), this);
                }
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }

            // 2. Estrella Macabra (Ensamblándose / Cross-fade)
            if (alphaMacabre > 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaMacabre));
                int drawWidthM = (macabreImage.getWidth(null) * currentHeight) / Math.max(1, macabreImage.getHeight(null));
                int xM = (getWidth() - drawWidthM) / 2;
                // Pequeña vibración mientras se estabiliza
                int shakeM = (int) (Math.random() * 5 * (1.0 - progress));
                g2d.drawImage(macabreImage, xM + shakeM, currentY, drawWidthM, currentHeight, this);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        }

        // 3. Glitch Cromático y Vibración (Se apaga suavemente al final)
        int shake = (int) (Math.random() * 10 * (1.0 - progress)); 
        if (progress < 0.98) {
            float glitchAlpha = (progress > 0.7) ? (float)(1.0 - (progress - 0.7)/0.3) : 1.0f;
            renderRGBSplit(g2d, title, (int)(15 * (1.0 - progress)), Math.max(0, glitchAlpha));
        }

        // 4. Distorsión por Bloques / "Melting" (Se desvanece al final)
        float distortionAlpha = (progress > 0.75) ? (float)(1.0 - (progress - 0.75)/0.25) : 1.0f;
        if (progress > 0.4 && distortionAlpha > 0) {
            int blockCount = (int) (100 * (1.0 - progress) * distortionAlpha);
            for (int i = 0; i < blockCount; i++) {
                int bw = (int) (Math.random() * 80 * (1.0 - progress));
                int bh = (int) (Math.random() * 40 * (1.0 - progress));
                int bx = (int) (Math.random() * getWidth());
                int by = (int) (Math.random() * getHeight());
                
                // Bloques con transparencia progresiva
                g2d.setColor(Math.random() > 0.3 ? new Color(0, 0, 0, (int)(255 * distortionAlpha)) 
                                                 : new Color(150, 0, 0, (int)(180 * distortionAlpha)));
                g2d.fillRect(bx, by, bw, bh);
            }
        }

        // Ruido estático fino (se apaga gradualmente)
        if (progress < 0.98) {
            g2d.setColor(new Color(255, 255, 255, (int)(30 * (1.0 - progress) * distortionAlpha)));
            for (int i = 0; i < 30; i++) {
                g2d.drawLine(0, (int)(Math.random()*getHeight()), getWidth(), (int)(Math.random()*getHeight()));
            }
        }
    }

    private void renderRGBSplit(Graphics2D g2d, String title, int offset, float alpha) {
        Composite oldComp = g2d.getComposite();
        if (alpha < 1.0f) g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        int randY = (int)(Math.random() * 4);

        // Canal Rojo Split Multilínea
        drawMultiLineTitle(g2d, new Color(255, 0, 0, 120), null, -offset, randY, 290);
        
        // Canal Azul Split Multilínea
        drawMultiLineTitle(g2d, new Color(0, 0, 255, 120), null, offset, -randY, 290);

        // Centro Blanco principal Multilínea
        drawMultiLineTitle(g2d, Color.WHITE, null, 0, 0, 290);

        g2d.setComposite(oldComp);
    }

    private void renderNormalization(Graphics2D g2d, String title) {
        long currentTime = System.currentTimeMillis();
        double progress = Math.max(0.0, Math.min(1.0, (double) (currentTime - glitchStartTime) / transitionDuration));
        
        // Fondo: Cross-fade inverso (Macabre -> Normal)
        float alphaNormal = (float) progress;
        g2d.drawImage(macabreBackgroundImage, 0, 0, getWidth(), getHeight(), this);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaNormal));
        g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // Estrella: Cross-fade e interpolación inversa
        if (normalImage != null && macabreImage != null) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            double amplitud = 8.0;
            double velocidad = 400.0;
            int yOffset = (int) (Math.sin(elapsedTime / velocidad) * amplitud);

            int currentHeight = (int) (300 + (220 - 300) * progress);
            int currentY = (int) (20 + (40 - 20) * progress) + yOffset;
            
            // Estrella Macabra
            int drawWidthM = (macabreImage.getWidth(null) * currentHeight) / Math.max(1, macabreImage.getHeight(null));
            int xM = (getWidth() - drawWidthM) / 2;
            g2d.drawImage(macabreImage, xM, currentY, drawWidthM, currentHeight, this);

            // Estrella Normal (Aparece suavemente)
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaNormal));
            int drawWidthN = (normalImage.getWidth(null) * currentHeight) / Math.max(1, normalImage.getHeight(null));
            int xN = (getWidth() - drawWidthN) / 2;
            g2d.drawImage(normalImage, xN, currentY, drawWidthN, currentHeight, this);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        // Título: Recuperación de brillo
        // Título Recuperado Multilínea
        drawMultiLineTitle(g2d, Color.WHITE, Color.BLACK, 0, 0, 290);

        // Barrido de Luz Purificador (Efecto de escaneo blanco)
        g2d.setColor(new Color(255, 255, 255, (int)(100 * (1.0 - progress))));
        int sweepY = (int) (progress * getHeight());
        g2d.fillRect(0, sweepY, getWidth(), 20);
    }

    private void renderMacabre(Graphics2D g2d, String title) {
        float entryAlpha = 1.0f; // Instantáneo, sin retraso visual

        // Nuevo Fondo Macabro
        if (macabreBackgroundImage != null) {
            g2d.drawImage(macabreBackgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(new Color(15, 0, 0));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // Aplicar transparencia de entrada a los elementos dinámicos
        Composite oldComp = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, entryAlpha));

        // Estrella Macabra Flotante
        if (macabreImage != null) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            double amplitud = 8.0;
            double velocidad = 400.0;
            int yOffset = (int) (Math.sin(elapsedTime / velocidad) * amplitud);

            int drawHeight = 300; // Aumentado de 220 a 300
            int drawWidth = (macabreImage.getWidth(null) * drawHeight) / Math.max(1, macabreImage.getHeight(null));

            int x = (getWidth() - drawWidth) / 2;
            int y = 20 + yOffset; // Subir un poco (de 40 a 20) para compensar el mayor tamaño
            g2d.drawImage(macabreImage, x, y, drawWidth, drawHeight, this);
        }

        // Título Macabro Multilínea alineado
        drawMultiLineTitle(g2d, Color.WHITE, Color.BLACK, 0, 0, 290);

        g2d.setComposite(oldComp);
    }

    /**
     * Crea un botón que adapta su visualización al estado actual del menú.
     */
    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            public Dimension getPreferredSize() {
                // Usar siempre la fuente Normal para mantener el tamaño constante
                Font f = (normalFont != null) ? normalFont.deriveFont(Font.BOLD, 36f) : new Font(Font.SANS_SERIF, Font.BOLD, 36);
                FontMetrics fm = getFontMetrics(f);
                int w = fm.stringWidth(getText()) + 40;
                int h = fm.getHeight() + 20;
                return new Dimension(w, h);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                String text = getText();

                if (currentState == MenuAnimationState.NORMAL) {
                    g2d.setFont(normalFont.deriveFont(32f));
                    FontMetrics fmN = g2d.getFontMetrics();
                    int xN = (getWidth() - fmN.stringWidth(text)) / 2;
                    int yN = (getHeight() + fmN.getAscent()) / 2 - 4;

                    // Relieve
                    g2d.setColor(Color.BLACK);
                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dy = -2; dy <= 2; dy++) {
                            if (dx != 0 || dy != 0) g2d.drawString(text, xN + dx, yN + dy);
                        }
                    }
                    // Texto principal con Hover
                    g2d.setColor(getModel().isRollover() ? new Color(255, 50, 50) : Color.WHITE);
                    g2d.drawString(text, xN, yN);
                } else if (currentState == MenuAnimationState.GLITCH_TRANSITION) {
                    // No dibujar texto o dibujar solo ráfagas
                    if (Math.random() > 0.7) {
                        g2d.setFont(normalFont.deriveFont(32f));
                        g2d.setColor(new Color(255, 0, 0, 100));
                        g2d.drawString(text, (getWidth() - g2d.getFontMetrics().stringWidth(text))/2 + (int)(Math.random()*10-5), getHeight()/2);
                    }
                } else if (currentState == MenuAnimationState.MACABRE) {
                    // Misma fuente que Menú 1 (Luvable)
                    g2d.setFont(normalFont.deriveFont(32f));
                    FontMetrics fmM = g2d.getFontMetrics();
                    int xM = (getWidth() - fmM.stringWidth(text)) / 2;
                    int yM = (getHeight() + fmM.getAscent()) / 2 - 4;
                    
                    // Añadir contorno negro para legibilidad
                    g2d.setColor(Color.BLACK);
                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dy = -2; dy <= 2; dy++) {
                            if (dx != 0 || dy != 0) g2d.drawString(text, xM + dx, yM + dy);
                        }
                    }

                    // Añadir hover para que sean "funcionales" visualmente
                    if (getModel().isRollover()) {
                        g2d.setColor(new Color(255, 50, 50)); // Rojo en hover
                    } else {
                        g2d.setColor(new Color(220, 220, 220)); // Blanco/Gris claro para contraste
                    }
                    g2d.drawString(text, xM, yM);
                } else if (currentState == MenuAnimationState.NORMALIZATION_TRANSITION) {
                    // Los botones ya vuelven a su estilo normal suavemente
                    g2d.setFont(normalFont.deriveFont(32f));
                    FontMetrics fmR = g2d.getFontMetrics();
                    int xR = (getWidth() - fmR.stringWidth(text)) / 2;
                    int yR = (getHeight() + fmR.getAscent()) / 2 - 4;
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(text, xR, yR);
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

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Cambio INMEDIATO a estado Macabro al poner el mouse sobre cualquier botón
                currentState = MenuAnimationState.MACABRE;
                macabreStartTime = System.currentTimeMillis();
                PanelMenu.this.repaint(); 
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Retorno instantáneo al estado Normal al salir
                currentState = MenuAnimationState.NORMAL;
                PanelMenu.this.repaint();
            }
        });

        return boton;
    }

    private void drawMultiLineTitle(Graphics2D g2d, Color fill, Color stroke, int xOff, int yOff, int yBase) {
        String[] lines = {"THE", "DOPO", "HARDEST GAME"};
        float[] sizes = {40f, 150f, 45f};
        int[] yDiffs = {0, 125, 185}; // Aumentado el espaciado vertical
        
        for (int i = 0; i < 3; i++) {
            g2d.setFont(normalFont.deriveFont(sizes[i]));
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(lines[i])) / 2 + xOff;
            int y = (yBase - 20) + yDiffs[i] + yOff; // Ajustar base 20px arriba para compensar expansión
            
            if (stroke != null) {
                g2d.setColor(stroke);
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dy = -2; dy <= 2; dy++) {
                        if (dx != 0 || dy != 0) g2d.drawString(lines[i], x + dx, y + dy);
                    }
                }
            }
            g2d.setColor(fill);
            g2d.drawString(lines[i], x, y);
        }
    }
}
