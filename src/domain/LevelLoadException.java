package domain;

/**
 * Excepción específica para fallos controlados durante el parseo de niveles.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class LevelLoadException extends Exception {
    public LevelLoadException(String message) {
        super(message);
    }

    public LevelLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
