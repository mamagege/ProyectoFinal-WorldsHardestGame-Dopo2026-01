package domain;

import java.util.List;

/**
 * Representa el "Punto azul básico" que se desplaza en línea recta.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class EnemigoBasico extends ElementoTablero {
    private int velocidad;
    private boolean movimientoHorizontal;
    private int direccion; // 1 para derecha/abajo, -1 para izquierda/arriba

    public EnemigoBasico(Posicion posicion, int velocidad, boolean movimientoHorizontal) {
        super(posicion);
        this.velocidad = velocidad;
        this.movimientoHorizontal = movimientoHorizontal;
        this.direccion = 1; // Inicia moviéndose en dirección positiva
    }

    /**
     * Mueve el enemigo en su dirección actual. Si choca con una pared, rebota.
     * @param paredes Lista de paredes en el tablero para verificar colisiones.
     */
    public void mover(List<Pared> paredes) {
        int nuevoX = posicion.getX() + (movimientoHorizontal ? velocidad * direccion : 0);
        int nuevoY = posicion.getY() + (!movimientoHorizontal ? velocidad * direccion : 0);
        Posicion nuevaPosicion = new Posicion(nuevoX, nuevoY);

        if (chocaConPared(nuevaPosicion, paredes)) {
            rebotar();
        } else {
            this.posicion = nuevaPosicion;
        }
    }

    /**
     * Invierte la dirección de movimiento del enemigo.
     */
    public void rebotar() {
        this.direccion *= -1;
    }

    private boolean chocaConPared(Posicion pos, List<Pared> paredes) {
        for (Pared pared : paredes) {
            if (pared.getPosicion().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public boolean isMovimientoHorizontal() {
        return movimientoHorizontal;
    }

    public int getDireccion() {
        return direccion;
    }
}
