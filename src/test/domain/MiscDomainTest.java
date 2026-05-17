package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MiscDomainTest {

    // --- Particle Tests ---
    @Test
    public void particleShouldUpdatePositionBasedOnVelocity() {
        Particle p = new Particle(0, 0, 1, 1, 5); // positionX=0, positionY=0, vx=1, vy=1, lifespan=5
        p.update();
        assertEquals(1, p.getPositionX(), 0.001, "Particle should move in X");
        assertEquals(1, p.getPositionY(), 0.001, "Particle should move in Y");
    }

    @Test
    public void particleShouldDecreaseLifespan() {
        Particle p = new Particle(0, 0, 0, 0, 5);
        p.update();
        assertTrue(p.getLifeTime() < 5, "Lifespan should decrease each update");
    }

    @Test
    public void particleShouldReturnFalseWhenDead() {
        Particle p = new Particle(0, 0, 0, 0, 1);
        p.update(); // Lifespan becomes 0
        assertEquals(0, p.getLifeTime(), "LifeTime should become 0");
    }

    // --- Modality Tests ---
    @Test
    public void modalityEnumShouldContainExpectedValues() {
        Modality[] modalities = Modality.values();
        assertEquals(3, modalities.length, "Should have exactly 3 modalities");
    }

    @Test
    public void modalityEnumValuesShouldMatch() {
        assertEquals("PLAYER", Modality.PLAYER.name(), "Should have PLAYER modality");
        assertEquals("PVP", Modality.PVP.name(), "Should have PVP modality");
        assertEquals("PVSM", Modality.PVSM.name(), "Should have PVSM modality");
    }

    @Test
    public void modalityShouldBeAssignable() {
        Modality mod = Modality.PLAYER;
        assertNotNull(mod, "Modality should be instantiable/assignable");
    }

    // --- Exception Tests ---
    @Test
    public void gameWHGExceptionShouldStoreMessage() {
        GameWHGException ex = new GameWHGException(GameWHGException.ERROR_AVANCE_PREMATURO);
        assertEquals(GameWHGException.ERROR_AVANCE_PREMATURO, ex.getMessage(), "Message should match the business error constant");
    }

    @Test
    public void gameWHGExceptionShouldInheritFromException() {
        GameWHGException ex = new GameWHGException("Test");
        assertTrue(ex instanceof Exception, "GameWHGException should be a checked Exception");
    }

    @Test
    public void gameWHGExceptionShouldHaveConstants() {
        assertNotNull(GameWHGException.ERROR_PERSONAJE_INVALIDO, "Constants should be initialized");
        assertNotNull(GameWHGException.ERROR_SINTAXIS_NIVEL, "Constants should be initialized");
    }

    @Test
    public void levelLoadExceptionShouldStoreMessage() {
        LevelLoadException ex = new LevelLoadException("Load failed");
        assertEquals("Load failed", ex.getMessage(), "LevelLoadException should store its message");
    }

    @Test
    public void levelLoadExceptionShouldStoreCause() {
        Throwable cause = new RuntimeException("IO Error");
        LevelLoadException ex = new LevelLoadException("Load failed", cause);
        assertEquals(cause, ex.getCause(), "LevelLoadException should wrap the root cause");
    }

    @Test
    public void levelLoadExceptionShouldInheritFromException() {
        LevelLoadException ex = new LevelLoadException("Test");
        assertTrue(ex instanceof Exception, "LevelLoadException should be a checked Exception");
    }

    @Test
    public void shouldTransitionToPvPMetaModeWhenCollidingWithPvPPortal() throws Exception {
        GameWHG game = new GameWHG();
        game.prepareLevels();
        assertEquals(Modality.PLAYER, game.getCurrentModality());

        // El jugador empieza en (24.0, 22.0)
        // El portal PVP está en (19.0, 3.5, 4.0, 6.5)
        // Colocamos al personaje del nivel actual justo dentro del portal PVP
        game.getCurrentLevel().getCharacter().setPositionX(20.0);
        game.getCurrentLevel().getCharacter().setPositionY(5.0);

        // Actualizamos tick
        game.updateGameTick();

        // Verificamos que se detectó la colisión y se cambió a PVP
        assertEquals(Modality.PVP, game.getCurrentModality(), "Debería haber cambiado a modalidad PVP");
        assertTrue(game.shouldGoSelectionMenu(), "Debería activar flag para ir al menú de selección");
        // Y que la posición se reseteó
        assertEquals(24.0, game.getCurrentLevel().getCharacter().getPositionX(), 0.001);
        assertEquals(22.0, game.getCurrentLevel().getCharacter().getPositionY(), 0.001);
    }
}
