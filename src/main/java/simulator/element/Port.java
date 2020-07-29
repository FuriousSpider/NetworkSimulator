package simulator.element;

import javafx.util.Pair;

public class Port {
    private final Pair<Integer, Integer> id;
    private static int portIdCounter;

    public Port(int deviceId) {
        this.id = new Pair<>(deviceId, portIdCounter++);
    }

    public Pair<Integer, Integer> getId() {
        return id;
    }
}
