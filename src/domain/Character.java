package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa al personaje controlado por el jugador.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public abstract class Character extends Alive {
    private int deaths;
    protected boolean hasArmor;
    
    // Para movimiento fluido
    protected double velocityX = 0;
    protected double velocityY = 0;
    
    // Game Feel: Explosión
    private boolean isExploding;
    private List<Particle> fragments;

    public Character(double positionX, double positionY, double width, double height, double speed) {
        super(positionX, positionY, width, height, speed, 'N'); // 'N' significa Ninguna dirección
        this.deaths = 0;
        this.hasArmor = false;
        this.isExploding = false;
        this.fragments = new ArrayList<>();
    }

    public int getDeaths() { return deaths; }
    public void incrementDeaths() { this.deaths++; }
    public boolean hasArmor() { return hasArmor; }
    public void removeArmor() { this.hasArmor = false; }

    public void setVelocity(double vx, double vy) {
        this.velocityX = vx;
        this.velocityY = vy;
    }

    @Override
    public void move(char direction) {
        // Obsoleto en físicas continuas.
        this.direction = direction;
    }

    @Override
    public void updatePosition() {
        positionX += velocityX;
        positionY += velocityY;
    }
    
    public boolean isExploding() { return isExploding; }
    public List<Particle> getFragments() { return fragments; }

    public void triggerExplosion() {
        this.isExploding = true;
        this.fragments.clear();
        int numParticles = 8;
        double angleStep = (Math.PI * 2) / numParticles;
        for (int i = 0; i < numParticles; i++) {
            double angle = i * angleStep;
            double speedMod = Alive.BASE_SPEED * 1.0; // Velocidad reducida para menor radio
            double vx = Math.cos(angle) * speedMod;
            double vy = Math.sin(angle) * speedMod;
            // Tiempo de vida reducido (15 frames = 1/4 de segundo) para animación más rápida
            fragments.add(new Particle(getCenterX() - 0.125, getCenterY() - 0.125, vx, vy, 15));
        }
    }

    public void updateExplosion() {
        if (!isExploding) return;
        
        boolean anyAlive = false;
        for (Particle p : fragments) {
            p.update();
            if (p.getLifeTime() > 0) {
                anyAlive = true;
            }
        }
        
        if (!anyAlive) {
            isExploding = false;
        }
    }
}
