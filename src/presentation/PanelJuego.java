package presentation;

import domain.EnemigoBasico;
import domain.JuegoDOPO;
import domain.Jugador;
import domain.Moneda;
import domain.Pared;
import domain.Posicion;
import domain.ZonaSegura;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Panel encargado de renderizar el nivel actual del juego.
 * Mantiene una referencia al Modelo (JuegoDOPO) para dibujar su estado.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class PanelJuego extends JPanel {
    private VentanaPrincipal ventana;
    private JuegoDOPO modelo;
    
    // Tamaño en píxeles de cada celda de la grilla
    private final int TAMANO_CELDA = 40;

    private JLabel labelMuertes;
    private JLabel labelNivel;

    public PanelJuego(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(Color.decode("#E6E6FA")); // Fondo lavanda claro

        // Inicializamos un modelo de prueba básico
        inicializarModeloPrueba();

        // Crear la barra superior
        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setBackground(Color.LIGHT_GRAY);

        JButton btnMenu = new JButton("MENU");
        btnMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.mostrarPanel("MENU");
            }
        });

        labelNivel = new JLabel("Nivel: 1/30", JLabel.CENTER);
        labelNivel.setFont(new Font("Arial", Font.BOLD, 16));

        labelMuertes = new JLabel("Deaths: 0  ");
        labelMuertes.setFont(new Font("Arial", Font.BOLD, 16));

        barraSuperior.add(btnMenu, BorderLayout.WEST);
        barraSuperior.add(labelNivel, BorderLayout.CENTER);
        barraSuperior.add(labelMuertes, BorderLayout.EAST);

        add(barraSuperior, BorderLayout.NORTH);

        // Inicializar y agregar el Controlador para el teclado
        ControladorJuego controlador = new ControladorJuego(this, modelo);
        addKeyListener(controlador);
        setFocusable(true); // Indispensable para capturar eventos de teclado
    }

    /**
     * Crea un nivel de prueba temporal utilizando las clases de dominio.
     */
    private void inicializarModeloPrueba() {
        Jugador jugador = new Jugador(new Posicion(1, 1));
        
        java.util.List<EnemigoBasico> enemigos = new ArrayList<>();
        enemigos.add(new EnemigoBasico(new Posicion(5, 5), 1, true));
        enemigos.add(new EnemigoBasico(new Posicion(4, 3), 1, false));
        
        java.util.List<Moneda> monedas = new ArrayList<>();
        monedas.add(new Moneda(new Posicion(8, 3)));
        
        java.util.List<Pared> paredes = new ArrayList<>();
        // Crear un marco de paredes simple
        for(int i = 0; i < 11; i++){
            paredes.add(new Pared(new Posicion(i, 0))); // Borde superior
            paredes.add(new Pared(new Posicion(i, 10))); // Borde inferior
            paredes.add(new Pared(new Posicion(0, i))); // Borde izquierdo
            paredes.add(new Pared(new Posicion(10, i))); // Borde derecho
        }
        
        java.util.List<ZonaSegura> zonasSeguras = new ArrayList<>();
        zonasSeguras.add(new ZonaSegura(new Posicion(1, 1), true, false));
        zonasSeguras.add(new ZonaSegura(new Posicion(8, 8), false, true));

        modelo = new JuegoDOPO(jugador, enemigos, monedas, paredes, zonasSeguras);
    }

    /**
     * Llama a repintar el componente y actualiza los textos de la barra superior.
     */
    public void actualizarInterfaz() {
        labelMuertes.setText("Deaths: " + modelo.getJugador().getMuertes() + "  ");
        repaint(); // Invoca a paintComponent de nuevo
    }

    /**
     * Método central de dibujado de Swing.
     * Separa estrictamente la representación gráfica de la lógica del juego.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Convertir a Graphics2D para métodos más avanzados si es necesario
        Graphics2D g2d = (Graphics2D) g;
        
        // Offset para centrar la grilla en la pantalla
        int offsetX = 100;
        int offsetY = 50;

        // 1. Renderizar Zonas Seguras (Fondo Verde)
        g2d.setColor(Color.decode("#B5E61D")); 
        for (ZonaSegura zona : modelo.getZonasSeguras()) {
            Posicion p = zona.getPosicion();
            g2d.fillRect(offsetX + p.getX() * TAMANO_CELDA, offsetY + p.getY() * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);
        }

        // 2. Renderizar Paredes (Gris/Negro)
        g2d.setColor(Color.DARK_GRAY);
        for (Pared pared : modelo.getParedes()) {
            Posicion p = pared.getPosicion();
            g2d.fillRect(offsetX + p.getX() * TAMANO_CELDA, offsetY + p.getY() * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);
            // Dibujar un borde negro para distinguir cada bloque
            g2d.setColor(Color.BLACK);
            g2d.drawRect(offsetX + p.getX() * TAMANO_CELDA, offsetY + p.getY() * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);
            g2d.setColor(Color.DARK_GRAY); // Volver al color original para el siguiente
        }

        // 3. Renderizar Monedas (Círculos Amarillos)
        g2d.setColor(Color.YELLOW);
        for (Moneda moneda : modelo.getMonedas()) {
            if (!moneda.isRecolectada()) {
                Posicion p = moneda.getPosicion();
                // Dibujar círculo centrado en la celda
                int padding = 10;
                int radio = TAMANO_CELDA - (padding * 2);
                g2d.fillOval(offsetX + p.getX() * TAMANO_CELDA + padding, offsetY + p.getY() * TAMANO_CELDA + padding, radio, radio);
            }
        }

        // 4. Renderizar Enemigos Básicos (Círculos Azules)
        g2d.setColor(Color.BLUE);
        for (EnemigoBasico enemigo : modelo.getEnemigos()) {
            Posicion p = enemigo.getPosicion();
            int padding = 5;
            int radio = TAMANO_CELDA - (padding * 2);
            g2d.fillOval(offsetX + p.getX() * TAMANO_CELDA + padding, offsetY + p.getY() * TAMANO_CELDA + padding, radio, radio);
        }

        // 5. Renderizar Jugador (Cuadrado Rojo)
        g2d.setColor(Color.RED);
        Posicion pJugador = modelo.getJugador().getPosicion();
        int pJug = 5;
        int tamJugador = TAMANO_CELDA - (pJug * 2);
        g2d.fillRect(offsetX + pJugador.getX() * TAMANO_CELDA + pJug, offsetY + pJugador.getY() * TAMANO_CELDA + pJug, tamJugador, tamJugador);
        
        // Borde negro para el jugador (clásico de Worlds Hardest Game)
        g2d.setColor(Color.BLACK);
        g2d.drawRect(offsetX + pJugador.getX() * TAMANO_CELDA + pJug, offsetY + pJugador.getY() * TAMANO_CELDA + pJug, tamJugador, tamJugador);
    }
}
