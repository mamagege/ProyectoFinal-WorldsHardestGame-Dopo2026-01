package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CharacterTest {

    @Test
    public void shouldIncrementDeathsCorrectly() {
        // Arrange
        Character character = new RedCharacter(0, 0);
        int initialDeaths = character.getDeaths();

        // Act
        character.incrementDeaths();

        // Assert
        assertEquals(initialDeaths + 1, character.getDeaths(), "Deaths should increment by 1");
    }

    @Test
    public void shouldUpdatePositionBasedOnVelocity() {
        // Arrange
        Character character = new BlueCharacter(0, 0);
        character.setVelocity(0.5, 0.5);

        // Act
        character.updatePosition();

        // Assert
        assertEquals(0.5, character.getPositionX(), 0.001, "X position should update based on velocity");
        assertEquals(0.5, character.getPositionY(), 0.001, "Y position should update based on velocity");
    }

    @Test
    public void greenCharacterShouldHaveArmorAndReducedSpeed() {
        // Arrange
        Character greenChar = new GreenCharacter(0, 0);

        // Act
        boolean hasArmor = greenChar.hasArmor();
        double speed = greenChar.getSpeed();

        // Assert
        assertTrue(hasArmor, "GreenCharacter should start with armor");
        assertEquals(0.40 * Alive.BASE_SPEED, speed, 0.001, "GreenCharacter should have reduced speed");
    }

    @Test
    public void blueCharacterShouldHaveIncreasedSpeed() {
        // Arrange
        Character blueChar = new BlueCharacter(0, 0);

        // Act
        double speed = blueChar.getSpeed();

        // Assert
        assertEquals(1.15 * Alive.BASE_SPEED, speed, 0.001, "BlueCharacter should have increased speed");
    }

    @Test
    public void whiteCharacterShouldHaveNormalSpeed() {
        // Arrange
        Character whiteChar = new WhiteCharacter(0, 0);

        // Act
        double speed = whiteChar.getSpeed();

        // Assert
        assertEquals(1.5 * Alive.BASE_SPEED, speed, 0.001, "WhiteCharacter should have 1.5x speed");
    }
}
