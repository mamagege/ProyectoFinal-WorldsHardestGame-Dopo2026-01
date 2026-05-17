package domain;

import presentation.GameWHGGUI;
import presentation.VentanaPrincipal;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.io.File;
import java.lang.reflect.Method;

public class TestRealPvpTransition {
    public static void main(String[] args) throws Exception {
        System.out.println("--- INICIANDO TEST CON CAPTURA DE PASO 1 Y PASO 2 ---");
        
        SwingUtilities.invokeAndWait(() -> {
            try {
                GameWHGGUI gui = new GameWHGGUI();
                VentanaPrincipal window = gui.getMainWindow();
                GameWHG game = gui.getGameFacade();
                
                window.setSize(1280, 720);
                window.setVisible(true);
                window.validate();
                
                window.mostrarPanel("JUEGO");
                
                Character c = game.getCurrentLevel().getCharacter();
                c.setPositionX(21.0);
                c.setPositionY(5.0); // Colisión
                
                System.out.println("--- Ejecutando tick de colisión ---");
                gui.tick();
                
                System.out.println("Modality después del primer tick: " + game.getCurrentModality());
                
                // Forzar que el thread de renderizado de swing dibuje PASO 1 (J1 seleccionando)
                BufferedImage image1 = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g1 = image1.createGraphics();
                window.getContentPane().paint(g1);
                g1.dispose();
                ImageIO.write(image1, "PNG", new File("test_pvp_step1.png"));
                System.out.println("Imagen PASO 1 capturada exitosamente en: test_pvp_step1.png");
                
                // Obtener panelSeleccion por reflection para simular click
                java.lang.reflect.Field fieldPanel = VentanaPrincipal.class.getDeclaredField("panelSeleccion");
                fieldPanel.setAccessible(true);
                Object panelSeleccion = fieldPanel.get(window);
                
                // Invocar seleccionarPersonaje(0) (PERSONAJE_ROJO) para J1
                Method methodSeleccionar = panelSeleccion.getClass().getDeclaredMethod("seleccionarPersonaje", int.class);
                methodSeleccionar.setAccessible(true);
                methodSeleccionar.invoke(panelSeleccion, 0); // J1 selecciona Rojo
                
                // Forzar que el thread de renderizado de swing dibuje PASO 2 (J2 seleccionando, J1 Confirmado)
                BufferedImage image2 = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = image2.createGraphics();
                window.getContentPane().paint(g2);
                g2.dispose();
                ImageIO.write(image2, "PNG", new File("test_pvp_step2.png"));
                System.out.println("Imagen PASO 2 capturada exitosamente en: test_pvp_step2.png");
                
                window.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        System.out.println("--- TEST FINALIZADO ---");
        System.exit(0);
    }
}
