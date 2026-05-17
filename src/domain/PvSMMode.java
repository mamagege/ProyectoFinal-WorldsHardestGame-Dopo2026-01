package domain;

/**
 * Lógica del modo de juego Player vs Smart Machines.
 * Estructura base para futura implementación.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class PvSMMode extends SinglePlayerMode {
    // Hereda de SinglePlayerMode ya que las mecánicas base (victoria y muerte global) 
    // suelen ser iguales, pero se pueden añadir comportamientos de las máquinas inteligentes.

    @Override
    public void handleDeath(Level level, Character character) {
        if (character.hasArmor()) {
            character.removeArmor();
        } else {
            character.incrementDeaths();
        }
        SinglePlayerMode.resetCharacterToSpawn(level.getTablero(), character);
        resetAllCoins(level.getTablero());
    }
}
