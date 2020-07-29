package simulator.element.device;

import javafx.scene.image.Image;
import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.Port;

import java.util.ArrayList;
import java.util.List;

abstract public class Element {
    private final int id;
    private final Image image;
    private int x;
    private int y;
    private final String deviceType;
    private final List<Port> portList;
    private static int idCounter = 0;

    protected Element(String imagePath, int x, int y, String deviceType) {
        this.image = new Image(imagePath);
        this.x = x;
        this.y = y;
        this.deviceType = deviceType;
        this.id = idCounter++;
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

    public abstract List<Message> handleMessage(Element source, List<Connection> connectionList);
}
