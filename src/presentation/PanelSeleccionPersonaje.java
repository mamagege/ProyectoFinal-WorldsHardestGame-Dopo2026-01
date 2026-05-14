package presentation;

import domain.RedCharacter;
import domain.BlueCharacter;
import domain.GreenCharacter;

import javax.imageio.ImageIO;
import javax.swing.*;
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
    private final GameWHG          gameOrchestrator;

    // ------------------------------------------------------------------ //
    //  Recursos graficos
    // ------------------------------------------------------------------ //
    private BufferedImage backgroundImage;
    private Font          titleFont;
    private Font          cardTitleFont;
    private Font          cardStatsFont;

    // ------------------------------------------------------------------ //
    //  Tarjetas de personaje
    // ------------------------------------------------------------------ //
    private TarjetaPersonaje tarjetaRojo;
    private TarjetaPersonaje tarjetaAzul;
    private TarjetaPersonaje tarjetaVerde;

    // ------------------------------------------------------------------ //
    //  Animacion hover de las tarjetas
    // ------------------------------------------------------------------ //
    private Timer animTimer;

    // ====================================================================
    //  CONSTRUCTOR
    // ====================================================================
    public PanelSeleccionPersonaje(VentanaPrincipal ventana, GameWHG gameOrchestrator) {
        this.ventana          = ventana;
        this.gameOrchestrator = gameOrchestrator;
        setLayout(null);  // Layout absoluto; doLayout() posiciona las tarjetas
        setBackground(Color.BLACK);
        cargarRecursos();
        construirUI();

        // Timer de repintado a 60 FPS para animaciones de hover
        animTimer = new Timer(16, e -> repaint());
        animTimer.start();
    }

    // ====================================================================
    //  CARGA DE RECURSOS
    // ====================================================================
    private void cargarRecursos() {
        try {
            backgroundImage = ImageIO.read(new File("src/resources/images/fondo_SelectPlayer.png"));
        } catch (IOException e) {
            System.err.println("Advertencia: no se pudo cargar fondo_SelectPlayer.png");
        }
        try {
            Font base = Font.createFont(Font.TRUETYPE_FONT,
                            new File("src/resources/fonts/HellraiserBloody.ttf"));
            titleFont    = base.deriveFont(Font.PLAIN, 70f);
            cardTitleFont = base.deriveFont(Font.PLAIN, 36f);
        } catch (FontFormatException | IOException e) {
            titleFont     = new Font("SansSerif", Font.BOLD, 60);
            cardTitleFont = new Font("SansSerif", Font.BOLD, 30);
        }
        try {
            cardStatsFont = Font.createFont(Font.TRUETYPE_FONT,
                                new File("src/resources/fonts/Luvable.ttf"))
                                .deriveFont(Font.PLAIN, 18f);
        } catch (FontFormatException | IOException e) {
            cardStatsFont = new Font("SansSerif", Font.PLAIN, 16);
        }
    }

    // ====================================================================
    //  CONSTRUCCION DE UI
    // ====================================================================
    private void construirUI() {
        // -- Tarjeta ROJO: Rafael
        String[] statsRojo = {
            "VEL: Media (0.85x)",
            "TAM: Medio  (0.75)",
            "ARMADURA: No"
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
            "VEL: Alta  (1.5x)",
            "TAM: Alto  (1.125)",
            "ARMADURA: No"
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
            "VEL: Baja  (1.0 -> 0.7x)",
            "TAM: Medio (0.75)",
            "ARMADURA: Si (1 golpe)"
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
        JButton btnVolver = crearBotonVolver();
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
        Component volver = getComponent(3);
        int bW = 180, bH = 48;
        volver.setBounds((w - bW) / 2, h - bH - 20, bW, bH);
    }

    // ====================================================================
    //  RENDERIZADO DEL PANEL (fondo + titulo)
    // ====================================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
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
        g2d.setColor(new Color(220, 220, 255));
        g2d.drawString(titulo, tx, ty);
    }

    // ====================================================================
    //  BOTON VOLVER
    // ====================================================================
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
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> ventana.mostrarPanel("MENU"));
        return btn;
    }

    // ====================================================================
    //  CALLBACK: el jugador eligio un personaje
    // ====================================================================
    void seleccionarPersonaje(int tipo) {
        gameOrchestrator.iniciarJuegoConPersonaje(tipo);
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

            // 1. Fondo semitransparente de la tarjeta
            Color fondoBase = new Color(10, 10, 30, 200);
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
            float borderWidth = 2f + 3f * glowAlpha;
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
            dibujarAvatar(g2d, avatarX, avatarY, avatarSize);

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

            g2d.dispose();
        }

        private void dibujarAvatar(Graphics2D g2d, int x, int y, int size) {
            // Circulo base del color del personaje
            Color avatarColor;
            switch (tipoPersonaje) {
                case PERSONAJE_ROJO:   avatarColor = new Color(220,  60,  60); break;
                case PERSONAJE_AZUL:   avatarColor = new Color( 60, 130, 220); break;
                case PERSONAJE_VERDE:  avatarColor = new Color( 50, 190,  80); break;
                default:               avatarColor = Color.GRAY;
            }

            // Sombra
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.fillOval(x + 4, y + 4, size, size);

            // Cuerpo
            g2d.setColor(avatarColor);
            g2d.fillOval(x, y, size, size);

            // Brillo superior
            g2d.setColor(new Color(255, 255, 255, 60));
            g2d.fillOval(x + size / 8, y + size / 12, size / 2, size / 3);

            // Borde
            g2d.setColor(avatarColor.brighter());
            g2d.setStroke(new BasicStroke(2.5f));
            g2d.drawOval(x, y, size, size);

            // Cara: ojos blancos
            int eyeR = size / 9;
            int eyeY = y + (int)(size * 0.38);
            int eyeLeftX  = x + (int)(size * 0.28) - eyeR;
            int eyeRightX = x + (int)(size * 0.72) - eyeR;

            g2d.setColor(Color.WHITE);
            g2d.fillOval(eyeLeftX,  eyeY, eyeR * 2, eyeR * 2);
            g2d.fillOval(eyeRightX, eyeY, eyeR * 2, eyeR * 2);

            // Pupilas
            int pupilR = eyeR / 2;
            g2d.setColor(Color.BLACK);
            g2d.fillOval(eyeLeftX  + eyeR - pupilR / 2, eyeY + eyeR - pupilR / 2, pupilR, pupilR);
            g2d.fillOval(eyeRightX + eyeR - pupilR / 2, eyeY + eyeR - pupilR / 2, pupilR, pupilR);

            // Boca (distinta segun tipo: sonrisa, seria, escudo)
            g2d.setStroke(new BasicStroke(2.2f));
            int mouthW  = size / 4;
            int mouthX  = x + size / 2 - mouthW / 2;
            int mouthY  = eyeY + eyeR * 2 + (int)(size * 0.10);
            int mouthH  = size / 7;

            switch (tipoPersonaje) {
                case PERSONAJE_ROJO:  // Sonrisa media
                    g2d.drawArc(mouthX, mouthY, mouthW, mouthH, 180, 180);
                    break;
                case PERSONAJE_AZUL:  // Boca abierta amplia (arrogante)
                    g2d.drawArc(mouthX - mouthW / 4, mouthY, mouthW + mouthW / 2, mouthH + 4, 180, 180);
                    break;
                case PERSONAJE_VERDE: // Escudo pequeño abajo del avatar
                    g2d.drawArc(mouthX, mouthY, mouthW, mouthH, 180, 180);
                    // Icono de escudo
                    int shX = x + size / 2 - 8;
                    int shY = y + size - 14;
                    int shW = 16, shH = 14;
                    g2d.setColor(new Color(80, 230, 100));
                    int[] shieldX = { shX, shX + shW / 2, shX + shW };
                    int[] shieldY = { shY, shY + shH, shY };
                    g2d.fillPolygon(shieldX, shieldY, 3);
                    g2d.setColor(new Color(40, 120, 50));
                    g2d.drawPolygon(shieldX, shieldY, 3);
                    break;
            }
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
