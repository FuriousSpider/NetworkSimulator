package simulator.element.device;

import simulator.Engine;
import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.Port;
import util.Values;

import java.util.ArrayList;
import java.util.List;

public class Hub extends Device {
    public static String fileName = "/hub.png";
    public static String deviceType = "Hub";

    public Hub(int x, int y) {
        super(fileName, x, y, deviceType);
    }

    @Override
    void initPorts() {
        for (int i = 0; i < Values.DEVICE_HUB_NUMBER_OF_PORTS; i++) {
            getPortList().add(new Port());
        }
    }

    @Override
    public List<Message> handleMessage(Message message, List<Connection> connectionList) {
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
