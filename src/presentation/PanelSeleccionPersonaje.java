package presentation;

import javax.imageio.ImageIO;
import javax.swing.*;
import domain.GameWHGException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Pantalla de seleccion de personaje.
 * Muestra 3 tarjetas (Rafael, Leonardo, Miguel Angelo) con efectos hover
 * y notifica al orquestador GameWHG la eleccion del jugador.
 *
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class PanelSeleccionPersonaje extends JPanel {

    // ------------------------------------------------------------------ //
    //  Constantes de personaje (espejo de GameWHG.iniciarJuegoConPersonaje)
    // ------------------------------------------------------------------ //
    public static final int PERSONAJE_ROJO  = 0;
    public static final int PERSONAJE_AZUL  = 1;
    public static final int PERSONAJE_VERDE = 2;

    // ------------------------------------------------------------------ //
    //  Referencias
    // ------------------------------------------------------------------ //
    private final VentanaPrincipal ventana;
    private final GameWHGGUI          gameOrchestrator;

    // ------------------------------------------------------------------ //
    //  Recursos graficos
    // ------------------------------------------------------------------ //
    private BufferedImage backgroundImage;
    private Font          titleFont;
    private Font          cardTitleFont;
    private Font          cardStatsFont;

    private TarjetaPersonaje tarjetaRojo;
    private TarjetaPersonaje tarjetaAzul;
    private TarjetaPersonaje tarjetaVerde;
    private BufferedImage    imagePruebaPvp;
    private JButton          btnVolver;

    // ------------------------------------------------------------------ //
    //  Animacion hover de las tarjetas
    // ------------------------------------------------------------------ //
    private Timer animTimer;

    // ------------------------------------------------------------------ //
    //  Estado PvsP
    // ------------------------------------------------------------------ //
    private int p1Index = 0;
    private int p2Index = 2;
    private boolean p1Ready = false;
    private boolean p2Ready = false;
    private int pvpStep = 1; // 1 = J1, 2 = J2
    private int j1ChosenCharacter = -1;

    // ====================================================================
    //  CONSTRUCTOR
    // ====================================================================
    public PanelSeleccionPersonaje(VentanaPrincipal ventana, GameWHGGUI gameOrchestrator) {
        this.ventana          = ventana;
        this.gameOrchestrator = gameOrchestrator;
        setLayout(null);  // Layout absoluto; doLayout() posiciona las tarjetas
        setBackground(Color.BLACK);
        cargarRecursos();
        construirUI();

        setFocusable(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                manejarTeclas(e.getKeyCode());
            }
        });
        
        // Timer de repintado a 60 FPS para animaciones de hover
        animTimer = new Timer(16, e -> repaint());
        animTimer.start();
    }

    public void resetSelection() {
        System.out.println("[DEBUG] PanelSeleccionPersonaje resetSelection. Modality = " + gameOrchestrator.getCurrentModality());
        this.p1Index = 0;
        this.p2Index = 2;
        this.p1Ready = false;
        this.p2Ready = false;
        this.pvpStep = 1;
        this.j1ChosenCharacter = -1;
        
        if (tarjetaRojo != null) tarjetaRojo.setVisible(true);
        if (tarjetaAzul != null) tarjetaAzul.setVisible(true);
        if (tarjetaVerde != null) tarjetaVerde.setVisible(true);
        if (btnVolver != null) btnVolver.setVisible(true);
        repaint();
    }

    // ====================================================================
    //  CARGA DE RECURSOS
    // ====================================================================
    private void cargarRecursos() {
        try {
            try {
                backgroundImage = ImageIO.read(new File("src/resources/images/fondo_SelectPlayer.png"));
                if (backgroundImage == null) {
                    throw new GameWHGException(GameWHGException.ERROR_CARGA_RECURSO);
                }

                File pvpPruebaFile = new File("src/resources/images/prueba_pvp.png");
                if (pvpPruebaFile.exists()) {
                    imagePruebaPvp = ImageIO.read(pvpPruebaFile);
                }

                Font base = Font.createFont(Font.TRUETYPE_FONT, new File("src/resources/fonts/HellraiserBloody.ttf"));
                titleFont    = base.deriveFont(Font.PLAIN, 70f);
                cardTitleFont = base.deriveFont(Font.PLAIN, 36f);

                cardStatsFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/resources/fonts/Luvable.ttf")).deriveFont(Font.PLAIN, 18f);
            } catch (FontFormatException | IOException e) {
                throw new GameWHGException(GameWHGException.ERROR_CARGA_RECURSO);
            }
        } catch (GameWHGException ex) {
            domain.Log.record(ex);
            System.err.println("Error de recurso: " + ex.getMessage());
            titleFont     = new Font("SansSerif", Font.BOLD, 60);
            cardTitleFont = new Font("SansSerif", Font.BOLD, 30);
            cardStatsFont = new Font("SansSerif", Font.PLAIN, 16);
        }
    }

    // ====================================================================
    //  CONSTRUCCION DE UI
    // ====================================================================
    private void construirUI() {
        // -- Tarjeta ROJO: Rafael
        String[] statsRojo = {
            "TAM:  ★★★☆☆  (Estándar)",
            "VEL:  ★★★☆☆  (Estándar)",
            " ",
            "El personaje de referencia.",
            "Equilibrado en todo."
        };
        tarjetaRojo = new TarjetaPersonaje(
            "RAFAEL",
            statsRojo,
            new Color(200, 40,  40),   // borde normal: rojo
            new Color(255, 110, 80),   // borde hover:  rojo brillante
            new Color(200, 40,  40, 120),
            PERSONAJE_ROJO,
            this
        );

        // -- Tarjeta AZUL: Leonardo
        String[] statsAzul = {
            "TAM:  ★★★★☆  (1.25x Grande)",
            "VEL:  ★★★★★  (1.5x Rápido)",
            " ",
            "Rápido y voluminoso.",
            "Difícil de esquivar obstáculos."
        };
        tarjetaAzul = new TarjetaPersonaje(
            "LEONARDO",
            statsAzul,
            new Color(30, 100, 200),   // borde normal: azul
            new Color(80, 170, 255),   // borde hover
            new Color(30, 100, 200, 120),
            PERSONAJE_AZUL,
            this
        );

        // -- Tarjeta VERDE: Miguel Angelo
        String[] statsVerde = {
            "TAM:  ★★★★☆  (1.25x → 1.5x)",
            "VEL:  ★★☆☆☆  (0.75x → 0.5x)",
            "ESCUDO: ✔ (1 golpe)",
            " ",
            "Al recibir daño, pierde el",
            "escudo, crece y se ralentiza."
        };
        tarjetaVerde = new TarjetaPersonaje(
            "MIGUEL ANGELO",
            statsVerde,
            new Color(30, 160, 60),    // borde normal: verde
            new Color(80, 240, 100),   // borde hover
            new Color(30, 160, 60, 120),
            PERSONAJE_VERDE,
            this
        );

        add(tarjetaRojo);
        add(tarjetaAzul);
        add(tarjetaVerde);

        // -- Boton VOLVER
        btnVolver = crearBotonVolver();
        add(btnVolver);
    }

    // ====================================================================
    //  LAYOUT ABSOLUTO (responde a cambios de tamanio de ventana)
    // ====================================================================
    @Override
    public void doLayout() {
        super.doLayout();
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        // Tarjetas: 3 columnas, centradas horizontalmente
        int cardW    = (int) (w * 0.26);
        int cardH    = (int) (h * 0.60);
        int gapX     = (int) (w * 0.04);
        int totalW   = cardW * 3 + gapX * 2;
        int startX   = (w - totalW) / 2;
        int cardY    = (int) (h * 0.22);

        tarjetaRojo .setBounds(startX,                         cardY, cardW, cardH);
        tarjetaAzul .setBounds(startX + cardW + gapX,         cardY, cardW, cardH);
        tarjetaVerde.setBounds(startX + (cardW + gapX) * 2,   cardY, cardW, cardH);

        // Boton Volver
        if (btnVolver != null) {
            int bW = 180, bH = 48;
            btnVolver.setBounds((w - bW) / 2, h - bH - 20, bW, bH);
        }
    }

    // ====================================================================
    //  RENDERIZADO DEL PANEL (fondo + titulo)
    // ====================================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("[DEBUG] PanelSeleccionPersonaje paintComponent. Modality = " + gameOrchestrator.getCurrentModality());
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Fondo
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(new Color(10, 10, 30));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // Titulo principal
        dibujarTitulo(g2d);
    }

    private void dibujarTitulo(Graphics2D g2d) {
        String titulo = "SELECT PLAYER";
        Color textoColor = new Color(220, 220, 255);
        boolean isPvP = gameOrchestrator.getCurrentModality() == domain.Modality.PVP;
        if (isPvP) {
            if (pvpStep == 1) {
                titulo = "SELECCION DE PERSONAJE J1";
                textoColor = new Color(255, 80, 80); // Rojo brillante
            } else {
                titulo = "SELECCION DE PERSONAJE J2";
                textoColor = new Color(80, 220, 255); // Cyan brillante
            }
        }
        g2d.setFont(titleFont);
        FontMetrics fm = g2d.getFontMetrics();
        int tx = (getWidth() - fm.stringWidth(titulo)) / 2;
        int ty = (int) (getHeight() * 0.17);

        // Sombra / contorno
        g2d.setColor(new Color(0, 0, 0, 180));
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                if (dx != 0 || dy != 0) g2d.drawString(titulo, tx + dx, ty + dy);
            }
        }
        // Texto principal
        g2d.setColor(textoColor);
        g2d.drawString(titulo, tx, ty);
        
        // Dibujar Subtítulo de Modalidad
        if (isPvP) {
            g2d.setFont(cardStatsFont.deriveFont(Font.BOLD, 22f));
            String sub = "MODALIDAD: JUGADOR VS JUGADOR (PvP)";
            FontMetrics fmSub = g2d.getFontMetrics();
            int sx = (getWidth() - fmSub.stringWidth(sub)) / 2;
            int sy = (int) (getHeight() * 0.07);
            
            // Sombra
            g2d.setColor(Color.BLACK);
            g2d.drawString(sub, sx + 2, sy + 2);
            
            g2d.setColor(new Color(46, 204, 113));
            g2d.drawString(sub, sx, sy);
        }

        // Dibujar Indicadores de Selección PvsP
        dibujarIndicadoresJugadores(g2d);
    }
    
    private void manejarTeclas(int keyCode) {
        boolean isPvP = gameOrchestrator.getCurrentModality() == domain.Modality.PVP;
        
        if (isPvP) {
            if (pvpStep == 1) {
                if (keyCode == java.awt.event.KeyEvent.VK_A || keyCode == java.awt.event.KeyEvent.VK_LEFT) {
                    p1Index = Math.max(0, p1Index - 1);
                }
                if (keyCode == java.awt.event.KeyEvent.VK_D || keyCode == java.awt.event.KeyEvent.VK_RIGHT) {
                    p1Index = Math.min(2, p1Index + 1);
                }
                if (keyCode == java.awt.event.KeyEvent.VK_E || keyCode == java.awt.event.KeyEvent.VK_ENTER || keyCode == java.awt.event.KeyEvent.VK_SPACE) {
                    j1ChosenCharacter = p1Index;
                    pvpStep = 2;
                }
            } else if (pvpStep == 2) {
                if (keyCode == java.awt.event.KeyEvent.VK_A || keyCode == java.awt.event.KeyEvent.VK_LEFT) {
                    p2Index = Math.max(0, p2Index - 1);
                }
                if (keyCode == java.awt.event.KeyEvent.VK_D || keyCode == java.awt.event.KeyEvent.VK_RIGHT) {
                    p2Index = Math.min(2, p2Index + 1);
                }
                if (keyCode == java.awt.event.KeyEvent.VK_E || keyCode == java.awt.event.KeyEvent.VK_ENTER || keyCode == java.awt.event.KeyEvent.VK_SPACE) {
                    gameOrchestrator.iniciarJuegoConPersonaje(j1ChosenCharacter, p2Index);
                }
            }
        } else {
            if (keyCode == java.awt.event.KeyEvent.VK_A || keyCode == java.awt.event.KeyEvent.VK_LEFT) {
                p1Index = Math.max(0, p1Index - 1);
            }
            if (keyCode == java.awt.event.KeyEvent.VK_D || keyCode == java.awt.event.KeyEvent.VK_RIGHT) {
                p1Index = Math.min(2, p1Index + 1);
            }
            if (keyCode == java.awt.event.KeyEvent.VK_E || keyCode == java.awt.event.KeyEvent.VK_ENTER || keyCode == java.awt.event.KeyEvent.VK_SPACE) {
                gameOrchestrator.iniciarJuegoConPersonaje(p1Index, 0);
            }
        }
        
        repaint();
    }

    private void dibujarIndicadoresJugadores(Graphics2D g2d) {
        boolean isPvP = gameOrchestrator.getCurrentModality() == domain.Modality.PVP;
        g2d.setFont(cardStatsFont.deriveFont(Font.BOLD, 24f));
        
        if (isPvP) {
            if (pvpStep == 1) {
                TarjetaPersonaje t1 = obtenerTarjeta(p1Index);
                if (t1 != null) {
                    int cx = t1.getX() + t1.getWidth() / 2;
                    int cy = t1.getY() - 20;
                    String text = "JUGADOR 1 (A/D)";
                    g2d.setColor(Color.RED);
                    FontMetrics fm = g2d.getFontMetrics();
                    g2d.drawString(text, cx - fm.stringWidth(text)/2, cy);
                }
            } else {
                TarjetaPersonaje t2 = obtenerTarjeta(p2Index);
                if (t2 != null) {
                    int cx = t2.getX() + t2.getWidth() / 2;
                    int cy = t2.getY() - 20;
                    String text = "JUGADOR 2 (< / >)";
                    g2d.setColor(Color.CYAN);
                    FontMetrics fm = g2d.getFontMetrics();
                    g2d.drawString(text, cx - fm.stringWidth(text)/2, cy);
                }
            }
        } else {
            TarjetaPersonaje t1 = obtenerTarjeta(p1Index);
            if (t1 != null) {
                int cx = t1.getX() + t1.getWidth() / 2;
                int cy = t1.getY() - 20;
                String text = "JUGADOR 1 (A/D)";
                g2d.setColor(Color.RED);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(text, cx - fm.stringWidth(text)/2, cy);
            }
        }
    }
    
    private TarjetaPersonaje obtenerTarjeta(int index) {
        if (index == 0) return tarjetaRojo;
        if (index == 1) return tarjetaAzul;
        if (index == 2) return tarjetaVerde;
        return null;
    }

    private JButton crearBotonVolver() {
        JButton btn = new JButton("VOLVER") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()
                        ? new Color(80, 80, 130)
                        : new Color(40, 40, 90));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(160, 160, 220));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);
                g2.setFont(cardTitleFont.deriveFont(22f));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFocusable(false); // Prevención de robo de foco del teclado
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> ventana.mostrarPanel("JUEGO")); // Regresar al Hub de Portales, no al Título
        return btn;
    }

    void seleccionarPersonaje(int tipo) {
        boolean isPvP = gameOrchestrator.getCurrentModality() == domain.Modality.PVP;
        if (isPvP) {
            if (pvpStep == 1) {
                j1ChosenCharacter = tipo;
                pvpStep = 2;
                repaint();
            } else if (pvpStep == 2) {
                gameOrchestrator.iniciarJuegoConPersonaje(j1ChosenCharacter, tipo);
            }
        } else {
            gameOrchestrator.iniciarJuegoConPersonaje(tipo, 0);
        }
    }

    // ====================================================================
    //  CLASE INTERNA: Tarjeta de Personaje
    // ====================================================================
    private class TarjetaPersonaje extends JPanel {

        private final String   nombre;
        private final String[] stats;
        private final Color    colorBordeNormal;
        private final Color    colorBordeHover;
        private final Color    colorFondoTint;
        private final int      tipoPersonaje;
        private final PanelSeleccionPersonaje owner;

        private boolean hovered    = false;
        private float   glowAlpha  = 0f;  // 0..1 brillo de hover

        TarjetaPersonaje(String nombre, String[] stats,
                         Color bordeNormal, Color bordeHover,
                         Color fondoTint, int tipoPersonaje,
                         PanelSeleccionPersonaje owner) {
            this.nombre          = nombre;
            this.stats           = stats;
            this.colorBordeNormal = bordeNormal;
            this.colorBordeHover  = bordeHover;
            this.colorFondoTint   = fondoTint;
            this.tipoPersonaje    = tipoPersonaje;
            this.owner            = owner;

            setOpaque(false);
            setFocusable(false); // Garantía de no intercepción del foco
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  }
                @Override public void mouseExited (MouseEvent e) { hovered = false; }
                @Override public void mouseClicked(MouseEvent e) { owner.seleccionarPersonaje(tipoPersonaje); }
            });
        }

        // -- Animacion suave del glow
        private void actualizarGlow() {
            if (hovered && glowAlpha < 1f) glowAlpha = Math.min(1f, glowAlpha + 0.08f);
            if (!hovered && glowAlpha > 0f) glowAlpha = Math.max(0f, glowAlpha - 0.06f);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            actualizarGlow();

            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int arc = 20;

            // --- FEAR CALCULATION ---
            float fearRatio = 0f;
            java.awt.Point mousePos = null;
            try {
                if (java.awt.MouseInfo.getPointerInfo() != null) {
                    mousePos = java.awt.MouseInfo.getPointerInfo().getLocation();
                    javax.swing.SwingUtilities.convertPointFromScreen(mousePos, this);
                }
            } catch (Exception e) {
                domain.Log.record(e);
            }
            
            if (mousePos != null) {
                double dist = Math.hypot(mousePos.x - w/2.0, mousePos.y - h/2.0);
                double maxDist = 350.0; // Distancia donde comienza el miedo
                if (dist < maxDist) {
                    fearRatio = (float) Math.pow(1.0 - (dist / maxDist), 1.5); // Curva exponencial para mayor intensidad de cerca
                }
            }

            // 1. Fondo semitransparente de la tarjeta (Se vuelve rojo con el miedo)
            int baseR = 10, baseG = 10, baseB = 30;
            int redR = 180, redG = 0, redB = 0;
            int finalR = (int) (baseR + (redR - baseR) * fearRatio);
            int finalG = (int) (baseG + (redG - baseG) * fearRatio);
            int finalB = (int) (baseB + (redB - baseB) * fearRatio);
            Color fondoBase = new Color(finalR, finalG, finalB, 200);
            g2d.setColor(fondoBase);
            g2d.fillRoundRect(0, 0, w, h, arc, arc);

            // 2. Tinte de color del personaje interpolado con el hover
            if (glowAlpha > 0f) {
                Color tintHover = new Color(
                    colorFondoTint.getRed(),
                    colorFondoTint.getGreen(),
                    colorFondoTint.getBlue(),
                    (int) (colorFondoTint.getAlpha() * glowAlpha));
                g2d.setColor(tintHover);
                g2d.fillRoundRect(0, 0, w, h, arc, arc);
            }

            // 3. Borde (normal -> hover con interpolacion lineal)
            Color borderColor = interpolarColor(colorBordeNormal, colorBordeHover, glowAlpha);
            
            boolean isSelectedByActive = false;
            Color activeColor = Color.RED;
            boolean isPvP = gameOrchestrator.getCurrentModality() == domain.Modality.PVP;
            if (isPvP) {
                if (pvpStep == 1 && tipoPersonaje == p1Index) {
                    isSelectedByActive = true;
                    activeColor = Color.RED;
                } else if (pvpStep == 2 && tipoPersonaje == p2Index) {
                    isSelectedByActive = true;
                    activeColor = Color.CYAN;
                }
            } else {
                if (tipoPersonaje == p1Index) {
                    isSelectedByActive = true;
                    activeColor = Color.RED;
                }
            }

            if (isSelectedByActive) {
                borderColor = activeColor;
            } else if (fearRatio > 0.5f) {
                borderColor = interpolarColor(borderColor, Color.RED, (fearRatio - 0.5f) * 2f);
            }

            float borderWidth = 2f + 3f * glowAlpha;
            if (isSelectedByActive) {
                borderWidth = 6f + 2f * glowAlpha; // Mas grueso para el cursor activo
            }
            g2d.setColor(borderColor);
            g2d.setStroke(new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawRoundRect(
                (int)(borderWidth / 2), (int)(borderWidth / 2),
                (int)(w - borderWidth), (int)(h - borderWidth),
                arc, arc);

            // 4. Avatar del personaje (cuadrado de color identificativo)
            int avatarSize = (int)(w * 0.55);
            int avatarX    = (w - avatarSize) / 2;
            int avatarY    = (int)(h * 0.07);
            
            // Efecto de temblor basado en el miedo
            if (fearRatio > 0) {
                int shakeIntensity = (int) (fearRatio * 8); 
                avatarX += (Math.random() - 0.5) * shakeIntensity;
                avatarY += (Math.random() - 0.5) * shakeIntensity;
            }

            dibujarAvatar(g2d, avatarX, avatarY, avatarSize, fearRatio);

            // 5. Nombre del personaje
            g2d.setFont(cardTitleFont);
            FontMetrics fmTitle = g2d.getFontMetrics();
            String nombreUpper = nombre;
            int nombreY = avatarY + avatarSize + (int)(h * 0.10);

            // Contorno sombra
            g2d.setColor(Color.BLACK);
            g2d.drawString(nombreUpper, (w - fmTitle.stringWidth(nombreUpper)) / 2 + 2, nombreY + 2);
            // Texto principal
            g2d.setColor(interpolarColor(Color.WHITE, colorBordeHover, glowAlpha));
            g2d.drawString(nombreUpper, (w - fmTitle.stringWidth(nombreUpper)) / 2, nombreY);

            // 6. Estadisticas
            g2d.setFont(cardStatsFont);
            FontMetrics fmStats = g2d.getFontMetrics();
            int statsStartY = nombreY + (int)(h * 0.09);
            int lineH = fmStats.getHeight() + 4;
            for (int i = 0; i < stats.length; i++) {
                String stat = stats[i];
                int sx = (w - fmStats.stringWidth(stat)) / 2;
                int sy = statsStartY + i * lineH;
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.drawString(stat, sx + 1, sy + 1);
                g2d.setColor(new Color(200, 220, 255));
                g2d.drawString(stat, sx, sy);
            }

            // 7. Indicador "CLICK" al hacer hover
            if (glowAlpha > 0.3f) {
                String hint = "[ SELECCIONAR ]";
                g2d.setFont(cardStatsFont.deriveFont(14f));
                FontMetrics fmHint = g2d.getFontMetrics();
                int hintX = (w - fmHint.stringWidth(hint)) / 2;
                int hintY = h - 14;
                g2d.setColor(new Color(255, 255, 255, (int)(180 * glowAlpha)));
                g2d.drawString(hint, hintX, hintY);
            }

            // 8. Banner "J1 ELEGIDO" si J1 seleccionó este personaje y estamos en la selección de J2
            if (isPvP && pvpStep == 2 && tipoPersonaje == j1ChosenCharacter) {
                // Dibujar overlay rojo semitransparente
                g2d.setColor(new Color(225, 40, 40, 100));
                g2d.fillRoundRect(0, 0, w, h, arc, arc);

                // Dibujar banner oblicuo o centrado
                g2d.setColor(new Color(180, 20, 20));
                g2d.fillRect(0, h/2 - 25, w, 50);

                g2d.setColor(Color.WHITE);
                g2d.setFont(cardTitleFont.deriveFont(Font.BOLD, 22f));
                FontMetrics fmChosen = g2d.getFontMetrics();
                String chosenText = "J1 CONFIRMADO";
                int cx = (w - fmChosen.stringWidth(chosenText)) / 2;
                int cy = h/2 + fmChosen.getAscent()/2 - 4;
                
                // Sombra de texto
                g2d.setColor(Color.BLACK);
                g2d.drawString(chosenText, cx + 2, cy + 2);
                
                g2d.setColor(Color.WHITE);
                g2d.drawString(chosenText, cx, cy);
            }

            g2d.dispose();
        }

        private void dibujarAvatar(Graphics2D g2d, int x, int y, int size, float fearRatio) {
            Color avatarColor;
            switch (tipoPersonaje) {
                case PERSONAJE_ROJO:   avatarColor = new Color(220,  60,  60); break;
                case PERSONAJE_AZUL:   avatarColor = new Color( 60, 130, 220); break;
                case PERSONAJE_VERDE:  avatarColor = new Color( 50, 190,  80); break;
                default:               avatarColor = Color.GRAY;
            }

            // Sombra
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.fillRect(x + 4, y + 4, size, size);

            // Fondo negro (borde grueso)
            g2d.setColor(Color.BLACK);
            g2d.fillRect(x, y, size, size);

            // Cuadrado de color interior
            int border = Math.max(2, size / 16);
            g2d.setColor(avatarColor);
            g2d.fillRect(x + border, y + border, size - 2*border, size - 2*border);

            // Brillo superior para mantener estilo UI
            g2d.setColor(new Color(255, 255, 255, 60));
            g2d.fillRect(x + border, y + border, size - 2*border, (size - 2*border) / 3);

            // --- ANIMACION DE OJOS SIGUIENDO EL CURSOR ---
            java.awt.Point mousePos = null;
            try {
                if (java.awt.MouseInfo.getPointerInfo() != null) {
                    mousePos = java.awt.MouseInfo.getPointerInfo().getLocation();
                    javax.swing.SwingUtilities.convertPointFromScreen(mousePos, this);
                }
            } catch (Exception e) {
                domain.Log.record(e);
            }

            int mouseX = mousePos != null ? mousePos.x : (x + size / 2);
            int mouseY = mousePos != null ? mousePos.y : (y + size);

            int eyeRadius = (int) (size * 0.16);
            int eyeY = y + (int) (size * 0.35);

            int eyeLeftCX = x + (int) (size * 0.35);
            int eyeRightCX = x + (int) (size * 0.65);

            // Escleróticas
            g2d.setColor(Color.WHITE);
            g2d.fillOval(eyeLeftCX - eyeRadius, eyeY - eyeRadius, eyeRadius * 2, eyeRadius * 2);
            g2d.fillOval(eyeRightCX - eyeRadius, eyeY - eyeRadius, eyeRadius * 2, eyeRadius * 2);

            g2d.setColor(Color.BLACK);
            java.awt.Stroke oldStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawOval(eyeLeftCX - eyeRadius, eyeY - eyeRadius, eyeRadius * 2, eyeRadius * 2);
            g2d.drawOval(eyeRightCX - eyeRadius, eyeY - eyeRadius, eyeRadius * 2, eyeRadius * 2);
            g2d.setStroke(oldStroke);

            // Pupilas (se encogen con el miedo)
            int pupilRadius = (int) (eyeRadius * (0.45f - 0.30f * fearRatio));
            double maxDist = eyeRadius - pupilRadius - 1;
            if (maxDist < 0) maxDist = 0;

            // Pupila izquierda
            double dxL = mouseX - eyeLeftCX;
            double dyL = mouseY - eyeY;
            double distL = Math.hypot(dxL, dyL);
            if (distL > maxDist) {
                dxL = dxL * maxDist / distL;
                dyL = dyL * maxDist / distL;
            }
            g2d.setColor(Color.BLACK);
            g2d.fillOval((int) (eyeLeftCX + dxL - pupilRadius), (int) (eyeY + dyL - pupilRadius), pupilRadius * 2, pupilRadius * 2);

            // Pupila derecha
            double dxR = mouseX - eyeRightCX;
            double dyR = mouseY - eyeY;
            double distR = Math.hypot(dxR, dyR);
            if (distR > maxDist) {
                dxR = dxR * maxDist / distR;
                dyR = dyR * maxDist / distR;
            }
            g2d.fillOval((int) (eyeRightCX + dxR - pupilRadius), (int) (eyeY + dyR - pupilRadius), pupilRadius * 2, pupilRadius * 2);
        }

        // Interpolacion lineal entre dos colores
        private Color interpolarColor(Color c1, Color c2, float t) {
            t = Math.max(0f, Math.min(1f, t));
            int r = (int)(c1.getRed()   + (c2.getRed()   - c1.getRed())   * t);
            int gv = (int)(c1.getGreen() + (c2.getGreen() - c1.getGreen()) * t);
            int b = (int)(c1.getBlue()  + (c2.getBlue()  - c1.getBlue())  * t);
            return new Color(r, gv, b);
        }
    }

}