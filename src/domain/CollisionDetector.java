package domain;

/**
 * Detector de colisiones que aplica el Principio de Responsabilidad Única (SRP).
 * Maneja las matemáticas para colisiones Rectángulo-Rectángulo, Círculo-Círculo y Círculo-Rectángulo.
 * 
 * @author Oscar Lasso - Juan Gaitan
 * @version 2026
 */
public class CollisionDetector {

    public static boolean checkCollision(Element e1, Element e2) {
        if (e1.isCircular() && e2.isCircular()) {
            return checkCircleCircle(e1, e2);
        } else if (!e1.isCircular() && !e2.isCircular()) {
            return checkRectRect(e1, e2);
        } else {
            // Uno es círculo y el otro es rectángulo
            Element circle = e1.isCircular() ? e1 : e2;
            Element rect = e1.isCircular() ? e2 : e1;
            return checkCircleRect(circle, rect);
        }
    }

    private static boolean checkRectRect(Element r1, Element r2) {
        return r1.getPositionX() < r2.getPositionX() + r2.getWidth() &&
               r1.getPositionX() + r1.getWidth() > r2.getPositionX() &&
               r1.getPositionY() < r2.getPositionY() + r2.getHeight() &&
               r1.getPositionY() + r1.getHeight() > r2.getPositionY();
    }

    private static boolean checkCircleCircle(Element c1, Element c2) {
        double dx = c1.getCenterX() - c2.getCenterX();
        double dy = c1.getCenterY() - c2.getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (c1.getRadius() + c2.getRadius());
    }

    private static boolean checkCircleRect(Element circle, Element rect) {
        // Encontrar el punto más cercano en el rectángulo al centro del círculo
        double closestX = Math.max(rect.getPositionX(), Math.min(circle.getCenterX(), rect.getPositionX() + rect.getWidth()));
        double closestY = Math.max(rect.getPositionY(), Math.min(circle.getCenterY(), rect.getPositionY() + rect.getHeight()));

        // Calcular la distancia desde el centro del círculo hasta ese punto más cercano
        double dx = circle.getCenterX() - closestX;
        double dy = circle.getCenterY() - closestY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Hay colisión si la distancia es menor que el radio del círculo
        return distance < circle.getRadius();
    }
}
