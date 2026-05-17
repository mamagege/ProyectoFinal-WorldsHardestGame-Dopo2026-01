package domain;

import presentation.GameWHGGUI;
import javax.swing.SwingUtilities;

public class PortalSimulation {
    public static void main(String[] args) throws Exception {
        System.out.println("--- INICIANDO SIMULACIÓN DE PORTAL ---");
        GameWHGGUI gui = new GameWHGGUI();
        GameWHG game = gui.getGameFacade();
        
        System.out.println("Nivel actual: " + game.getCurrentLevelIndex());
        System.out.println("Is Selection Level: " + game.getCurrentLevel().isSelectionLevel());
        System.out.println("Modality inicial: " + game.getCurrentModality());
        
        // Simular movimiento del jugador hacia la izquierda y arriba
        Character c = game.getCurrentLevel().getCharacter();
        System.out.println("Posición inicial jugador: (" + c.getPositionX() + ", " + c.getPositionY() + ")");
        
        // Mover a x = 21.0
        c.setPositionX(21.0);
        c.setPositionY(22.0);
        System.out.println("Posición después de paso 1: (" + c.getPositionX() + ", " + c.getPositionY() + ")");
        
        // Mover arriba hasta y = 5.0 (dentro del portal PVP)
        c.setPositionY(5.0);
        System.out.println("Posición antes de colisión: (" + c.getPositionX() + ", " + c.getPositionY() + ")");
        
        // Ejecutar tick
        gui.tick();
        
        System.out.println("¿Debe ir a selección?: " + game.shouldGoSelectionMenu());
        System.out.println("Modality final: " + game.getCurrentModality());
        System.out.println("Posición final jugador: (" + c.getPositionX() + ", " + c.getPositionY() + ")");
        System.out.println("--- SIMULACIÓN FINALIZADA ---");
        System.exit(0);
    }
}
