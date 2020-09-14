package simulator.element.device;

import javafx.application.Platform;
import simulator.Engine;
import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.device.additionalElements.History;
import simulator.element.device.additionalElements.Port;
import simulator.view.RoutingTable;
import util.Utils;
import util.Values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Router extends Device implements RoutingTable.OnRoutingTableChangeListener {
    public static String fileName = "/router.jpg";
    public static String deviceType = "Router";
    private final Map<String, String> routingTable;
    public static int nameCounter = 1;

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
        setDeviceName(deviceType + nameCounter++);
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
            List<Message> messageList = new ArrayList<>();
            for (Port port : getPortList()) {
                if (Utils.belongToTheSameNetwork(message.getDestinationIpAddress(), port.getIpAddress())) {
                    Port destinationPort;
                    for (Connection connection : connectionList) {
                        if (connection.containsPort(port) && port != message.getCurrentDestinationPort()) {
                            destinationPort = connection.getOtherPort(port);
                            Message msg = new Message(
                                    message,
                                    port,
                                    destinationPort,
                                    this,
                                    message.getDestinationIpAddress(),
                                    null,
                                    "direct connection",
                                    true
                            );
                            History lastHistory = message.getHistoryList().get(message.getHistoryList().size() - 1);
                            msg.updateLastHistoryPacketInfo(
                                    lastHistory.getPacketInfoSourceIp(),
                                    lastHistory.getPacketInfoDestinationIp()
                            );
                            msg.updateLastHistoryFrameInfo(
                                    getMacAddress(),
                                    Engine.getInstance().getDeviceByIPAddress(message.getDestinationIpAddress()).getMacAddress()
                            );

                            messageList.add(msg);
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
                                    Message msg = new Message(
                                            message,
                                            port,
                                            connection.getOtherPort(port),
                                            this,
                                            Utils.getIpAddressWithoutMask(nextHop),
                                            null,
                                            "by routing table entry",
                                            true
                                    );
                                    History lastHistory = message.getHistoryList().get(message.getHistoryList().size() - 1);
                                    msg.updateLastHistoryPacketInfo(
                                            lastHistory.getPacketInfoSourceIp(),
                                            lastHistory.getPacketInfoDestinationIp()
                                    );
                                    msg.updateLastHistoryFrameInfo(
                                            getMacAddress(),
                                            Engine.getInstance().getDeviceByPortIpAddress(nextHop).getMacAddress()
                                    );

                                    messageList.add(msg);
                                    return messageList;
                                }
                            }
                        }
                    }
                }
            }

        } else if (message.getCurrentSourcePort().isInTrunkMode()) {
            List<Message> messageList = new ArrayList<>();
            for (Integer vLanId : message.getCurrentSourcePort().getTrunkModeAllowedIds()) {
                if (!vLanId.equals(message.getVLanId())) {
                    Message msg = new Message(
                            message,
                            message.getCurrentDestinationPort(),
                            message.getCurrentSourcePort(),
                            this,
                            ((EndDevice) Engine.getInstance().getDeviceByMacAddress(message.getDestinationMac())).getIpAddress(),
                            vLanId,
                            "by one-armed router",
                            true
                    );
                    History lastHistory = message.getHistoryList().get(message.getHistoryList().size() - 1);

                    msg.updateLastHistoryPacketInfo(
                            lastHistory.getPacketInfoSourceIp(),
                            lastHistory.getPacketInfoDestinationIp()
                    );
                    msg.updateLastHistoryFrameInfo(
                            getMacAddress(),
                            message.getDestinationMac()
                    );

                    messageList.add(msg);
                }
            }
            return messageList;
        }
        return new ArrayList<>();
    }

    private List<Message> handleTestMessage(Message message, List<Connection> connectionList) {
        if (!Utils.belongToTheSameNetwork(message.getCurrentDestinationPort().getIpAddress(), message.getSourceIpAddress())) {
            Device otherDevice = Engine.getInstance().getDeviceByMacAddress(message.getSourceMac());
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
                && !Utils.belongToTheSameNetwork(message.getDestinationIpAddress(), message.getCurrentDestinationPort().getIpAddress()));
    }

    @Override
    public void onRoutingTableChange(Map<String, String> routingTable) {
        this.routingTable.clear();
        for (String key : routingTable.keySet()) {
            this.routingTable.put(key, routingTable.get(key));
        }
    }
}
