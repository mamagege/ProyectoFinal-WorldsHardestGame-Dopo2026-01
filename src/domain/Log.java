package domain;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Utilidad centralizada para registrar errores y excepciones en un archivo legible.
 * Implementa un patrón Singleton implícito a través de métodos estáticos.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class Log {
    private static final Logger LOGGER = Logger.getLogger(Log.class.getName());
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = LOG_DIR + "/dopo_whg_errors.log";

    static {
        try {
            // Asegurar que la carpeta logs exista
            File dir = new File(LOG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Configurar el manejador de archivos (Append = true)
            FileHandler fileHandler = new FileHandler(LOG_FILE, true);
            
            // Formateador simple y legible (Fecha Hora Nivel Mensaje)
            // System property configures the SimpleFormatter format
            System.setProperty("java.util.logging.SimpleFormatter.format", 
              "[%1$tF %1$tT] [%4$-7s] %5$s %6$s%n");
              
            fileHandler.setFormatter(new SimpleFormatter());
            
            // Eliminar manejadores por consola si se desea silenciar, 
            // o simplemente añadir el nuestro.
            LOGGER.setUseParentHandlers(false); 
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.ALL);
            
        } catch (IOException e) {
            System.err.println("No se pudo inicializar el sistema de logs: " + e.getMessage());
        }
    }

    /**
     * Registra una excepción (SEVERE) junto con su traza (stack trace).
     * @param e La excepción a registrar.
     */
    public static void record(Exception e) {
        if (e != null) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }
    
    /**
     * Registra un mensaje de advertencia (WARNING).
     * @param message El mensaje.
     */
    public static void warning(String message) {
        if (message != null) {
            LOGGER.log(Level.WARNING, message);
        }
    }
    
    /**
     * Registra un mensaje de información (INFO).
     * @param message El mensaje.
     */
    public static void info(String message) {
        if (message != null) {
            LOGGER.log(Level.INFO, message);
        }
    }
}
