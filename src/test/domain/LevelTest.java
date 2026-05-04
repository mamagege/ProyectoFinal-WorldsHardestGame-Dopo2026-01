package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

/**
 * Pruebas unitarias para verificar la lógica de los niveles.
 * Actualizadas para físicas continuas y AABB.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class LevelTest {

    private Level level;
    private Character character;
    private Obstacle obstacle;
    private Coin coin;
    private Wall wall;
    private Checkpoint checkpoint;
    private Goal goal;

    @BeforeEach
    public void setUp() {
        character = new RedCharacter(0.0, 0.0);
        checkpoint = new Checkpoint(0.0, 0.0, 1.0, 1.0);
        goal = new Goal(5.0, 5.0, 1.0, 1.0);
        wall = new Wall(1.0, 0.0, 1.0, 1.0);
        obstacle = new BasicObstacle(0.0, 2.0, 'R', true);
        coin = new Coin(2.0, 2.0, 1.0, 1.0);
        
        List<Checkpoint> checkpoints = Arrays.asList(checkpoint);
        List<Wall> walls = Arrays.asList(wall);
        List<Obstacle> obstacles = Arrays.asList(obstacle);
        List<Coin> coins = Arrays.asList(coin);
        
        level = new Level(character, obstacles, coins, walls, checkpoints, goal);
    }

    @Test
    public void checkAABBCollisionDetectsOverlap() {
        Element e1 = new Wall(0.0, 0.0, 1.0, 1.0);
        Element e2 = new Wall(0.5, 0.5, 1.0, 1.0);
        assertTrue(level.checkAABBCollision(e1, e2), "Los elementos superpuestos deben colisionar");

        Element e3 = new Wall(2.0, 2.0, 1.0, 1.0);
        assertFalse(level.checkAABBCollision(e1, e3), "Los elementos separados no deben colisionar");
    }

    @Test
    public void wallsBlockMovement() {
        // Forzamos el choque para verificar que no atraviesa
        character.setPositionX(0.9);
        character.setVelocity(0.2, 0); // Esto lo llevaría a 1.1, solapando la pared (1.0)
        level.tick();
        
        // Debería haberse deshecho el movimiento (regresa a 0.9)
        assertEquals(0.9, character.getPositionX(), 0.001, "La posición X no debe penetrar la pared");
    }

    @Test
    public void greenCharacterArmorLogic() {
        GreenCharacter greenChar = new GreenCharacter(0.0, 2.0);
        Level greenLevel = new Level(greenChar, Arrays.asList(obstacle), Arrays.asList(coin), Arrays.asList(wall), Arrays.asList(checkpoint), goal);
        
        // Forzamos colisión
        greenChar.setPositionX(obstacle.getPositionX());
        greenChar.setPositionY(obstacle.getPositionY());
        
        int initialDeaths = greenChar.getDeaths();
        greenLevel.tick(); // Evalúa colisiones
        
        assertEquals(initialDeaths, greenChar.getDeaths(), "No debe morir con armadura");
        assertFalse(greenChar.hasArmor(), "Debe perder la armadura");
        assertEquals(0.7 * Alive.BASE_SPEED, greenChar.getSpeed(), 0.001, "Su velocidad debe reducirse");
    }

    @Test
    public void collectingCoinsAndReachingGoalCompletesLevel() {
        character.setPositionX(2.0);
        character.setPositionY(2.0); // Encima de la moneda
        level.tick();
        assertTrue(coin.isCollected(), "La moneda debe estar recolectada");
        
        character.setPositionX(5.0);
        character.setPositionY(5.0); // Encima de la meta
        level.tick();
        assertTrue(level.isCompleted(), "El nivel debe estar completado");
    }
}
