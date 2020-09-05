package simulator.element.device;

import javafx.util.Pair;
import simulator.Engine;
import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.device.additionalElements.Port;
import util.Values;

import java.util.ArrayList;
import java.util.List;

public class Switch extends Device {
    public static String fileName = "/switch.png";
    public static String deviceType = "Switch";

    private final List<Pair<String, Integer>> associationTable;

    public Switch(int x, int y) {
        super(fileName, x, y, deviceType);

        this.associationTable = new ArrayList<>();
    }

    @Override
    void initPorts() {
        for (int i = 0; i < Values.DEVICE_SWITCH_NUMBER_OF_PORTS; i++) {
            Port port = new Port(i + 1);
            port.setVLan();
            getPortList().add(port);
        }
    }

    @Override
    void initName() {
        setDeviceName(deviceType);
    }

    //TODO: check if trunk mode works properly
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
        addNewRecord(message.getSourceMac(), message.getCurrentDestinationPort());
        List<Port> vLanPortList = getPortListByVLanAndTrunk(message.getCurrentDestinationPort().getVLanId());
        if (associationTableContainsKey(message.getDestinationMac()) && nextHopBelongsToVLan(message.getDestinationMac(), message.getCurrentDestinationPort())) {
            Port sourcePort = getAssociationTablePort(message.getDestinationMac());
            Connection connection = getConnectionByPort(sourcePort, connectionList);
            List<Message> messageList = new ArrayList<>();
            if (connection != null && sourcePort != null) {
                messageList.add(new Message(message, sourcePort, connection.getOtherPort(sourcePort)));
            }
            return messageList;
        } else {
            List<Message> messageList = new ArrayList<>();
            for (Port sourcePort : vLanPortList) {
                Connection connection = getConnectionByPort(sourcePort, connectionList);
                if (connection != null && !sourcePort.equals(message.getCurrentDestinationPort())) {
                    messageList.add(new Message(message, sourcePort, connection.getOtherPort(sourcePort)));
                }
            }
            return messageList;
        }
    }

    private List<Message> handleTestMessage(Message message, List<Connection> connectionList) {
        if (message.getTestHistory().contains(this)) {
            return new ArrayList<>();
        } else {
            List<Message> messageList = new ArrayList<>();
            for (Connection connection : connectionList) {
                if (!connection.containsPort(message.getCurrentDestinationPort())) {
                    if (connection.getFirstElementId() == this.getId()) {
                        messageList.add(
                                new Message(
                                        message,
                                        Engine.getInstance().getPortById(connection.getPortPair().getKey()),
                                        Engine.getInstance().getPortById(connection.getPortPair().getValue())
                                )
                        );
                    } else {
                        messageList.add(
                                new Message(
                                        message,
                                        Engine.getInstance().getPortById(connection.getPortPair().getValue()),
                                        Engine.getInstance().getPortById(connection.getPortPair().getKey())
                                )
                        );
                    }
                }
            }
            return messageList;
        }
    }

    public List<Pair<String, Integer>> getAssociationTable() {
        return associationTable;
    }

    private void addNewRecord(String macAddress, Port port) {
        if (!associationTableContainsKey(macAddress)) {
            associationTable.add(new Pair<>(macAddress, port.getId()));
            if (associationTable.size() > Values.DEVICE_SWITCH_ASSOCIATION_TABLE_SIZE_LIMIT) {
                associationTable.remove(0);
            }
        }
    }

    public void setAssociationTable(List<Pair<String, Integer>> associationTable) {
        this.associationTable.clear();
        this.associationTable.addAll(associationTable);
    }

    private boolean associationTableContainsKey(String key) {
        for (Pair<String, Integer> pair : associationTable) {
            if (pair.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    private Port getAssociationTablePort(String key) {
        for (Pair<String, Integer> pair : associationTable) {
            if (pair.getKey().equals(key)) {
                return Engine.getInstance().getPortById(pair.getValue());
            }
        }
        return null;
    }

    private List<Port> getPortListByVLanAndTrunk(int vLanId) {
        List<Port> list = new ArrayList<>();
        for (Port port : getPortList()) {
            if ((port.hasVLan() && port.getVLanId() == vLanId)
                    || (port.hasVLan() && port.getTrunkModeAllowedIds().contains(vLanId))) {
                list.add(port);
            }
        }
        return list;
    }

    private Connection getConnectionByPort(Port port, List<Connection> connectionList) {
        for (Connection connection : connectionList) {
            if (connection.containsPort(port)) {
                return connection;
            }
        }
        return null;
    }

    private boolean nextHopBelongsToVLan(String destinationMac, Port currentDestinationPort) {
        Port sourcePort = getAssociationTablePort(destinationMac);
        if (sourcePort != null && sourcePort.hasVLan()) {
            return sourcePort.getVLanId() == currentDestinationPort.getVLanId();
        }
        return false;
    }
}
