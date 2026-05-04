package presentation;

import domain.Level;
import domain.Character;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Controlador MVC.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class ControladorJuego extends KeyAdapter {
    private PanelJuego vista;
    private GameWHG gameOrchestrator;

    public ControladorJuego(PanelJuego vista, GameWHG gameOrchestrator) {
        this.vista = vista;
        this.gameOrchestrator = gameOrchestrator;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Level level = gameOrchestrator.getCurrentLevel();
        if (level == null || level.isCompleted()) {
            return;
        }

        int keyCode = e.getKeyCode();
        Character pJugador = level.getCharacter();
        int nuevoX = pJugador.getPositionX();
        int nuevoY = pJugador.getPositionY();

        if (keyCode == KeyEvent.VK_UP) {
            nuevoY -= 1;
        } else if (keyCode == KeyEvent.VK_DOWN) {
            nuevoY += 1;
        } else if (keyCode == KeyEvent.VK_LEFT) {
            nuevoX -= 1;
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            nuevoX += 1;
        } else {
            return;
        }

        level.moveCharacter(nuevoX, nuevoY);
        vista.actualizarInterfaz();
    }
}
