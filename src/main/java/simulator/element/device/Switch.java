package simulator.element.device;

import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.Port;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Switch extends Device {
    public static String fileName = "/switch.png";
    public static String deviceType = "Switch";

    private Map<String, Port> associationTable;

    public Switch(int x, int y) {
        super(fileName, x, y, deviceType);

        this.associationTable = new HashMap<>();
    }

    @Override
    public List<Message> handleMessage(Message message, List<Connection> connectionList) {
        addNewRecord(message.getSourceMac(), message.getCurrentDestinationPort());
        if (associationTable.containsKey(message.getDestinationMac())) {
            Port sourcePort = associationTable.get(message.getDestinationMac());
            Port destinationPort = null;
            for (Connection connection : connectionList) {
                if (connection.containsPort(sourcePort)) {
                    destinationPort = connection.getOtherPort(sourcePort);
                }
            }

            List<Message> messageList = new ArrayList<>();
            messageList.add(new Message(message, sourcePort, destinationPort));
            return messageList;
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
        //TODO: handle table overload
        if (!associationTable.containsKey(macAddress)) {
            associationTable.put(macAddress, port);
        }
    }
}
