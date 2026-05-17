package presentation;

import domain.GameWHG;

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
    private GameWHGGUI gameOrchestrator;
    
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public ControladorJuego(PanelJuego vista, GameWHGGUI gameOrchestrator) {
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
        if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) upPressed = pressed;
        if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) downPressed = pressed;
        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) leftPressed = pressed;
        if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) rightPressed = pressed;
    }
    
    public void updateVelocity() {
        GameWHG facade = gameOrchestrator.getGameFacade();
        if (facade == null) return;
        
        double speed = facade.getPlayerSpeed();
        
        double vx = 0;
        double vy = 0;
        
        if (upPressed) vy -= speed;
        if (downPressed) vy += speed;
        if (leftPressed) vx -= speed;
        if (rightPressed) vx += speed;
        
        facade.movePlayer(vx, vy);
    }

    public void resetKeyStates() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
    }
}
