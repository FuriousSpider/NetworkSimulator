package simulator.element.device;

import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.Port;
import simulator.view.RoutingTable;
import util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Router extends Device implements RoutingTable.OnRoutingTableChangeListener {
    public static String fileName = "/router.png";
    public static String deviceType = "Router";
    public Map<String, String> routingTable;

    public Router(int x, int y) {
        super(fileName, x, y, deviceType);
        routingTable = new HashMap<>();
    }

    @Override
    public Port getNewPort() {
        Port port = super.getNewPort();
        port.setNewInterface();
        return port;
    }

    @Override
    public List<Message> handleMessage(Message message, List<Connection> connectionList) {
        for (Port port : getPortList()) {
            if (Utils.belongToTheSameNetwork(message.getDestinationIpAddress(), port.getIpAddress())) {
                List<Message> messageList = new ArrayList<>();
                Port destinationPort = null;
                for (Connection connection : connectionList) {
                    if (connection.containsPort(port)) {
                        destinationPort = connection.getOtherPort(port);
                    }
                }
                messageList.add(new Message(message, port, destinationPort));
                return messageList;
            }
        }
        //TODO: change to checking by network address rather than by specific ip address because it's not working when hub/switch between routers
        for (String key : routingTable.keySet()) {
            if (Utils.getNetworkAddressFromIp(message.getDestinationIpAddress()).equals(key)) {
                String nextHop = routingTable.get(key);
                for (Port port : getPortList()) {
                    for (Connection connection : connectionList) {
                        if (connection.containsPort(port) && connection.getOtherPort(port).getIpAddress() != null) {
                            Port otherPort = connection.getOtherPort(port);
                            if (otherPort.getIpAddress().split("/")[0].equals(nextHop)) {
                                List<Message> messageList = new ArrayList<>();
                                messageList.add(new Message(message, port, otherPort));
                                return messageList;
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    public Map<String, String> getRoutingTable() {
        return routingTable;
    }

    @Override
    public void onRoutingTableChange(Map<String, String> routingTable) {
        this.routingTable = routingTable;
    }
}
