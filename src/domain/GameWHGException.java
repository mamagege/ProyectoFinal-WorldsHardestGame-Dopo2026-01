package domain;

public class GameWHGException extends Exception {
    // Constantes estáticas con los mensajes de error del negocio
    public static final String ERROR_PERSONAJE_INVALIDO = "Debe seleccionar un personaje válido (Rojo, Azul o Verde) antes de iniciar la partida.";
    public static final String ERROR_AVANCE_PREMATURO = "El jugador intentó acceder al siguiente nivel sin haber recolectado todas las monedas de la zona actual.";
    public static final String ERROR_NIVEL_NO_ENCONTRADO = "No se pudo encontrar el archivo de configuración para el nivel solicitado.";
    public static final String ERROR_SINTAXIS_NIVEL = "El archivo de nivel contiene una instrucción o formato de comando no válido.";
    public static final String ERROR_FALLO_LECTURA_NIVEL = "Error crítico al cargar los datos del nivel desde el archivo.";
    public static final String ERROR_CARGA_RECURSO = "Error crítico de E/S al cargar recursos multimedia del juego (imagen o fuente).";
    public static final String ERROR_CONEXION_TABLERO = "Consistencia del tablero violada: las dimensiones lógicas no pueden ser menores o iguales a cero.";
    public static final String ERROR_LIMITE_NIVELES = "Límite superior de nivel alcanzado. No hay más niveles disponibles en esta versión.";

    // Constructor estándar que recibe el mensaje
    public GameWHGException(String message) {
        super(message);
    }
}
