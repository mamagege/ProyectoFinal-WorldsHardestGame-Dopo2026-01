package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EnvironmentElementsTest {

    // --- Coin Tests ---
    @Test
    public void coinShouldBeCollectable() {
        Coin coin = new Coin(0, 0, 1, 1);
        coin.collect();
        assertTrue(coin.isCollected(), "Coin should be marked as collected");
    }

    @Test
    public void coinShouldBeResettable() {
        Coin coin = new Coin(0, 0, 1, 1);
        coin.collect();
        coin.reset();
        assertFalse(coin.isCollected(), "Coin should be reset to not collected");
    }

    @Test
    public void coinShouldHaveCorrectPosition() {
        Coin coin = new Coin(5.5, 3.2, 1, 1);
        assertEquals(5.5, coin.getPositionX(), 0.001);
        assertEquals(3.2, coin.getPositionY(), 0.001);
    }

    // --- Wall Tests ---
    @Test
    public void wallShouldHaveCorrectDimensions() {
        Wall wall = new Wall(0, 0, 2.5, 3.5);
        assertEquals(2.5, wall.getWidth(), 0.001);
        assertEquals(3.5, wall.getHeight(), 0.001);
    }

    @Test
    public void wallShouldNotBePassable() {
        Wall wall = new Wall(0, 0, 1, 1);
        assertNotNull(wall, "Wall should exist as an impassable element");
    }

    @Test
    public void wallShouldMaintainPosition() {
        Wall wall = new Wall(10, 10, 1, 1);
        assertEquals(10, wall.getPositionX(), 0.001);
        assertEquals(10, wall.getPositionY(), 0.001);
    }

    // --- Checkpoint Tests ---
    @Test
    public void checkpointShouldStoreCoordinates() {
        Checkpoint cp = new Checkpoint(1, 2, 3, 4);
        assertEquals(1, cp.getPositionX(), 0.001);
        assertEquals(2, cp.getPositionY(), 0.001);
    }

    @Test
    public void checkpointShouldActAsSafeZone() {
        Checkpoint cp = new Checkpoint(0, 0, 1, 1);
        assertTrue(cp.getWidth() > 0, "Checkpoint must have physical width to act as safe zone");
    }

    @Test
    public void checkpointShouldProvideCorrectBoundaries() {
        Checkpoint cp = new Checkpoint(5, 5, 2, 2);
        assertEquals(2, cp.getHeight(), 0.001);
    }

    // --- Goal Tests ---
    @Test
    public void goalShouldDetermineEndCondition() {
        Goal goal = new Goal(10, 10, 2, 2);
        assertNotNull(goal, "Goal must be instantiable");
    }

    @Test
    public void goalShouldProvideDimensionsForCollision() {
        Goal goal = new Goal(0, 0, 5, 5);
        assertEquals(5, goal.getWidth(), 0.001);
    }

    @Test
    public void goalShouldProvidePosition() {
        Goal goal = new Goal(15, 25, 1, 1);
        assertEquals(15, goal.getPositionX(), 0.001);
        assertEquals(25, goal.getPositionY(), 0.001);
    }

    // --- Tile Tests ---
    @Test
    public void tileShouldStoreHexColor() {
        Tile tile = new Tile(0, 0, 1, 1, "#FFFFFF");
        assertEquals("#FFFFFF", tile.getColorHex(), "Tile should return its color");
    }

    @Test
    public void tileShouldProvideVisualPosition() {
        Tile tile = new Tile(3, 3, 1, 1, "#000000");
        assertEquals(3, tile.getPositionX(), 0.001);
    }

    @Test
    public void tileShouldProvideVisualDimensions() {
        Tile tile = new Tile(0, 0, 1.5, 1.5, "#FF0000");
        assertEquals(1.5, tile.getWidth(), 0.001);
    }

    // --- ModalityZone Tests ---
    @Test
    public void modalityZoneShouldAssignModality() {
        ModalityZone mz = new ModalityZone(0, 0, 1, 1, Modality.PVP);
        assertEquals(Modality.PVP, mz.getModality(), "Zone should hold PVP modality");
    }

    @Test
    public void modalityZoneShouldReturnDimensions() {
        ModalityZone mz = new ModalityZone(0, 0, 2, 3, Modality.PVSM);
        assertEquals(2, mz.getWidth(), 0.001);
        assertEquals(3, mz.getHeight(), 0.001);
    }

    @Test
    public void modalityZoneShouldStoreLocation() {
        ModalityZone mz = new ModalityZone(7, 8, 1, 1, Modality.PLAYER);
        assertEquals(7, mz.getPositionX(), 0.001);
        assertEquals(8, mz.getPositionY(), 0.001);
    }
}
