package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

/**
 * Clase de pruebas unitarias con JUnit 5 para verificar la lógica y reglas de
 * negocio
 * de "The DOPO Hardest Game", aisladas de la interfaz gráfica.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class JuegoWHGTest {

    private JuegoDOPO juego;
    private Jugador jugador;
    private EnemigoBasico enemigo;
    private Moneda moneda;
    private Pared pared;
    private ZonaSegura zonaInicial;
    private ZonaSegura zonaFinal;

    /**
     * Configuración del escenario básico reutilizable antes de cada prueba.
     * Crea un mini-tablero con todos los elementos necesarios.
     */
    @BeforeEach
    public void setUp() {
        // Preparar posiciones y elementos
        Posicion posInicialJugador = new Posicion(0, 0);
        jugador = new Jugador(posInicialJugador);

        zonaInicial = new ZonaSegura(new Posicion(0, 0), true, false);
        zonaFinal = new ZonaSegura(new Posicion(5, 5), false, true);

        pared = new Pared(new Posicion(1, 0)); // Pared que bloquea moverse hacia la derecha en la coordenada 1,0

        enemigo = new EnemigoBasico(new Posicion(0, 2), 1, true); // Enemigo ubicado en la coordenada 0,2

        moneda = new Moneda(new Posicion(2, 2)); // Moneda ubicada en la coordenada 2,2

        // Crear las listas requeridas por JuegoDOPO
        List<ZonaSegura> zonasSeguras = Arrays.asList(zonaInicial, zonaFinal);
        List<Pared> paredes = Arrays.asList(pared);
        List<EnemigoBasico> enemigos = Arrays.asList(enemigo);
        List<Moneda> monedas = Arrays.asList(moneda);

        // Instanciar la fachada del juego
        juego = new JuegoDOPO(jugador, enemigos, monedas, paredes, zonasSeguras);
    }

    @Test
    public void elCuadradoRojoSeMueveCorrectamente() {
        // Preparar
        Posicion nuevaPosicionVacia = new Posicion(0, 1);

        // Ejecutar
        juego.moverJugador(nuevaPosicionVacia);

        // Afirmar
        assertEquals(nuevaPosicionVacia, jugador.getPosicion(),
                "La posición del jugador debe actualizarse a la nueva coordenada vacía.");
    }

    @Test
    public void lasColisionesConParedesBloqueanElMovimiento() {
        // Preparar
        Posicion posicionOriginal = new Posicion(0, 0);
        Posicion posicionPared = new Posicion(1, 0); // Celda ocupada por la pared definida en setUp

        // Ejecutar
        juego.moverJugador(posicionPared);

        // Afirmar
        assertEquals(posicionOriginal, jugador.getPosicion(),
                "La posición del jugador NO debe cambiar si la celda destino es una pared.");
    }

    @Test
    public void tocarUnPuntoAzulGeneraUnaMuerteYReinicio() {
        // Preparar
        Posicion posicionEnemigo = new Posicion(0, 2);
        Posicion posicionInicialZonaSegura = new Posicion(0, 0);
        int muertesAntes = jugador.getMuertes(); // 0

        // Ejecutar
        juego.moverJugador(posicionEnemigo); // El jugador se mueve hacia el enemigo

        // Afirmar
        assertEquals(muertesAntes + 1, jugador.getMuertes(),
                "El contador de muertes del jugador debe incrementarse en 1.");
        assertEquals(posicionInicialZonaSegura, jugador.getPosicion(),
                "El jugador debe regresar a la posición de la Zona Segura inicial tras chocar con el enemigo.");
    }

    @Test
    public void recolectarTodasLasMonedasYLlegarAZonaVerdeActivaLaVictoria() {
        // Preparar
        Posicion posicionMoneda = new Posicion(2, 2);
        Posicion posicionZonaFinal = new Posicion(5, 5);

        // Ejecutar
        juego.moverJugador(posicionMoneda); // Paso 1: Ir a la moneda
        boolean recolectadaAlPasar = moneda.isRecolectada();

        juego.moverJugador(posicionZonaFinal); // Paso 2: Ir a la zona segura final
        boolean victoriaActiva = juego.isNivelCompletado();

        // Afirmar
        assertTrue(recolectadaAlPasar,
                "La moneda debe cambiar su estado a 'recolectada' al pasar por ella.");
        assertTrue(victoriaActiva,
                "El nivel debe estar completado (victoria) al tener todas las monedas y pisar la zona segura final.");
    }
}
