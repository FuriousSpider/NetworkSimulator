package simulator.element.device;

import simulator.Engine;
import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.device.additionalElements.Port;
import util.Values;

import java.util.ArrayList;
import java.util.List;

public class Hub extends Device {
    public static String fileName = "/hub.png";
    public static String deviceType = "Hub";
    public static int nameCounter = 1;

    public Hub(int x, int y) {
        super(fileName, x, y, deviceType);
    }

    @Override
    void initPorts() {
        for (int i = 0; i < Values.DEVICE_HUB_NUMBER_OF_PORTS; i++) {
            getPortList().add(new Port(i + 1));
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
        List<Message> messageList = new ArrayList<>();
        for (Connection connection : connectionList) {
            if (!connection.containsPort(message.getCurrentDestinationPort())) {
                if (connection.getFirstElementId() == this.getId()) {
                    messageList.add(
                            new Message(
                                    message,
                                    Engine.getInstance().getPortById(connection.getPortPair().getKey()),
                                    Engine.getInstance().getPortById(connection.getPortPair().getValue()),
                                    this,
                                    message.getCurrentIpDestinationAddress(),
                                    message.getVLanId(),
                                    "",
                                    true
                            )
                    );
                } else {
                    messageList.add(
                            new Message(
                                    message,
                                    Engine.getInstance().getPortById(connection.getPortPair().getValue()),
                                    Engine.getInstance().getPortById(connection.getPortPair().getKey()),
                                    this,
                                    message.getCurrentIpDestinationAddress(),
                                    message.getVLanId(),
                                    "",
                                    true
                            )
                    );
                }
            }
        }
        return messageList;
    }

    private List<Message> handleTestMessage(Message message, List<Connection> connectionList) {
        if (message.getTestHistory().contains(this)) {
            return new ArrayList<>();
        } else {
            return handleNormalMessage(message, connectionList);
        }
    }
}
