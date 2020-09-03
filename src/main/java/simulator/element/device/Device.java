package simulator.element.device;

import javafx.scene.image.Image;
import javafx.util.Pair;
import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.Port;
import util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class Device {
    private int id;
    private final Image image;
    private int x;
    private int y;
    private String name;
    private final String deviceType;
    private String macAddress;
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
        initPorts();
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

    public String getName() {
        return name;
    }

    public int getNumberOfPorts() {
        return portList.size();
    }

    public void setNumberOfPorts(int numberOfPorts) {
        //TODO: check if function works properly
        if (portList.size() < numberOfPorts) {
            for (int i = portList.size(); i < numberOfPorts; i++) {
                portList.add(new Port(i + 1));
            }
        } else if (portList.size() > numberOfPorts) {
            if (portList.size() > numberOfPorts + 1) {
                portList.subList(numberOfPorts + 1, portList.size()).clear();
            }
        }
    }

    public boolean hasPort(Port port) {
        return portList.contains(port);
    }

    public boolean hasEmptyPort() {
        for (Port port : portList) {
            if (!port.isPortTaken()) {
                return true;
            }
        }
        return false;
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

    public static int getIdCounter() {
        return idCounter;
    }

    public static void setIdCounter(int counter) {
        idCounter = counter;
    }

    abstract void initPorts();

    public abstract List<Message> handleMessage(Message message, List<Connection> connectionList);

    public static final class Builder {
        private int id;
        private int x;
        private int y;
        private String name;
        private String deviceType;
        private String macAddress;
        private List<Port> portList;
        private String ipAddress;
        private Map<String, String> routingTable;
        private List<Pair<String, Integer>> associationTable;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder x(int x) {
            this.x = x;
            return this;
        }

        public Builder y(int y) {
            this.y = y;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder deviceType(String deviceType) {
            this.deviceType = deviceType;
            return this;
        }

        public Builder macAddress(String macAddress) {
            this.macAddress = macAddress;
            return this;
        }

        public Builder portList(List<Port> portList) {
            if (this.portList == null) {
                this.portList = new ArrayList<>();
            }
            this.portList.clear();
            this.portList = portList;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder routingTable(Map<String, String> routingTable) {
            if (this.routingTable == null) {
                this.routingTable = new HashMap<>();
            }
            this.routingTable.clear();
            this.routingTable.putAll(routingTable);
            return this;
        }

        public Builder associationTable(List<Pair<String, Integer>> associationTable) {
            if (this.associationTable == null) {
                this.associationTable = new ArrayList<>();
            }
            this.associationTable.clear();
            this.associationTable.addAll(associationTable);
            return this;
        }

        public Device build() {
            Device device;
            if (this.deviceType.equals(Firewall.deviceType)) {
                device = new Firewall(this.x, this.y);
            } else if (this.deviceType.equals(Hub.deviceType)) {
                device = new Hub(this.x, this.y);
            } else if (this.deviceType.equals(Router.deviceType)) {
                device = new Router(this.x, this.y);
                ((Router) device).setRoutingTable(this.routingTable);
            } else if (this.deviceType.equals(Switch.deviceType)) {
                device = new Switch(this.x, this.y);
                ((Switch) device).setAssociationTable(this.associationTable);
            } else {
                device = new EndDevice(this.x, this.y);
                ((EndDevice) device).setIpAddress(this.ipAddress);
            }
            device.id = this.id;
            device.name = this.name;
            device.macAddress = this.macAddress;
            device.portList.clear();
            device.portList.addAll(this.portList);

            return device;
        }
    }
}
