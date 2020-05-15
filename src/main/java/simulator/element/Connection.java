package simulator.element;

import javafx.scene.paint.Color;

public class Connection {
    private int firstId;
    private int secondId;
    private final Color color;

    public Connection(int firstId, int secondId) {
        this.firstId = firstId;
        this.secondId = secondId;
        color = Color.color(Math.random(), Math.random(), Math.random());
    }

    public int getFirstId() {
        return firstId;
    }

    public int getSecondId() {
        return secondId;
    }

    public Color getColor() {
        return color;
    }
}
