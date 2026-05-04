package domain;

/**
 * Representa al personaje controlado por el jugador.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class Character extends Alive {
    private int deaths;

    public Character(int positionX, int positionY, int width, int height, int speed) {
        super(positionX, positionY, width, height, speed, 'N'); // 'N' significa Ninguna dirección
        this.deaths = 0;
    }

    public int getDeaths() { return deaths; }
    public void incrementDeaths() { this.deaths++; }

    @Override
    public void move(char direction) {
        this.direction = direction;
        updatePosition();
    }

    @Override
    public void updatePosition() {
        if (direction == 'U') positionY -= speed;
        else if (direction == 'D') positionY += speed;
        else if (direction == 'L') positionX -= speed;
        else if (direction == 'R') positionX += speed;
    }
}
