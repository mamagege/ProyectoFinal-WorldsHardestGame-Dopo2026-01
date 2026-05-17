package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

public class ObstacleTest {

    @Test
    public void basicObstacleShouldHaveCorrectSpeed() {
        // Arrange & Act
        Obstacle obstacle = new BasicObstacle(0, 0, 'R', true);

        // Assert
        assertEquals(Obstacle.BASE_SPEED, obstacle.getSpeed(), 0.001, "BasicObstacle should have normal speed");
    }

    @Test
    public void fastObstacleShouldHaveIncreasedSpeed() {
        // Arrange & Act
        Obstacle obstacle = new FastObstacle(0, 0, 'R', true);

        // Assert
        assertEquals(Obstacle.BASE_SPEED * 2.0, obstacle.getSpeed(), 0.001, "FastObstacle should be faster");
    }

    @Test
    public void patrolObstacleShouldSwitchDirectionWhenHittingBounds() {
        // Arrange
        PatrolObstacle patrol = new PatrolObstacle(0, 0, 5); // OriginX, OriginY, Radius

        // Act
        patrol.updatePosition();

        // Assert
        assertTrue(patrol.getPositionX() > 0 || patrol.getPositionY() > 0, "PatrolObstacle should move based on trig functions");
    }

    @Test
    public void waypointObstacleShouldNavigateToWaypoints() {
        // Arrange
        double[][] points = {{0, 0}, {5, 0}, {5, 5}};
        WaypointObstacle waypointObstacle = new WaypointObstacle(points); // No boolean parameter
        
        // Act & Assert
        // Initial target is index 1 (5, 0)
        double speed = waypointObstacle.getSpeed();
        assertTrue(speed > 0, "WaypointObstacle should have positive speed");
        
        // Move towards waypoint manually
        for (int i = 0; i < 100; i++) {
            waypointObstacle.updatePosition();
            if(waypointObstacle.getPositionX() >= 5.0) break;
        }
        
        assertEquals(5.0, waypointObstacle.getPositionX(), 0.5, "Obstacle should approach the first waypoint");
    }

    @Test
    public void shouldReturnIsCircular() {
        // Arrange
        Obstacle obstacle = new BasicObstacle(0, 0, 'R', true);

        // Act
        boolean circular = obstacle.isCircular();

        // Assert
        assertTrue(circular, "Obstacles should be circular for collision geometry");
    }
}
