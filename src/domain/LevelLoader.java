package domain;

/**
 * Interfaz pura para la carga de niveles siguiendo DIP (SOLID).
 * Permite desacoplar el motor del origen físico de los mapas.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public interface LevelLoader {
    /**
     * Carga un nivel completo dado su identificador numérico.
     * 
     * @param levelNumber Número del nivel a cargar.
     * @return Objeto Level completamente poblado.
     * @throws LevelLoadException Si hay problemas de lectura o de sintaxis en la fuente.
     */
    Level loadLevel(int levelNumber) throws LevelLoadException;
}
