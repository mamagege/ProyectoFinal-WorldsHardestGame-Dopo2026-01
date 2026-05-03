package presentation;

import domain.JuegoDOPO;
import domain.Posicion;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Controlador que conecta las entradas del usuario (teclado) con el Modelo.
 * Escucha las teclas de flechas direccionales y actualiza el juego.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class ControladorJuego extends KeyAdapter {
    private PanelJuego vista;
    private JuegoDOPO modelo;

    public ControladorJuego(PanelJuego vista, JuegoDOPO modelo) {
        this.vista = vista;
        this.modelo = modelo;
    }

    /**
     * Se invoca cuando una tecla física es presionada.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // No hacer nada si el nivel ya se completó
        if (modelo.isNivelCompletado()) {
            return;
        }

        int keyCode = e.getKeyCode();
        Posicion posActual = modelo.getJugador().getPosicion();
        int nuevoX = posActual.getX();
        int nuevoY = posActual.getY();

        // Determinar la nueva coordenada deseada según la flecha presionada
        if (keyCode == KeyEvent.VK_UP) {
            nuevoY -= 1;
        } else if (keyCode == KeyEvent.VK_DOWN) {
            nuevoY += 1;
        } else if (keyCode == KeyEvent.VK_LEFT) {
            nuevoX -= 1;
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            nuevoX += 1;
        } else {
            // Si presionó otra tecla, ignoramos
            return;
        }

        Posicion nuevaPosicion = new Posicion(nuevoX, nuevoY);

        // Actualizar el estado del dominio
        modelo.moverJugador(nuevaPosicion);

        // Reflejar los cambios en la interfaz gráfica
        vista.actualizarInterfaz();
    }
}
