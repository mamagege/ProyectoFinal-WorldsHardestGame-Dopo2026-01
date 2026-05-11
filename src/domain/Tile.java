package domain;

/**
 * Representa una baldosa visual en el damero del suelo.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class Tile extends Element {
    private String colorHex;

    public Tile(double positionX, double positionY, double width, double height, String colorHex) {
        super(positionX, positionY, width, height);
        this.colorHex = colorHex;
    }

    public String getColorHex() {
        return colorHex;
    }

    @Override
    public boolean isCircular() {
        return false;
    }
}
