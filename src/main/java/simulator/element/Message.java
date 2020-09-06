package simulator.element;

import javafx.scene.image.Image;
import simulator.Engine;
import simulator.element.device.Device;
import simulator.element.device.additionalElements.History;
import simulator.element.device.additionalElements.Policy;
import simulator.element.device.additionalElements.Port;
import util.Values;

import java.util.ArrayList;
import java.util.List;

public class Message {
    public static String fileName = "/message.png";

    private final String sourceIpAddress;
    private final String destinationIpAddress;
    private final String sourceMac;
    private final String destinationMac;
    private final Port currentSourcePort;
    private final Port currentDestinationPort;
    private int progress;
    private final Image image;
    private final Policy.Application application;
    private final Type type;
    private final List<History> history;
    private final List<Device> testHistory;

    public Message(String sourceIpAddress, String destinationIpAddress, String sourceMac, String destinationMac, Port currentSourcePort, Port currentDestinationPort, Policy.Application application, Type type) {
        this.sourceIpAddress = sourceIpAddress;
        this.destinationIpAddress = destinationIpAddress;
        this.sourceMac = sourceMac;
        this.destinationMac = destinationMac;
        this.currentSourcePort = currentSourcePort;
        this.currentDestinationPort = currentDestinationPort;
        this.history = new ArrayList<>();
        this.progress = 0;
        this.image = new Image(fileName);
        this.application = application;
        this.type = type;
        this.testHistory = new ArrayList<>();
        this.testHistory.add(Engine.getInstance().getDeviceByPort(currentSourcePort));
    }

    public Message(Message message, Port currentSourcePort, Port currentDestinationPort, Device device, History.Decision decision, String decisionValue) {
        this.sourceIpAddress = message.sourceIpAddress;
        this.destinationIpAddress = message.destinationIpAddress;
        this.sourceMac = message.sourceMac;
        this.destinationMac = message.destinationMac;
        this.currentSourcePort = currentSourcePort;
        this.currentDestinationPort = currentDestinationPort;
        this.progress = 0;
        this.image = new Image(fileName);
        this.application = message.application;
        this.type = message.type;
        this.history = message.history;
        history.add(new History(this, device, decision, decisionValue, message.getCurrentDestinationPort(), currentSourcePort));
        this.testHistory = message.testHistory;
        testHistory.add(Engine.getInstance().getDeviceByPort(message.getCurrentSourcePort()));
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

    public String getSourceIpAddress() {
        return sourceIpAddress;
    }

    public String getDestinationIpAddress() {
        return destinationIpAddress;
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

    public Policy.Application getApplication() {
        return application;
    }

    public List<Device> getTestHistory() {
        return testHistory;
    }

    public Type getType() {
        return this.type;
    }

    public List<History> getHistoryList() {
        return history;
    }

    public enum Type {
        TEST,
        NORMAL
    }
}
