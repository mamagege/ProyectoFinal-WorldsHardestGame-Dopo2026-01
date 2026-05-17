package data;

import domain.Level;
import domain.LevelLoadException;
import domain.LevelLoader;
import domain.Tablero;
import domain.Character;
import domain.RedCharacter;
import domain.Wall;
import domain.Checkpoint;
import domain.Goal;
import domain.Coin;
import domain.Tile;
import domain.Obstacle;
import domain.BasicObstacle;
import domain.FastObstacle;
import domain.PatrolObstacle;
import domain.WaypointObstacle;
import domain.GameWHGException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Motor concreto de carga de niveles que lee sintaxis de texto plana.
 * Cumple con OCP: Añadir nuevos comandos no requiere modificar el dominio.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class TxtLevelLoader implements LevelLoader {
    private static final String BASE_DIR = "src/resources/levels/";

    @Override
    public Level loadLevel(int levelNumber) throws LevelLoadException {
        File file = new File(BASE_DIR + "level" + levelNumber + ".txt");
        if (!file.exists()) {
            try {
                throw new GameWHGException(GameWHGException.ERROR_NIVEL_NO_ENCONTRADO);
            } catch (GameWHGException ex) {
                domain.Log.record(ex);
                throw new LevelLoadException(ex.getMessage() + " (Archivo: " + file.getName() + ")", ex);
            }
        }

        double boardW = 20, boardH = 15; // Fallbacks
        double startX = 1, startY = 1;
        
        List<Wall> walls = new ArrayList<>();
        List<Checkpoint> checkpoints = new ArrayList<>();
        Goal goal = null;
        List<Coin> coins = new ArrayList<>();
        List<Obstacle> obstacles = new ArrayList<>();
        List<Tile> tiles = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                // Ignorar vacíos y comentarios
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] tokens = line.split("\\s+");
                String command = tokens[0].toUpperCase();

                try {
                    switch (command) {
                        case "SIZE":
                            boardW = Double.parseDouble(tokens[1]);
                            boardH = Double.parseDouble(tokens[2]);
                            break;

                        case "START":
                            startX = Double.parseDouble(tokens[1]);
                            startY = Double.parseDouble(tokens[2]);
                            break;

                        case "WALL":
                            walls.add(new Wall(
                                Double.parseDouble(tokens[1]), 
                                Double.parseDouble(tokens[2]),
                                Double.parseDouble(tokens[3]), 
                                Double.parseDouble(tokens[4])
                            ));
                            break;

                        case "CHECKPOINT":
                            checkpoints.add(new Checkpoint(
                                Double.parseDouble(tokens[1]), 
                                Double.parseDouble(tokens[2]),
                                Double.parseDouble(tokens[3]), 
                                Double.parseDouble(tokens[4])
                            ));
                            break;

                        case "GOAL":
                            goal = new Goal(
                                Double.parseDouble(tokens[1]), 
                                Double.parseDouble(tokens[2]),
                                Double.parseDouble(tokens[3]), 
                                Double.parseDouble(tokens[4])
                            );
                            break;

                        case "COIN":
                            // Las monedas por defecto son 0.5x0.5
                            coins.add(new Coin(
                                Double.parseDouble(tokens[1]), 
                                Double.parseDouble(tokens[2]), 
                                0.5, 0.5
                            ));
                            break;

                        case "TILES":
                            // Rellena un área rectangular con baldosas 1x1 siguiendo el patrón de damero
                            fillCheckeredTiles(
                                levelNumber,
                                (int)Double.parseDouble(tokens[1]), 
                                (int)Double.parseDouble(tokens[2]),
                                (int)Double.parseDouble(tokens[3]), 
                                (int)Double.parseDouble(tokens[4]), 
                                tiles
                            );
                            break;

                        case "OBSTACLE":
                            parseObstacle(tokens, obstacles);
                            break;

                        default:
                            System.err.println("TxtLevelLoader: Comando desconocido ignorado en línea " + lineNumber + ": " + command);
                    }
                } catch (Exception syntaxError) {
                    try {
                        throw new GameWHGException(GameWHGException.ERROR_SINTAXIS_NIVEL);
                    } catch (GameWHGException ex) {
                        domain.Log.record(ex);
                        throw new LevelLoadException(ex.getMessage() + " (Línea: " + lineNumber + ", Detalle: " + syntaxError.getMessage() + ")", syntaxError);
                    }
                }
            }

        } catch (Exception e) {
            if (e instanceof LevelLoadException) throw (LevelLoadException) e;
            try {
                throw new GameWHGException(GameWHGException.ERROR_FALLO_LECTURA_NIVEL);
            } catch (GameWHGException ex) {
                domain.Log.record(ex);
                throw new LevelLoadException(ex.getMessage() + " (Nivel: " + levelNumber + ")", e);
            }
        }

        // Ensamblado Final del Nivel según el Dominio
        if (boardW <= 0 || boardH <= 0) {
            try {
                throw new GameWHGException(GameWHGException.ERROR_CONEXION_TABLERO);
            } catch (GameWHGException ex) {
                domain.Log.record(ex);
                throw new LevelLoadException(ex.getMessage(), ex);
            }
        }
        Character player = new RedCharacter(startX, startY);
        Tablero tablero = new Tablero(boardW, boardH, player, obstacles, coins, walls, checkpoints, goal);
        Level constructedLevel = new Level(tablero);
        constructedLevel.setTiles(tiles);

        // Calcular los límites reales del área jugable desde las baldosas.
        // Esto hace que el clamp EXCLUSIVO del personaje coincida exactamente con el delineado rojo
        // en lugar de usar boardWidth/boardHeight (que incluye el ancho de las paredes de borde).
        if (!tiles.isEmpty()) {
            double minTX = Double.MAX_VALUE, minTY = Double.MAX_VALUE;
            double maxTX = 0, maxTY = 0;
            for (Tile t : tiles) {
                if (t.getPositionX()                  < minTX) minTX = t.getPositionX();
                if (t.getPositionY()                  < minTY) minTY = t.getPositionY();
                if (t.getPositionX() + t.getWidth()   > maxTX) maxTX = t.getPositionX() + t.getWidth();
                if (t.getPositionY() + t.getHeight()  > maxTY) maxTY = t.getPositionY() + t.getHeight();
            }
            tablero.setTileBounds(minTX, maxTX, minTY, maxTY);
        }

        return constructedLevel;
    }

    private void fillCheckeredTiles(int levelNumber, int startX, int startY, int width, int height, List<Tile> tilesList) {
        String colorEven = "#D3D3D3";
        String colorOdd  = "#F08080";

        // Paleta premium personalizada para combinar con fondos oscuros de Niveles 1 y 2
        if (levelNumber == 1 || levelNumber == 2) {
            colorEven = "#1B1421"; // Negro/Berenjena muy oscuro
            colorOdd  = "#3A2B4F"; // Morado místico profundo
        }

        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                // Lógica de damero centralizada
                String color = ((x + y) % 2 == 0) ? colorEven : colorOdd;
                tilesList.add(new Tile(x, y, 1.0, 1.0, color));
            }
        }
    }

    private void parseObstacle(String[] tokens, List<Obstacle> obstacles) {
        String type = tokens[1].toUpperCase();
        double x = Double.parseDouble(tokens[2]);
        double y = Double.parseDouble(tokens[3]);
        
        Obstacle obs = null;

        switch (type) {
            case "BASIC":
                // OBSTACLE BASIC x y direction isHorizontal [speedOverride]
                obs = new BasicObstacle(x, y, tokens[4].charAt(0), Boolean.parseBoolean(tokens[5]));
                if (tokens.length > 6) obs.setSpeed(Double.parseDouble(tokens[6]));
                break;

            case "FAST":
                obs = new FastObstacle(x, y, tokens[4].charAt(0), Boolean.parseBoolean(tokens[5]));
                if (tokens.length > 6) obs.setSpeed(Double.parseDouble(tokens[6]));
                break;

            case "PATROL":
                // OBSTACLE PATROL x y range
                obs = new PatrolObstacle(x, y, Double.parseDouble(tokens[4]));
                break;

            case "WAYPOINT":
                // OBSTACLE WAYPOINT x1 y1 x2 y2 x3 y3 ...
                // Parsea pares de coordenadas como puntos de paso del recorrido.
                int numPoints = (tokens.length - 2) / 2;
                double[][] wps = new double[numPoints][2];
                for (int i = 0; i < numPoints; i++) {
                    wps[i][0] = Double.parseDouble(tokens[2 + i * 2]);
                    wps[i][1] = Double.parseDouble(tokens[3 + i * 2]);
                }
                obs = new WaypointObstacle(wps);
                break;
        }

        if (obs != null) {
            obstacles.add(obs);
        }
    }
}
