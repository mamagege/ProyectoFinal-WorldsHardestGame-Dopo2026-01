package domain;

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

    public Character(double positionX, double positionY, double width, double height, double speed) {
        super(positionX, positionY, width, height, speed, 'N'); // 'N' significa Ninguna dirección
        this.deaths = 0;
        this.hasArmor = false;
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
}
