package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CollisionDetectorTest {

    @Test
    public void shouldDetectOverlapBetweenTwoRectangles() {
        // Arrange
        Element rect1 = new Wall(0.0, 0.0, 1.0, 1.0);
        Element rect2 = new Wall(0.5, 0.5, 1.0, 1.0);

        // Act
        boolean result = CollisionDetector.checkCollision(rect1, rect2);

        // Assert
        assertTrue(result, "Should detect collision when rectangles overlap");
    }

    @Test
    public void shouldNotDetectOverlapForSeparateRectangles() {
        // Arrange
        Element rect1 = new Wall(0.0, 0.0, 1.0, 1.0);
        Element rect2 = new Wall(2.0, 2.0, 1.0, 1.0);

        // Act
        boolean result = CollisionDetector.checkCollision(rect1, rect2);

        // Assert
        assertFalse(result, "Should not detect collision when rectangles are separated");
    }

    @Test
    public void shouldDetectOverlapBetweenCircleAndRectangle() {
        // Arrange
        Element rect = new RedCharacter(0.0, 0.0); // 1x1 rect at 0,0
        // Circle center at (0.5+0.375, 0.5+0.375) = (0.875, 0.875), radius = 0.375. Closest point on rect is (0.75,0.75).
        // Distance is sqrt(0.125^2 + 0.125^2) = 0.176 < 0.375
        Element circle = new BasicObstacle(0.5, 0.5, 'R', true);

        // Act
        boolean result = CollisionDetector.checkCollision(circle, rect);

        // Assert
        assertTrue(result, "Should detect collision when circle penetrates rectangle corner");
    }

    @Test
    public void shouldNotDetectOverlapBetweenCircleAndRectangle() {
        // Arrange
        Element rect = new RedCharacter(0.0, 0.0); // 1x1 rect at 0,0
        // Circle center at (1.5, 1.5), radius = 0.5. Closest point on rect is (1,1).
        // Distance is sqrt(0.5^2 + 0.5^2) = 0.707 > 0.5
        Element circle = new BasicObstacle(1.0, 1.0, 'R', true);

        // Act
        boolean result = CollisionDetector.checkCollision(circle, rect);

        // Assert
        assertFalse(result, "Should not detect collision when circle is outside rectangle");
    }
}
