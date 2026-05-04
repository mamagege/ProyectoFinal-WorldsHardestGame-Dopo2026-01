package presentation;

import domain.*;
import domain.Character;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Controlador MVC.
 * Maneja eventos de teclado para el movimiento continuo.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class ControladorJuego extends KeyAdapter {
    private GameWHG gameOrchestrator;
    
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public ControladorJuego(PanelJuego vista, GameWHG gameOrchestrator) {
        this.gameOrchestrator = gameOrchestrator;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        setKeyState(e.getKeyCode(), true);
        updateVelocity();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        setKeyState(e.getKeyCode(), false);
        updateVelocity();
    }
    
    private void setKeyState(int keyCode, boolean pressed) {
        if (keyCode == KeyEvent.VK_UP) upPressed = pressed;
        if (keyCode == KeyEvent.VK_DOWN) downPressed = pressed;
        if (keyCode == KeyEvent.VK_LEFT) leftPressed = pressed;
        if (keyCode == KeyEvent.VK_RIGHT) rightPressed = pressed;
    }
    
    private void updateVelocity() {
        Level level = gameOrchestrator.getCurrentLevel();
        if (level == null || level.isCompleted()) return;
        
        Character pJugador = level.getCharacter();
        double speed = pJugador.getSpeed();
        
        double vx = 0;
        double vy = 0;
        
        if (upPressed) vy -= speed;
        if (downPressed) vy += speed;
        if (leftPressed) vx -= speed;
        if (rightPressed) vx += speed;
        
        pJugador.setVelocity(vx, vy);
    }
}
