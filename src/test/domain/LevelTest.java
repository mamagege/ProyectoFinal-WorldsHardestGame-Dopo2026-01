package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

/**
 * Pruebas unitarias para verificar la lógica de los niveles.
 * Actualizadas para físicas continuas y AABB bajo convención AAA.
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
        Tablero tablero = new Tablero(10.0, 10.0, character, obstacles, Arrays.asList(coin), walls, checkpoints, goal);
        level = new Level(tablero);
    }

    @Test
    public void shouldInitializeLevelProperly() {
        // Arrange & Act (Done in setUp)

        // Assert
        assertNotNull(level.getCharacter(), "Character should not be null");
        assertFalse(level.isCompleted(), "Level should not be completed initially");
        assertEquals(1, level.getCoins().size(), "Level should have 1 coin");
    }

    @Test
    public void shouldCompleteLevelWhenGoalReachedAndCoinsCollected() {
        // Arrange
        character.setPositionX(2.0);
        character.setPositionY(2.0); // Encima de la moneda
        level.tick(); // Recolecta moneda

        // Act
        character.setPositionX(5.0);
        character.setPositionY(5.0); // Encima de la meta
        level.tick();

        // Assert
        assertTrue(coin.isCollected(), "Coin should be collected");
        assertTrue(level.isCompleted(), "Level should be completed");
    }

    @Test
    public void shouldNotCompleteLevelWhenCoinsAreMissing() {
        // Arrange
        character.setPositionX(5.0);
        character.setPositionY(5.0); // Directo a la meta, saltando moneda

        // Act
        level.tick();

        // Assert
        assertFalse(coin.isCollected(), "Coin should not be collected");
        assertFalse(level.isCompleted(), "Level should not be completed without all coins");
    }

    @Test
    public void shouldResetCoinsWhenPlayerDies() {
        // Arrange
        character.setPositionX(2.0);
        character.setPositionY(2.0);
        level.tick(); // Recoge moneda
        
        // Act
        character.setPositionX(obstacle.getPositionX());
        character.setPositionY(obstacle.getPositionY());
        level.tick(); // Muere
        
        while (character.isExploding()) {
            level.tick(); // Avanzar explosión
        }
        level.tick(); // Finalizar ciclo muerte

        // Assert
        assertFalse(coin.isCollected(), "Coins should be reset after death");
        assertFalse(character.isExploding(), "Character should have respawned");
    }

    @Test
    public void shouldPreventMovementThroughWalls() {
        // Arrange
        character.setPositionX(0.9);
        character.setVelocity(0.2, 0); // Intenta cruzar pared en X=1.0

        // Act
        level.tick();

        // Assert
        assertEquals(0.9, character.getPositionX(), 0.001, "Character X position should be reverted to prevent crossing wall");
    }
}
