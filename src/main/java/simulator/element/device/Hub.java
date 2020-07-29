package simulator.element.device;

import simulator.element.Connection;
import simulator.element.Message;

import java.util.ArrayList;
import java.util.List;

public class Hub extends Device {
    public static String fileName = "/hub.png";
    public static String deviceType = "Hub";

    public Hub(int x, int y) {
        super(fileName, x, y, deviceType);
    }

    @Override
    public List<Message> handleMessage(Message message, List<Connection> connectionList) {
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
