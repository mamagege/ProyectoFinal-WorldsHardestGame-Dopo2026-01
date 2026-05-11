package domain;

/**
 * Clase que representa una zona especial para seleccionar la modalidad del juego.
 * Hereda de Zone respetando el principio Abierto/Cerrado (OCP).
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class ModalityZone extends Zone {
    private final Modality modality;

    public ModalityZone(double positionX, double positionY, double width, double height, Modality modality) {
        super(positionX, positionY, width, height);
        this.modality = modality;
    }

    public Modality getModality() {
        return modality;
    }
}
