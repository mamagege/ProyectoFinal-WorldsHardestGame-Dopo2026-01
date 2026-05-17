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
    
    private boolean p1Up = false;
    private boolean p1Down = false;
    private boolean p1Left = false;
    private boolean p1Right = false;

    private boolean p2Up = false;
    private boolean p2Down = false;
    private boolean p2Left = false;
    private boolean p2Right = false;

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
        boolean isSelection = false;
        if (gameOrchestrator != null && gameOrchestrator.getCurrentLevel() != null) {
            isSelection = gameOrchestrator.getCurrentLevel().isSelectionLevel();
        }

        if (isSelection) {
            // En nivel de selección (HUB), ambos WASD y Flechas controlan al único jugador (P1)
            if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) p1Up = pressed;
            if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) p1Down = pressed;
            if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) p1Left = pressed;
            if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) p1Right = pressed;
        } else {
            // Player 1 (WASD)
            if (keyCode == KeyEvent.VK_W) p1Up = pressed;
            if (keyCode == KeyEvent.VK_S) p1Down = pressed;
            if (keyCode == KeyEvent.VK_A) p1Left = pressed;
            if (keyCode == KeyEvent.VK_D) p1Right = pressed;
            
            // Player 2 (Flechas)
            if (keyCode == KeyEvent.VK_UP) p2Up = pressed;
            if (keyCode == KeyEvent.VK_DOWN) p2Down = pressed;
            if (keyCode == KeyEvent.VK_LEFT) p2Left = pressed;
            if (keyCode == KeyEvent.VK_RIGHT) p2Right = pressed;
        }
    }
    
    public void updateVelocity() {
        GameWHG facade = gameOrchestrator.getGameFacade();
        if (facade == null) return;
        
        // --- Player 1 ---
        double p1Speed = facade.getPlayerSpeed(0);
        double vx1 = 0, vy1 = 0;
        if (p1Up) vy1 -= p1Speed;
        if (p1Down) vy1 += p1Speed;
        if (p1Left) vx1 -= p1Speed;
        if (p1Right) vx1 += p1Speed;
        facade.movePlayer(0, vx1, vy1);
        
        // --- Player 2 ---
        double p2Speed = facade.getPlayerSpeed(1);
        double vx2 = 0, vy2 = 0;
        if (p2Up) vy2 -= p2Speed;
        if (p2Down) vy2 += p2Speed;
        if (p2Left) vx2 -= p2Speed;
        if (p2Right) vx2 += p2Speed;
        facade.movePlayer(1, vx2, vy2);
    }

    public void resetKeyStates() {
        p1Up = false; p1Down = false; p1Left = false; p1Right = false;
        p2Up = false; p2Down = false; p2Left = false; p2Right = false;
    }
}
