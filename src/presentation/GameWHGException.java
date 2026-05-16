package presentation;

public class GameWHGException extends Exception {
    // Constantes estáticas con los mensajes de error del negocio
    public static final String ERROR_PERSONAJE_INVALIDO = "Debe seleccionar un personaje válido (Rojo, Azul o Verde) antes de iniciar la partida.";
    public static final String ERROR_AVANCE_PREMATURO = "El jugador intentó acceder al siguiente nivel sin haber recolectado todas las monedas de la zona actual.";

    // Constructor estándar que recibe el mensaje
    public GameWHGException(String message) {
        super(message);
    }
}
