package simulator.element;

import javafx.scene.image.Image;
import simulator.Engine;
import simulator.element.device.Device;
import util.Values;

import java.util.ArrayList;
import java.util.List;

public class Message {
    public static String fileName = "/message.png";

    private final String sourceMac;
    private final String destinationMac;
    private final Port currentSourcePort;
    private final Port currentDestinationPort;
    private final List<Message> history;
    private int progress;
    private final Image image;

    public Message(String sourceMac, String destinationMac, Port currentSourcePort, Port currentDestinationPort) {
        this.sourceMac = sourceMac;
        this.destinationMac = destinationMac;
        this.currentSourcePort = currentSourcePort;
        this.currentDestinationPort = currentDestinationPort;
        this.history = new ArrayList<>();
        this.progress = 0;
        this.image = new Image(fileName);
    }

    public Message(Message message, Port currentSourcePort, Port currentDestinationPort) {
        this.sourceMac = message.sourceMac;
        this.destinationMac = message.destinationMac;
        this.currentSourcePort = currentSourcePort;
        this.currentDestinationPort = currentDestinationPort;
        this.history = message.history;
        history.add(message);
        this.progress = 0;
        this.image = new Image(fileName);
    }

    public int getX() {
        Device from = Engine.getInstance().getDeviceByPort(currentSourcePort);
        Device to = Engine.getInstance().getDeviceByPort(currentDestinationPort);
        return from.getX() + (to.getX() - from.getX()) * progress / Values.MESSAGE_PROGRESS_MAX;
    }

    public int getY() {
        Device from = Engine.getInstance().getDeviceByPort(currentSourcePort);
        Device to = Engine.getInstance().getDeviceByPort(currentDestinationPort);
        return from.getY() + (to.getY() - from.getY()) * progress / Values.MESSAGE_PROGRESS_MAX;
    }

    public String getSourceMac() {
        return sourceMac;
    }

    public String getDestinationMac() {
        return destinationMac;
    }

    public Port getCurrentSourcePort() {
        return currentSourcePort;
    }

    public Port getCurrentDestinationPort() {
        return currentDestinationPort;
    }

    public int getProgress() {
        return progress;
    }

    public Image getImage() {
        return image;
    }

    public void nextStep() {
        progress += Values.MESSAGE_PROGRESS_STEP;
    }
}
