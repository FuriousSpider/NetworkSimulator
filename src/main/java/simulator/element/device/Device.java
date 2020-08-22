package simulator.element.device;

import javafx.scene.image.Image;
import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.Port;
import util.Utils;

import java.util.ArrayList;
import java.util.List;

abstract public class Device {
    private final int id;
    private final Image image;
    private int x;
    private int y;
    private String name;
    private final String deviceType;
    private final String macAddress;
    private final List<Port> portList;
    private static int idCounter = 0;

    protected Device(String imagePath, int x, int y, String deviceType) {
        this.image = new Image(imagePath);
        this.x = x;
        this.y = y;
        this.deviceType = deviceType;
        this.macAddress = Utils.generateMacAddress();
        this.id = idCounter++;
        this.name = String.valueOf(this.id);
        this.portList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public Image getImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Port getNewPort() {
        Port port = new Port(id);
        portList.add(port);
        return port;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public List<Port> getPortList() {
        return portList;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void removePort(Port port) {
        portList.remove(port);
    }

    public abstract List<Message> handleMessage(Message message, List<Connection> connectionList);
}
