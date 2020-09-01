package simulator.element.device;

import simulator.Engine;
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
                Port destinationPort;
                for (Connection connection : connectionList) {
                    if (connection.containsPort(port) && port != message.getCurrentDestinationPort()) {
                        destinationPort = connection.getOtherPort(port);
                        messageList.add(new Message(message, port, destinationPort));
                        return messageList;
                    }
                }

            }
        }
        for (String key : routingTable.keySet()) {
            if (key.contains(Utils.getNetworkAddressFromIp(message.getDestinationIpAddress()))) {
                String nextHop = routingTable.get(key);
                for (Port port : getPortList()) {
                    if (Utils.belongToTheSameNetwork(port.getIpAddress(), Engine.getInstance().getIpWithMaskByIp(nextHop))) {
                        for (Connection connection : connectionList) {
                            if (connection.containsPort(port) && port != message.getCurrentDestinationPort()) {
                                List<Message> messageList = new ArrayList<>();
                                messageList.add(new Message(message, port, connection.getOtherPort(port)));
                                return messageList;
                            }
                        }
                    }
                }
            }
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


    @Override
    public void onRoutingTableChange(Map<String, String> routingTable) {
        this.routingTable.clear();
        for (String key : routingTable.keySet()) {
            this.routingTable.put(key, routingTable.get(key));
        }
    }
}
