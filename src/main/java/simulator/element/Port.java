package simulator.element;

import javafx.util.Pair;
import simulator.view.IPTextField;

public class Port implements IPTextField.OnSaveClickedListener {
    private final Pair<Integer, Integer> id;
    private static int portIdCounter;
    private Interface anInterface;

    public Port(int deviceId) {
        this.id = new Pair<>(deviceId, portIdCounter++);
    }

    public Pair<Integer, Integer> getId() {
        return id;
    }

    public void setNewInterface() {
        this.anInterface = new Interface();
    }

    public boolean hasInterface() {
        return anInterface != null;
    }

    public String getIpAddress() {
        if (hasInterface()) {
            return anInterface.getAddress();
        } else {
            return null;
        }
    }

    @Override
    public void onSaveClicked(String ipAddress) {
        if (hasInterface()) {
            anInterface.setAddress(ipAddress);
        }
    }
}
