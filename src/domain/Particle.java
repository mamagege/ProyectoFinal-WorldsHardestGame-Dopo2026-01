package domain;

/**
 * Representa una partícula visual para efectos de "Game Feel" (explosiones).
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class Particle {
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;
    private double width;
    private double height;
    private int lifeTime;

    public Particle(double positionX, double positionY, double velocityX, double velocityY, int lifeTime) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.width = 0.25;
        this.height = 0.25;
        this.lifeTime = lifeTime;
    }

    public void update() {
        positionX += velocityX;
        positionY += velocityY;
        lifeTime--;
    }

    public double getPositionX() { return positionX; }
    public double getPositionY() { return positionY; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public int getLifeTime() { return lifeTime; }
}
