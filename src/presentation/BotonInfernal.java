package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Componente visual "BotonInfernal" que materializa el concepto de Ghost Button.
 * Implementa el efecto de incandescencia espectral (Glow) mediante Graphics2D
 * y transformaciones de composición alfa basadas en eventos de proximidad del cursor.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026 - Dante's Edition
 */
public class BotonInfernal extends JButton {
    private Color colorHover;
    private boolean isHovered = false;
    private static final Color COLOR_NORMAL = new Color(150, 150, 150); // Gris Ceniza

    /**
     * Constructor especializado para forjar botones del abismo.
     * 
     * @param texto Contenido visual del botón.
     * @param fuente Tipografía base que se derivará al tamaño óptimo.
     * @param colorHover Color de la emanación incandescente al detectar cursor.
     */
    public BotonInfernal(String texto, Font fuente, Color colorHover) {
        super(texto);
        this.colorHover = colorHover;
        
        // Aplicar y derivar tipografía base
        setFont((fuente != null) ? fuente.deriveFont(40f) : new Font(Font.SERIF, Font.BOLD, 40));

        // Configuración de estilo "Ghost Button" transparente y limpio
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setAlignmentX(Component.CENTER_ALIGNMENT);

        // Orquestador de estados visuales locales
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        // Margen extra para acomodar la expansión del brillo espectral
        int w = fm.stringWidth(getText()) + 40;
        int h = fm.getHeight() + 20;
        return new Dimension(w, h);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Activar Antialiasing de alta calidad para texto complejo
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(getFont());
        
        FontMetrics fm = g2d.getFontMetrics();
        String text = getText();
        int textX = (getWidth() - fm.stringWidth(text)) / 2;
        int textY = (getHeight() + fm.getAscent()) / 2 - 4;

        if (isHovered) {
            // RENDERIZADO ESPECTRAL (GLOW EFFECT)
            Composite originalComp = g2d.getComposite();
            g2d.setColor(colorHover);
            
            // Generar aura de baja opacidad dispersa
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
            int spread = 2; // Radio de dispersión lumínica
            for (int dx = -spread; dx <= spread; dx++) {
                for (int dy = -spread; dy <= spread; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    g2d.drawString(text, textX + dx, textY + dy);
                }
            }
            
            // Retornar a opacidad total y dibujar núcleo central brillante
            g2d.setComposite(originalComp);
            g2d.setColor(colorHover);
        } else {
            // ESTADO REPOSO
            g2d.setColor(COLOR_NORMAL);
        }

        // Dibujar núcleo de texto
        g2d.drawString(text, textX, textY);

        g2d.dispose();
    }
}
