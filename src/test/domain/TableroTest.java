package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class TableroTest {

    @Test
    public void shouldInitializeListsWhenPassedNull() {
        // Arrange
        Character character = new RedCharacter(0, 0);
        
        // Act
        Tablero tablero = new Tablero(10, 10, character, null, null, null, null, null);

        // Assert
        assertNotNull(tablero.getObstacles(), "Obstacles list should not be null");
        assertNotNull(tablero.getCoins(), "Coins list should not be null");
        assertNotNull(tablero.getWalls(), "Walls list should not be null");
        assertNotNull(tablero.getCheckpoints(), "Checkpoints list should not be null");
    }

    @Test
    public void shouldSetBoardDimensionsCorrectly() {
        // Arrange
        Character character = new RedCharacter(0, 0);
        
        // Act
        Tablero tablero = new Tablero(20.5, 15.0, character, null, null, null, null, null);

        // Assert
        assertEquals(20.5, tablero.getBoardWidth(), 0.001);
        assertEquals(15.0, tablero.getBoardHeight(), 0.001);
    }

    @Test
    public void shouldClampPositionsProperly() {
        // Arrange
        Character character = new RedCharacter(0, 0);
        Tablero tablero = new Tablero(10, 10, character, null, null, null, null, null);

        // Act
        tablero.setClampingBounds(2.0, 8.0, 3.0, 7.0);

        // Assert
        assertEquals(2.0, tablero.getMinX(), 0.001);
        assertEquals(8.0, tablero.getMaxX(), 0.001);
        assertEquals(3.0, tablero.getMinY(), 0.001);
        assertEquals(7.0, tablero.getMaxY(), 0.001);
    }
}
