package simulator.element;

import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.List;

public class Connection {
    private final int id;
    private final Pair<Port, Port> portPair;
    private final Color color;
    private static int idCounter;

    public Connection(Port first, Port second) {
        this.id = idCounter++;
        this.portPair = new Pair<>(first, second);
        color = Color.color(Math.random(), Math.random(), Math.random());
    }

    public int getId() {
        return id;
    }

    public Pair<Port, Port> getPortPair() {
        return portPair;
    }

    public int getFirstElementId() {
        return portPair.getKey().getId().getKey();
    }

    public int getSecondElementId() {
        return portPair.getValue().getId().getKey();
    }

    public Color getColor() {
        return color;
    }

    public boolean containsPort(Port port) {
        return portPair.getKey().equals(port) || portPair.getValue().equals(port);
    }

    public Port getOtherPort(List<Port> portList) {
        for (Port port : portList) {
            if (port.equals(portPair.getKey())) {
                return portPair.getValue();
            } else if (port.equals(portPair.getValue())) {
                return portPair.getKey();
            }
        }
        return portPair.getKey();
    }
}
