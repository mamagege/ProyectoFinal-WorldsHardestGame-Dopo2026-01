package domain;

/**
 * Representa la zona verde (inicial y final).
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class ZonaSegura extends ElementoTablero {
    private boolean esInicial;
    private boolean esFinal;

    public ZonaSegura(Posicion posicion, boolean esInicial, boolean esFinal) {
        super(posicion);
        this.esInicial = esInicial;
        this.esFinal = esFinal;
    }

    public boolean isEsInicial() {
        return esInicial;
    }

    public boolean isEsFinal() {
        return esFinal;
    }
}
