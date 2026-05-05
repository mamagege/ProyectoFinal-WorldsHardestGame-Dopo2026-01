package presentation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

/**
 * Representa el menú de inicio del juego.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class PanelMenu extends JPanel {
    private VentanaPrincipal ventana;
    private Image backgroundImage;
    private Image skullImage;
    private Timer animationTimer;
    private long startTime;

    public PanelMenu(VentanaPrincipal ventana) {
        this.ventana = ventana;
        
        // Cargar imágenes de las capas
        try {
            backgroundImage = ImageIO.read(new File("src/resources/images/fondo_muro.png"));
        } catch (IOException e) {
            System.err.println("Advertencia: No se pudo cargar la imagen src/resources/images/fondo_muro.png");
        }
        
        try {
            skullImage = ImageIO.read(new File("src/resources/images/calavera.png"));
        } catch (IOException e) {
            System.err.println("Advertencia: No se pudo cargar la imagen src/resources/images/calavera.png");
        }

        // Configurar Layout (GridBagLayout para posicionar botones abajo)
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(0, 0, 40, 0); // Margen inferior

        // Panel contenedor para los botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 50, 15)); // 2 filas, 2 columnas, hgap, vgap
        buttonPanel.setOpaque(false); // Transparente para ver capas inferiores

        // Instanciar botones nuevos
        JButton btnNuevaPartida = crearBoton("NUEVA PARTIDA");
        JButton btnContinuar = crearBoton("CONTINUAR");
        JButton btnOpciones = crearBoton("OPCIONES");
        JButton btnSalir = crearBoton("SALIR");

        // Configurar acciones
        btnNuevaPartida.addActionListener(e -> ventana.mostrarPanel("JUEGO"));
        btnSalir.addActionListener(e -> System.exit(0));

        // Añadir botones al panel (GridLayout los acomoda fila por fila, de izquierda a derecha)
        buttonPanel.add(btnNuevaPartida); // Fila 1, Columna 1
        buttonPanel.add(btnOpciones);     // Fila 1, Columna 2
        buttonPanel.add(btnContinuar);    // Fila 2, Columna 1
        buttonPanel.add(btnSalir);        // Fila 2, Columna 2

        // Añadir contenedor de botones al panel principal
        add(buttonPanel, gbc);

        // Iniciar timer para la animación a 60 FPS (~16ms)
        startTime = System.currentTimeMillis();
        animationTimer = new Timer(16, e -> repaint());
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Al llamar a super.paintComponent, Swing se encarga de limpiar el panel
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Renderizado de alta calidad
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // ---------------------------------------------------------
        // CAPA 1: Fondo Estático (Z-index más bajo)
        // ---------------------------------------------------------
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(new Color(15, 15, 15));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // ---------------------------------------------------------
        // CAPA 2: Calavera Animada (Z-index medio)
        // ---------------------------------------------------------
        if (skullImage != null) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            
            // Animación: Modificar solo 'Y' usando Math.sin
            double amplitud = 8.0;
            double velocidad = 400.0;
            int yOffset = (int) (Math.sin(elapsedTime / velocidad) * amplitud);

            int originalW = skullImage.getWidth(null);
            int originalH = skullImage.getHeight(null);
            
            // Escalar la calavera para que quepa bien en la mitad superior
            int drawHeight = 220; 
            int drawWidth = (originalW > 0 && originalH > 0) ? (originalW * drawHeight) / originalH : 220;

            int x = (getWidth() - drawWidth) / 2;
            int y = 40 + yOffset; // Posición base Y = 40 + animación

            g2d.drawImage(skullImage, x, y, drawWidth, drawHeight, this);
        }

        // ---------------------------------------------------------
        // CAPA 3: Texto e Interfaz (Z-index más alto)
        // ---------------------------------------------------------
        // Los botones (JButton) se renderizarán automáticamente encima 
        // de esto gracias a que son componentes hijos de Swing.
        
        // Dibujamos el Título mediante código debajo de la calavera
        String t1 = "THE ";
        String t2 = "DOPO ";
        String t3 = "HARDEST GAME";

        Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 46);
        g2d.setFont(titleFont);
        FontMetrics fm = g2d.getFontMetrics();
        
        int w1 = fm.stringWidth(t1);
        int w2 = fm.stringWidth(t2);
        int w3 = fm.stringWidth(t3);
        int totalWidth = w1 + w2 + w3;
        
        int startX = (getWidth() - totalWidth) / 2;
        int textY = 320; // Posición Y del texto (debajo de la calavera)

        // Sombra blanca sutil para resaltar sobre el fondo
        g2d.setColor(new Color(255, 255, 255, 200));
        int offset = 2;
        g2d.drawString(t1, startX + offset, textY + offset);
        g2d.drawString(t2, startX + w1 + offset, textY + offset);
        g2d.drawString(t3, startX + w1 + w2 + offset, textY + offset);

        // Texto principal (Negro y Rojo)
        g2d.setColor(new Color(25, 25, 25)); // Gris muy oscuro / Negro
        g2d.drawString(t1, startX, textY);
        
        g2d.setColor(new Color(200, 20, 20)); // Rojo para "DOPO"
        g2d.drawString(t2, startX + w1, textY);
        
        g2d.setColor(new Color(25, 25, 25)); // Gris muy oscuro / Negro
        g2d.drawString(t3, startX + w1 + w2, textY);

        g2d.dispose();
    }

    /**
     * Método auxiliar para centralizar la configuración visual de los botones.
     */
    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        boton.setForeground(new Color(220, 220, 220)); // Gris claro
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        boton.setBorderPainted(false); // Quitar borde predeterminado
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setForeground(new Color(220, 20, 20)); // Rojo al hacer hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setForeground(new Color(220, 220, 220)); // Volver al gris claro
            }
        });

        return boton;
    }
}
