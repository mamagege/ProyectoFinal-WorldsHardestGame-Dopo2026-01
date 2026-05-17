package domain;

import presentation.GameWHGGUI;
import presentation.VentanaPrincipal;
import javax.swing.JFrame;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.io.File;

public class WalkToPvpAndCapture {
    public static void main(String[] args) throws Exception {
        System.out.println("--- INICIANDO CAPTURA DE RENDER DIRECTO ---");
        GameWHGGUI gui = new GameWHGGUI();
        VentanaPrincipal window = gui.getMainWindow();
        GameWHG game = gui.getGameFacade();
        
        // 1. Forzar ir directamente a JUEGO (hub de selección)
        window.mostrarPanel("JUEGO");
        
        // Renderizar el Hub de selección inicial
        captureComponent(window, "hub_seleccion_inicial.png");
        
        // 2. Simular que el jugador camina hacia el portal PVP
        Character c = game.getCurrentLevel().getCharacter();
        System.out.println("Spawn jugador: (" + c.getPositionX() + ", " + c.getPositionY() + ")");
        
        c.setPositionX(21.0);
        c.setPositionY(22.0);
        gui.tick();
        
        c.setPositionY(5.0); // Dentro del portal PVP
        gui.tick();
        
        System.out.println("¿Debe ir a selección?: " + game.shouldGoSelectionMenu());
        System.out.println("Modality final: " + game.getCurrentModality());
        
        // 3. Capturar la pantalla después de la colisión
        captureComponent(window, "pantalla_despues_colision.png");
        
        System.out.println("--- CAPTURA DIRECTA FINALIZADA ---");
        System.exit(0);
    }
    
    private static void captureComponent(JFrame window, String filename) throws Exception {
        int w = 1280;
        int h = 720;
        window.setSize(w, h);
        window.doLayout();
        
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        window.paint(g2d);
        g2d.dispose();
        
        File artifactDir = new File("C:\\Users\\USUARIO\\.gemini\\antigravity\\brain\\bc4b80b0-a1f4-492b-a664-f5fd18c5b2e1");
        File destFile = new File(artifactDir, filename);
        ImageIO.write(img, "png", destFile);
        System.out.println("Captura guardada en: " + destFile.getAbsolutePath());
    }
}
