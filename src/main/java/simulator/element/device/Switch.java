package simulator.element.device;

import javafx.util.Pair;
import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.Port;
import util.Values;

import java.util.ArrayList;
import java.util.List;

public class Switch extends Device {
    public static String fileName = "/switch.png";
    public static String deviceType = "Switch";

    private final List<Pair<String, Port>> associationTable;

    public Switch(int x, int y) {
        super(fileName, x, y, deviceType);

        this.associationTable = new ArrayList<>();
    }

    @Override
    public List<Message> handleMessage(Message message, List<Connection> connectionList) {
        addNewRecord(message.getSourceMac(), message.getCurrentDestinationPort());
        if (associationTableContainsKey(message.getDestinationMac())) {
            Port sourcePort = getAssociationTablePort(message.getDestinationMac());
            Port destinationPort = null;
            for (Connection connection : connectionList) {
                if (sourcePort != null && connection.containsPort(sourcePort)) {
                    destinationPort = connection.getOtherPort(sourcePort);
                }
            }

            if (message.getCurrentDestinationPort() != destinationPort) {
                List<Message> messageList = new ArrayList<>();
                messageList.add(new Message(message, sourcePort, destinationPort));
                return messageList;
            } else {
                return new ArrayList<>();
            }
        } else {
            List<Message> messageList = new ArrayList<>();
            for (Connection connection : connectionList) {
                if (!connection.containsPort(message.getCurrentDestinationPort())) {
                    if (connection.getFirstElementId() == this.getId()) {
                        messageList.add(new Message(message, connection.getPortPair().getKey(), connection.getPortPair().getValue()));
                    } else {
                        messageList.add(new Message(message, connection.getPortPair().getValue(), connection.getPortPair().getKey()));
                    }
                }
            }
            return messageList;
        }
    }

    private void addNewRecord(String macAddress, Port port) {
        if (!associationTableContainsKey(macAddress)) {
            associationTable.add(new Pair<>(macAddress, port));
            if (associationTable.size() > Values.DEVICE_SWITCH_ASSOCIATION_TABLE_SIZE_LIMIT) {
                associationTable.remove(0);
            }
        }
    }

    private boolean associationTableContainsKey(String key) {
        for (Pair<String, Port> pair : associationTable) {
            if (pair.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    private Port getAssociationTablePort(String key) {
        for (Pair<String, Port> pair : associationTable) {
            if (pair.getKey().equals(key)) {
                return pair.getValue();
            }
        }
        return null;
    }
}
