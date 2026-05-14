package presentation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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

    // Sistema dinámico de partículas de sangre (Modo Macabro)
    private List<BloodParticle> bloodParticles = new ArrayList<>();
    private List<BloodParticle> stagingBloodParticles = new ArrayList<>(); // Buffer para prevenir
                                                                           // ConcurrentModification

    // Sistema de brillo arcoiris (Modo Normal)
    private List<RainbowParticle> rainbowParticles = new ArrayList<>();
    private List<RainbowParticle> stagingRainbowParticles = new ArrayList<>();

    public PanelMenu(VentanaPrincipal ventana) {
        this.ventana = ventana;

        // 1. Carga de Recursos (Fuentes e Imágenes)
        try {
            backgroundImage = ImageIO.read(new File("src/resources/images/fondo_arcoiris.png"));
            macabreBackgroundImage = ImageIO.read(new File("src/resources/images/fondo_macabro.png"));
        } catch (IOException e) {
            System.err.println("Advertencia: No se pudieron cargar los fondos");
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
        // 2. Configurar Layout Absoluto para Posicionamiento Preciso sobre Ponies
        setLayout(null);

        // 3. Instanciar botones con nuevos identificadores del arte conceptual
        JButton btnJugar = crearBoton("JUGAR");
        JButton btnCargar = crearBoton("CARGAR");
        JButton btnOpciones = crearBoton("OPCIONES");
        JButton btnSalir = crearBoton("SALIR");

        // Configurar acciones directas (Transicion al splash antes de la seleccion de modalidad)
        btnJugar.addActionListener(e -> {
            ventana.getPanelSplashLimbo().startSequence();
            ventana.mostrarPanel("SPLASH_LIMBO");
        });

        btnSalir.addActionListener(e -> System.exit(0));

        // Registrar botones para actualización cíclica
        menuButtons.add(btnJugar);
        menuButtons.add(btnOpciones);
        menuButtons.add(btnCargar);
        menuButtons.add(btnSalir);

        // Añadir directamente al panel principal para que doLayout gestione sus
        // coordenadas
        add(btnJugar);
        add(btnOpciones);
        add(btnCargar);
        add(btnSalir);

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

        // 1. Título Multilínea con Distribución Cinematográfica (Dispara spawn de
        // brillo si useRainbow=true)
        drawMultiLineTitle(g2d, Color.WHITE, Color.BLACK, 0, 0, 290, true, false);

        // 2. Render y Actualización de Brillos Mágicos de Arcoiris
        Composite oldComp = g2d.getComposite();
        Iterator<RainbowParticle> it = rainbowParticles.iterator();
        while (it.hasNext()) {
            RainbowParticle p = it.next();
            p.update();
            if (p.alpha <= 0 && !p.fadingIn) {
                it.remove();
            } else {
                // Calcular opacidad segura
                float normAlpha = Math.max(0f, Math.min(255f, p.alpha)) / 255f;
                g2d.setColor(Color.getHSBColor(p.hue, 0.6f, 1.0f)); // Brillo neón pastel
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, normAlpha));
                // Dibujar punto de luz
                g2d.fillOval((int) p.x, (int) p.y, (int) p.size, (int) p.size);
            }
        }
        g2d.setComposite(oldComp);

        // 3. Flush de la cola de staging
        if (!stagingRainbowParticles.isEmpty()) {
            rainbowParticles.addAll(stagingRainbowParticles);
            stagingRainbowParticles.clear();
        }
    }

    private void renderGlitch(Graphics2D g2d, String title) {
        long currentTime = System.currentTimeMillis();
        double progress = Math.max(0.0, Math.min(1.0, (double) (currentTime - glitchStartTime) / transitionDuration));
        double intensity = progress; // De 0.0 a 1.0

        // 1. Dibujar fondo base (suavizado entre Normal y Macabre hasta el final)
        float alphaMacabre = (float) Math.max(0, Math.min(1.0, (progress - 0.3) / 0.7)); // Empieza a los 30% y termina
                                                                                         // al 100%
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
                float fragAlpha = (progress > 0.6) ? (float) (1.0 - (progress - 0.6) / 0.3) : 1.0f;
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0, fragAlpha)));

                int drawWidthN = (normalImage.getWidth(null) * currentHeight)
                        / Math.max(1, normalImage.getHeight(null));
                int slices = 10;
                int sliceH = currentHeight / slices;
                for (int i = 0; i < slices; i++) {
                    // Desplazamiento explosivo hacia afuera
                    int fragOffset = (int) (Math.random() * 50 * progress);
                    int xFrag = (getWidth() - drawWidthN) / 2 + (Math.random() > 0.5 ? fragOffset : -fragOffset);

                    g2d.drawImage(normalImage, xFrag, currentY + i * sliceH, xFrag + drawWidthN,
                            currentY + (i + 1) * sliceH,
                            0, i * (normalImage.getHeight(null) / slices), normalImage.getWidth(null),
                            (i + 1) * (normalImage.getHeight(null) / slices), this);
                }
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }

            // 2. Estrella Macabra (Ensamblándose / Cross-fade)
            if (alphaMacabre > 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaMacabre));
                int drawWidthM = (macabreImage.getWidth(null) * currentHeight)
                        / Math.max(1, macabreImage.getHeight(null));
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
            float glitchAlpha = (progress > 0.7) ? (float) (1.0 - (progress - 0.7) / 0.3) : 1.0f;
            renderRGBSplit(g2d, title, (int) (15 * (1.0 - progress)), Math.max(0, glitchAlpha));
        }

        // 4. Distorsión por Bloques / "Melting" (Se desvanece al final)
        float distortionAlpha = (progress > 0.75) ? (float) (1.0 - (progress - 0.75) / 0.25) : 1.0f;
        if (progress > 0.4 && distortionAlpha > 0) {
            int blockCount = (int) (100 * (1.0 - progress) * distortionAlpha);
            for (int i = 0; i < blockCount; i++) {
                int bw = (int) (Math.random() * 80 * (1.0 - progress));
                int bh = (int) (Math.random() * 40 * (1.0 - progress));
                int bx = (int) (Math.random() * getWidth());
                int by = (int) (Math.random() * getHeight());

                // Bloques con transparencia progresiva
                g2d.setColor(Math.random() > 0.3 ? new Color(0, 0, 0, (int) (255 * distortionAlpha))
                        : new Color(150, 0, 0, (int) (180 * distortionAlpha)));
                g2d.fillRect(bx, by, bw, bh);
            }
        }

        // Ruido estático fino (se apaga gradualmente)
        if (progress < 0.98) {
            g2d.setColor(new Color(255, 255, 255, (int) (30 * (1.0 - progress) * distortionAlpha)));
            for (int i = 0; i < 30; i++) {
                g2d.drawLine(0, (int) (Math.random() * getHeight()), getWidth(), (int) (Math.random() * getHeight()));
            }
        }
    }

    private void renderRGBSplit(Graphics2D g2d, String title, int offset, float alpha) {
        Composite oldComp = g2d.getComposite();
        if (alpha < 1.0f)
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        int randY = (int) (Math.random() * 4);

        // Canal Rojo Split Multilínea
        drawMultiLineTitle(g2d, new Color(255, 0, 0, 120), null, -offset, randY, 290, false, false);

        // Canal Azul Split Multilínea
        drawMultiLineTitle(g2d, new Color(0, 0, 255, 120), null, offset, -randY, 290, false, false);

        // Centro Blanco principal Multilínea
        drawMultiLineTitle(g2d, Color.WHITE, null, 0, 0, 290, false, false);

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
        drawMultiLineTitle(g2d, Color.WHITE, Color.BLACK, 0, 0, 290, false, false);

        // Barrido de Luz Purificador (Efecto de escaneo blanco)
        g2d.setColor(new Color(255, 255, 255, (int) (100 * (1.0 - progress))));
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

        // 1. Título Macabro en ROJO SANGRE (Activa el disparador de partículas)
        drawMultiLineTitle(g2d, new Color(180, 0, 0), Color.BLACK, 0, 0, 290, false, true);

        // 2. Render y Actualización de Gotas de Sangre Chorreando
        Iterator<BloodParticle> it = bloodParticles.iterator();
        while (it.hasNext()) {
            BloodParticle p = it.next();
            p.update();
            if (p.y > getHeight() || p.alpha <= 0) {
                it.remove(); // Garbage collect visual
            } else {
                // Dibujar contorno oscuro de la gota
                g2d.setColor(new Color(100, 0, 0, p.alpha));
                g2d.fillOval((int) p.x - 1, (int) p.y - 1, (int) p.size + 2, (int) (p.size * p.stretchY) + 2);
                // Dibujar núcleo carmesí brillante
                g2d.setColor(new Color(180, 0, 0, p.alpha));
                g2d.fillOval((int) p.x, (int) p.y, (int) p.size, (int) (p.size * p.stretchY));
            }
        }

        // 3. Integración Segura de nuevas partículas generadas este frame
        if (!stagingBloodParticles.isEmpty()) {
            bloodParticles.addAll(stagingBloodParticles);
            stagingBloodParticles.clear();
        }

        g2d.setComposite(oldComp);
    }

    @Override
    public void doLayout() {
        // Forzar redimensionamiento manual relativo al tamaño del lienzo
        super.doLayout();

        int w = getWidth();
        int h = getHeight();

        // Garantizar que los botones están creados y mapeados
        if (menuButtons != null && menuButtons.size() >= 4) {
            JButton btnJugar = menuButtons.get(0);
            JButton btnOpciones = menuButtons.get(1);
            JButton btnCargar = menuButtons.get(2);
            JButton btnSalir = menuButtons.get(3);

            // Definir áreas de toque confortables sobre los ponies
            int ponyW = 135;
            int ponyH = 55;

            // Mapeo Porcentual Preciso basado en la distribución artística
            // Pony Blanco (JUGAR) -> ~16% Horizontal, ~76% Vertical
            btnJugar.setBounds((int) (w * 0.147) - ponyW / 2, (int) (h * 0.790) - ponyH / 2, ponyW, ponyH);

            // Pony Azul (CARGAR) -> ~42% Horizontal, ~76% Vertical
            btnCargar.setBounds((int) (w * 0.438) - ponyW / 2, (int) (h * 0.79) - ponyH / 2, ponyW, ponyH);

            // Pony Violeta (OPCIONES) -> ~64% Horizontal, ~76% Vertical
            btnOpciones.setBounds((int) (w * 0.653) - ponyW / 2, (int) (h * 0.79) - ponyH / 2, ponyW, ponyH);

            // El Sol (SALIR) -> ~92% Horizontal, ~16% Vertical
            int sunDiameter = 130;
            btnSalir.setBounds((int) (w * 0.931) - sunDiameter / 2, (int) (h * 0.17) - sunDiameter / 2, sunDiameter,
                    sunDiameter);
        }
    }

    /**
     * Crea un botón que adapta su visualización al estado actual del menú.
     */
    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            public Dimension getPreferredSize() {
                // Usar siempre la fuente Normal para mantener el tamaño constante
                Font f = (normalFont != null) ? normalFont.deriveFont(Font.BOLD, 36f)
                        : new Font(Font.SANS_SERIF, Font.BOLD, 36);
                FontMetrics fm = getFontMetrics(f);
                int w = fm.stringWidth(getText()) + 40;
                int h = fm.getHeight() + 20;
                return new Dimension(w, h);
            }

            @Override
            protected void paintComponent(Graphics g) {
                // El delineado de depuración ha sido retirado tras calibración exitosa.
                // Los botones operan en modo de transparencia pura absoluta.
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
                rainbowParticles.clear(); // Eliminar rastros de fantasía
                PanelMenu.this.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Retorno instantáneo al estado Normal al salir
                currentState = MenuAnimationState.NORMAL;
                bloodParticles.clear(); // Eliminar rastros macabros
                PanelMenu.this.repaint();
            }
        });

        return boton;
    }

    private void drawMultiLineTitle(Graphics2D g2d, Color fill, Color stroke, int xOff, int yOff, int yBase,
            boolean useRainbow, boolean allowBloodSpawn) {
        String[] lines = { "THE", "DOPO", "HARDEST GAME" };
        float[] sizes = { 55f, 195f, 60f }; // Escalado masivo solicitado ("maaas grande")

        // Centrado visual geométrico compartido para el arco (Concéntrico)
        double centerX = getWidth() / 2.0 + xOff;
        double centerY = yBase + 950; // Ajustar radio de giro a la nueva escala mayor

        // Bajar alturas y recalcular brechas para fuentes más grandes
        int[] yTargets = { yBase + 10, yBase + 160, yBase + 235 };

        for (int i = 0; i < 3; i++) {
            g2d.setFont(normalFont.deriveFont(sizes[i]));
            FontMetrics fm = g2d.getFontMetrics();

            double linePeakY = yTargets[i] + yOff;
            double radius = centerY - linePeakY; // Radio concéntrico variable por línea

            char[] chars = lines[i].toCharArray();
            int totalWidth = fm.stringWidth(lines[i]);

            // Angulo total que abarca el texto = longitud del arco / radio
            double totalAngle = totalWidth / radius;
            double startAngle = -totalAngle / 2.0; // Centrado simétrico

            double currentAccumWidth = 0;

            for (int c = 0; c < chars.length; c++) {
                String ch = String.valueOf(chars[c]);
                int cw = fm.stringWidth(ch);

                // Calcular el ángulo exacto del CENTRO de este carácter particular
                double charCenterInArc = currentAccumWidth + cw / 2.0;
                double theta = startAngle + (charCenterInArc / radius);

                // Convertir de radianes locales a vector 2D en el círculo
                double drawAngle = -Math.PI / 2.0 + theta;
                double px = centerX + Math.cos(drawAngle) * radius;
                double py = centerY + Math.sin(drawAngle) * radius;

                // Aplicar Matrices de Transformación para Rotación Local del Carácter
                AffineTransform old = g2d.getTransform();
                g2d.translate(px, py);
                g2d.rotate(theta); // Alínea la base de la letra perpendicular al radio (forma el arco perfecto)

                // 1. Determinación final de colores (Capa de Personalización Cromática)
                Color activeFill = fill;
                Color activeStroke = stroke;

                if (useRainbow) {
                    if (i == 0) {
                        // Regla especial: "THE" invertido en modo normal (Negro con borde Blanco)
                        activeFill = Color.BLACK;
                        activeStroke = Color.WHITE;
                    } else {
                        // Resto de letras: Gradiente de Arcoiris
                        float hue = (float) currentAccumWidth / (float) Math.max(1, totalWidth);
                        activeFill = Color.getHSBColor(hue, 0.85f, 1.0f);
                    }
                }

                // 2. Dibujar Contorno (Relieve)
                if (activeStroke != null) {
                    g2d.setColor(activeStroke);
                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dy = -2; dy <= 2; dy++) {
                            if (dx != 0 || dy != 0)
                                g2d.drawString(ch, -cw / 2 + dx, dy);
                        }
                    }
                }

                // 3. Dibujar Relleno Principal
                g2d.setColor(activeFill);
                g2d.drawString(ch, -cw / 2, 0);

                g2d.setTransform(old); // Limpiar matriz de transformación para el siguiente ciclo

                // Inyectar gota de sangre probabilísticamente solo si está en modo macabro
                if (allowBloodSpawn && bloodParticles.size() < 250 && Math.random() < 0.018) {
                    stagingBloodParticles.add(new BloodParticle(px, py));
                }

                // Inyectar destello de arcoiris si está en modo feliz/normal (Más frecuentes y
                // densos)
                if (useRainbow && rainbowParticles.size() < 250 && Math.random() < 0.015) {
                    stagingRainbowParticles.add(new RainbowParticle(px, py));
                }

                currentAccumWidth += cw;
            }
        }
    }

    /**
     * Define una partícula dinámica de sangre con físicas vectoriales duales (goteo vs explosión).
     */
    private static class BloodParticle {
        double x, y, vx, vy;
        double gravity;
        double stretchY;
        float size;
        int alpha = 255;
        int decaySpeed;

        // Constructor 1: Modo Goteo Estándar (Desde el título)
        public BloodParticle(double x, double y) {
            this.x = x + (Math.random() * 30 - 15);
            this.y = y + 5;
            this.vx = 0;
            this.vy = 0.3 + Math.random() * 0.7;
            this.size = 3f + (float) (Math.random() * 3.5f);
            this.gravity = 0.01;
            this.stretchY = 1.6; // Forma de gota estirada
            this.decaySpeed = 1;
        }

        // Constructor 2: Modo Explosión (Desde los ponies)
        public BloodParticle(double startX, double startY, boolean explosion) {
            this.x = startX + (Math.random() * 40 - 20);
            this.y = startY + (Math.random() * 30 - 15);
            // Calcular vector de velocidad radial
            double angle = Math.random() * Math.PI * 2;
            double force = 4.0 + Math.random() * 14.0;
            this.vx = Math.cos(angle) * force;
            this.vy = Math.sin(angle) * force - 4.0; // Sesgo inicial hacia arriba para "saltar"
            this.size = 5f + (float) (Math.random() * 8.0f); // Trozos más grandes
            this.gravity = 0.4; // Fuerte caída gravitacional
            this.stretchY = 1.0; // Forma esférica tipo coágulo/salpicadura
            this.decaySpeed = 4; // Desvanecimiento más rápido por violencia visual
        }

        public void update() {
            x += vx;
            y += vy;
            vy += gravity;
            vx *= 0.97; // Fricción del aire lateral
            
            // Lógica de degradación orgánica
            if (Math.random() > 0.4) {
                alpha -= decaySpeed;
                if (alpha < 0) alpha = 0;
            }
        }
    }

    /**
     * Define una mota de luz flotante con fading de entrada/salida y tonalidad
     * aleatoria.
     */
    private static class RainbowParticle {
        double x, y, vx, vy;
        float size;
        int alpha = 0;
        int maxAlpha;
        boolean fadingIn = true;
        float hue;

        public RainbowParticle(double x, double y) {
            this.x = x + (Math.random() * 30 - 15);
            this.y = y + (Math.random() * 20 - 10); // Origen disperso cerca de la letra
            this.vx = (Math.random() - 0.5) * 0.4; // Viento lateral sutil
            this.vy = -0.15 - Math.random() * 0.3; // Flotan HACIA ARRIBA lentamente
            this.size = 3.5f + (float) (Math.random() * 4.0f); // Tamaño amplificado para mayor visibilidad
            this.maxAlpha = 130 + (int) (Math.random() * 125); // Mucho más brillante y luminosa
            this.hue = (float) Math.random(); // Color propio aleatorio
        }

        public void update() {
            x += vx;
            y += vy;
            // Efecto de respiración del brillo
            if (fadingIn) {
                alpha += 2;
                if (alpha >= maxAlpha)
                    fadingIn = false;
            } else {
                // Desvanecer sutilmente
                if (Math.random() > 0.3)
                    alpha -= 1;
            }
        }
    }
}
