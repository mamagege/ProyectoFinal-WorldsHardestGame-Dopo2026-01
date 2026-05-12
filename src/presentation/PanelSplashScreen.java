package presentation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Pantalla de carga cinemática con secuencia de fundido (Fade In / Hold / Fade Out).
 */
public class PanelSplashScreen extends JPanel {
    private VentanaPrincipal ventana;
    private BufferedImage splashImage;
    private String nextPanelTarget;
    private float alpha = 0f;
    private Timer animationTimer;
    private long startTime;

    // Duraciones en milisegundos
    private static final int FADE_IN_DURATION = 1600;
    private static final int HOLD_DURATION = 800;
    private static final int FADE_OUT_DURATION = 1600;

    public PanelSplashScreen(VentanaPrincipal ventana, String imagePath, String nextPanel) {
        this.ventana = ventana;
        this.nextPanelTarget = nextPanel;
        setBackground(Color.BLACK);

        try {
            splashImage = ImageIO.read(new File("src/resources/images/" + imagePath));
        } catch (IOException e) {
            System.err.println("Error cargando splash screen image: " + e.getMessage());
        }

        // Preparar motor de animación
        animationTimer = new Timer(16, e -> updateSequence());
    }

    /**
     * Inicia o reinicia la secuencia de animación cinemática.
     */
    public void startSequence() {
        this.alpha = 0f;
        this.startTime = System.currentTimeMillis();
        animationTimer.restart();
    }

    private void updateSequence() {
        long elapsed = System.currentTimeMillis() - startTime;

        if (elapsed < FADE_IN_DURATION) {
            alpha = (float) elapsed / FADE_IN_DURATION;
        } else if (elapsed < FADE_IN_DURATION + HOLD_DURATION) {
            alpha = 1.0f;
        } else if (elapsed < FADE_IN_DURATION + HOLD_DURATION + FADE_OUT_DURATION) {
            long fadeOutElapsed = elapsed - (FADE_IN_DURATION + HOLD_DURATION);
            alpha = 1.0f - ((float) fadeOutElapsed / FADE_OUT_DURATION);
        } else {
            alpha = 0f;
            animationTimer.stop();
            // Transición al destino dinámico en el hilo visual de Swing
            SwingUtilities.invokeLater(() -> ventana.mostrarPanel(nextPanelTarget));
            return;
        }

        // Clamp alpha por seguridad defensiva
        alpha = Math.max(0f, Math.min(1.0f, alpha));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Habilitar interpolación bilineal para máxima calidad visual
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (splashImage != null) {
            Composite oldComp = g2d.getComposite();
            
            // Aplicar transparencia dinámica de la secuencia
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            
            // Pintar cubriendo toda la ventana (Stretch to Fullscreen)
            g2d.drawImage(splashImage, 0, 0, getWidth(), getHeight(), null);
            
            g2d.setComposite(oldComp);
        }
    }
}
