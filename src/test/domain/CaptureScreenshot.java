package domain;

import presentation.GameWHGGUI;
import javax.swing.JFrame;
import java.awt.Robot;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class CaptureScreenshot {
    public static void main(String[] args) throws Exception {
        System.out.println("--- STARTING CAPTURE SCREENSHOT ---");
        
        // Start GUI in a separate thread so it doesn't block
        GameWHGGUI[] guiHolder = new GameWHGGUI[1];
        Thread t = new Thread(() -> {
            try {
                guiHolder[0] = new GameWHGGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        
        // Wait 3 seconds for splash and window to load
        Thread.sleep(3000);
        
        // Find the window and take screenshot
        Robot robot = new Robot();
        Rectangle screenRect = new Rectangle(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
        
        // Save screenshot to artifacts directory
        File artifactDir = new File("C:\\Users\\USUARIO\\.gemini\\antigravity\\brain\\bc4b80b0-a1f4-492b-a664-f5fd18c5b2e1");
        if (!artifactDir.exists()) {
            artifactDir.mkdirs();
        }
        File destFile = new File(artifactDir, "initial_game_screen.png");
        ImageIO.write(screenFullImage, "png", destFile);
        System.out.println("Screenshot saved to: " + destFile.getAbsolutePath());
        
        // Let's close the window and exit
        System.exit(0);
    }
}
