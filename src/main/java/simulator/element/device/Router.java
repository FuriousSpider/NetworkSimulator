package simulator.element.device;

import javafx.application.Platform;
import simulator.Engine;
import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.device.additionalElements.Port;
import simulator.view.RoutingTable;
import util.Utils;
import util.Values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Router extends Device implements RoutingTable.OnRoutingTableChangeListener {
    public static String fileName = "/router.png";
    public static String deviceType = "Router";
    private final Map<String, String> routingTable;

    public Router(int x, int y) {
        super(fileName, x, y, deviceType);
        routingTable = new HashMap<>();
    }

    @Override
    void initPorts() {
        for (int i = 0; i < Values.DEVICE_ROUTER_NUMBER_OF_PORTS; i++) {
            Port port = new Port(i + 1);
            port.setNewInterface();
            getPortList().add(port);
        }
    }

    @Override
    void initName() {
        setDeviceName(deviceType);
    }

    @Override
    public List<Message> handleMessage(Message message, List<Connection> connectionList) {
        switch (message.getType()) {
            case NORMAL:
                return handleNormalMessage(message, connectionList);
            case TEST:
                return handleTestMessage(message, connectionList);
        }
        return new ArrayList<>();
    }

    private List<Message> handleNormalMessage(Message message, List<Connection> connectionList) {
        if (isProperRecipient(message)) {
            for (Port port : getPortList()) {
                if (Utils.belongToTheSameNetwork(message.getDestinationIpAddress(), port.getIpAddress())) {
                    List<Message> messageList = new ArrayList<>();
                    Port destinationPort;
                    for (Connection connection : connectionList) {
                        if (connection.containsPort(port) && port != message.getCurrentDestinationPort()) {
                            destinationPort = connection.getOtherPort(port);
                            messageList.add(new Message(
                                    message,
                                    port,
                                    destinationPort,
                                    this,
                                    "0.0.0.0",
                                    "direct connection"
                            ));
                            return messageList;
                        }
                    }

                }
            }
            for (String key : routingTable.keySet()) {
                if (key.contains(Utils.getNetworkAddressFromIp(message.getDestinationIpAddress()))) {
                    String nextHop = routingTable.get(key);
                    for (Port port : getPortList()) {
                        if (Utils.belongToTheSameNetwork(port.getIpAddress(), nextHop)) {
                            for (Connection connection : connectionList) {
                                if (connection.containsPort(port) && port != message.getCurrentDestinationPort()) {
                                    List<Message> messageList = new ArrayList<>();
                                    messageList.add(new Message(
                                            message,
                                            port,
                                            connection.getOtherPort(port),
                                            this,
                                            Utils.getIpAddressWithoutMask(nextHop),
                                            "by routing table entry"
                                    ));
                                    return messageList;
                                }
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    private List<Message> handleTestMessage(Message message, List<Connection> connectionList) {
        if (!Utils.belongToTheSameNetwork(message.getCurrentDestinationPort().getIpAddress(), message.getSourceIpAddress())) {
            Device otherDevice = Engine.getInstance().getDeviceByIPAddress(message.getSourceIpAddress());
            if (otherDevice == null) {
                otherDevice = Engine.getInstance().getDeviceByPortIpAddress(message.getSourceIpAddress());
            }
            String errorMessage = "Wrong network configuration\n" +
                    getDeviceName() +
                    " and " +
                    otherDevice.getDeviceName() +
                    " should belong to the same network";
            Platform.runLater(() -> Engine.getInstance().logError(errorMessage));
            Engine.setTestNetworkSuccessful(false);
        } else if (message.getCurrentDestinationPort().getIpAddress().equals(message.getSourceIpAddress())) {
            Device otherDevice = Engine.getInstance().getDeviceByIPAddress(message.getSourceIpAddress());
            if (otherDevice == null) {
                otherDevice = Engine.getInstance().getDeviceByPortIpAddress(message.getSourceIpAddress());
            }
            String errorMessage = "Wrong network configuration\n" +
                    getDeviceName() +
                    " and " +
                    otherDevice.getDeviceName() +
                    " should not have the same ip address";
            Platform.runLater(() -> Engine.getInstance().logError(errorMessage));
            Engine.setTestNetworkSuccessful(false);
        }
        return new ArrayList<>();
    }

    public Map<String, String> getRoutingTableCopy() {
        Map<String, String> map = new HashMap<>();
        for (String key : routingTable.keySet()) {
            map.put(key, routingTable.get(key));
        }
        return map;
    }

    public void setRoutingTable(Map<String, String> routingTable) {
        this.routingTable.clear();
        this.routingTable.putAll(routingTable);
    }

    private boolean isProperRecipient(Message message) {
        return (!message.getCurrentIpDestinationAddress().isEmpty()
                && message.getCurrentDestinationPort().getIpAddress().contains(message.getCurrentIpDestinationAddress())
                && !Utils.belongToTheSameNetwork(message.getDestinationIpAddress(), message.getCurrentDestinationPort().getIpAddress())
                || message.getCurrentIpDestinationAddress().equals("0.0.0.0"));
    }

    @Override
    public void onRoutingTableChange(Map<String, String> routingTable) {
        this.routingTable.clear();
        for (String key : routingTable.keySet()) {
            this.routingTable.put(key, routingTable.get(key));
        }
    }
}
