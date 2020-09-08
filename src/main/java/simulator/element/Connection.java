package simulator.element;

import javafx.scene.paint.Color;
import javafx.util.Pair;
import simulator.Engine;
import simulator.element.device.additionalElements.Port;

import java.util.List;

public class Connection {
    private int id;
    private final Pair<Integer, Integer> portPair;
    private Color color;
    private static int idCounter;

    public Connection(int first, int second) {
        this.id = idCounter++;
        this.portPair = new Pair<>(first, second);
        color = Color.color(Math.random(), Math.random(), Math.random());
    }

    public int getId() {
        return id;
    }

    public Pair<Integer, Integer> getPortPair() {
        return portPair;
    }

    public int getFirstElementId() {
        return Engine.getInstance().getDeviceByPortId(portPair.getKey()).getId();
    }

    public int getSecondElementId() {
        return Engine.getInstance().getDeviceByPortId(portPair.getValue()).getId();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean containsPort(Port port) {
        return portPair.getKey() == port.getId() || portPair.getValue() == port.getId();
    }

    public Port getOtherPort(Port port) {
        if (port.getId() == portPair.getKey()) {
            return Engine.getInstance().getPortById(portPair.getValue());
        } else {
            return Engine.getInstance().getPortById(portPair.getKey());
        }
    }

    public Port getOtherPort(List<Port> portList) {
        for (Port port : portList) {
            if (port.getId() == portPair.getKey()) {
                return Engine.getInstance().getPortById(portPair.getValue());
            }
        }
        return Engine.getInstance().getPortById(portPair.getKey());
    }

    public static int getIdCounter() {
        return idCounter;
    }

    public static void setIdCounter(int counter) {
        idCounter = counter;
    }

    public static final class Builder {
        private int id;
        private Pair<Integer, Integer> portPair;
        private Color color;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder portPair(Pair<Integer, Integer> portPair) {
            this.portPair = portPair;
            return this;
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Connection build() {
            Connection connection = new Connection(portPair.getKey(), portPair.getValue());
            connection.id = this.id;
            connection.color = this.color;
            return connection;
        }
    }
}
