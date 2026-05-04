

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

/**
 * Pruebas unitarias para verificar la lógica de los niveles.
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
        character = new Character(0, 0, 1, 1, 1);
        checkpoint = new Checkpoint(0, 0, 1, 1);
        goal = new Goal(5, 5, 1, 1);
        wall = new Wall(1, 0, 1, 1);
        obstacle = new Obstacle(0, 2, 1, 1, 1, 'R', true);
        coin = new Coin(2, 2, 1, 1);
        
        List<Checkpoint> checkpoints = Arrays.asList(checkpoint);
        List<Wall> walls = Arrays.asList(wall);
        List<Obstacle> obstacles = Arrays.asList(obstacle);
        List<Coin> coins = Arrays.asList(coin);
        
        level = new Level(character, obstacles, coins, walls, checkpoints, goal);
    }

    @Test
    public void characterMovesCorrectly() {
        level.moveCharacter(0, 1);
        assertEquals(0, character.getPositionX(), "La posición X debe ser 0");
        assertEquals(1, character.getPositionY(), "La posición Y debe ser 1");
    }

    @Test
    public void wallsBlockMovement() {
        level.moveCharacter(1, 0); // La pared está en 1,0
        assertEquals(0, character.getPositionX(), "La posición X no debe cambiar");
        assertEquals(0, character.getPositionY(), "La posición Y no debe cambiar");
    }

    @Test
    public void touchingObstacleCausesDeathAndReset() {
        int initialDeaths = character.getDeaths();
        level.moveCharacter(0, 2); // El obstáculo está en 0,2
        
        assertEquals(initialDeaths + 1, character.getDeaths(), "Las muertes deben aumentar en 1");
        assertEquals(checkpoint.getPositionX(), character.getPositionX(), "Debe reiniciar en la X del punto de control");
        assertEquals(checkpoint.getPositionY(), character.getPositionY(), "Debe reiniciar en la Y del punto de control");
    }

    @Test
    public void collectingCoinsAndReachingGoalCompletesLevel() {
        level.moveCharacter(2, 2); // Recolecta moneda
        assertTrue(coin.isCollected(), "La moneda debe estar recolectada");
        
        level.moveCharacter(5, 5); // Llega a la meta
        assertTrue(level.isCompleted(), "El nivel debe estar completado");
    }
}
