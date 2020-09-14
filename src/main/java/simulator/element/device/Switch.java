package simulator.element.device;

import simulator.Engine;
import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.device.additionalElements.AssociationTableEntry;
import simulator.element.device.additionalElements.History;
import simulator.element.device.additionalElements.Port;
import simulator.view.SwitchButtonsView;
import util.Values;

import java.util.ArrayList;
import java.util.List;

public class Switch extends Device implements SwitchButtonsView.OnClearMacTableClickedListener {
    public static String fileName = "/switch.jpg";
    public static String deviceType = "Switch";
    public static int nameCounter = 1;

    private final List<AssociationTableEntry> associationTable;

    public Switch(int x, int y) {
        super(fileName, x, y, deviceType);

        this.associationTable = new ArrayList<>();
    }

    @Override
    void initPorts() {
        for (int i = 0; i < Values.DEVICE_SWITCH_NUMBER_OF_PORTS; i++) {
            Port port = new Port(i + 1);
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
            case GENERATE:
            case TEST:
                return handleTestMessage(message, connectionList);
        }
        return new ArrayList<>();
    }

    private List<Message> handleNormalMessage(Message message, List<Connection> connectionList) {
        Integer vLanId = message.getVLanId();
        if (vLanId == null) {
            vLanId = message.getCurrentDestinationPort().getVLanId();
        }

        if (message.getVLanId() == null && message.getCurrentDestinationPort().isInTrunkMode()) {
            List<Message> messageList = new ArrayList<>();
            for (int id : message.getCurrentDestinationPort().getTrunkModeAllowedIds()) {
                messageList.addAll(handleNormalMessage(new Message(
                        message,
                        message.getCurrentSourcePort(),
                        message.getCurrentDestinationPort(),
                        this,
                        message.getCurrentIpDestinationAddress(),
                        id,
                        "",
                        false
                ), connectionList));
            }
            return messageList;
        } else {
            addNewRecord(vLanId, message.getSourceMac(), message.getCurrentDestinationPort());

            List<Port> vLanPortList;
            if (message.getCurrentDestinationPort().hasVLan()) {
                vLanPortList = getPortListByVLanAndTrunk(vLanId);
            } else {
                vLanPortList = getPortList();
            }

            if (associationTableContainsKey(vLanId, message.getDestinationMac()) && nextHopBelongsToVLan(vLanId, message.getDestinationMac(), message.getCurrentDestinationPort())) {
                Port sourcePort = getAssociationTablePort(vLanId, message.getDestinationMac());
                Connection connection = getConnectionByPort(sourcePort, connectionList);
                List<Message> messageList = new ArrayList<>();
                if (connection != null && sourcePort != null) {
                    Message msg = new Message(
                            message,
                            sourcePort,
                            connection.getOtherPort(sourcePort),
                            this,
                            message.getCurrentIpDestinationAddress(),
                            vLanId,
                            "by MAC Address table entry",
                            true
                    );
                    History lastHistory = message.getHistoryList().get(message.getHistoryList().size() - 1);
                    msg.updateLastHistoryPacketInfo(
                            lastHistory.getPacketInfoSourceIp(),
                            lastHistory.getPacketInfoDestinationIp()
                    );
                    msg.updateLastHistoryFrameInfo(
                            lastHistory.getFrameInfoSourceMac(),
                            lastHistory.getFrameInfoDestinationMac()
                    );
                    messageList.add(msg);
                }
                return messageList;
            } else {
                List<Message> messageList = new ArrayList<>();
                for (Port sourcePort : vLanPortList) {
                    Connection connection = getConnectionByPort(sourcePort, connectionList);
                    if (connection != null && !sourcePort.equals(message.getCurrentDestinationPort())) {
                        Message msg = new Message(
                                message,
                                sourcePort,
                                connection.getOtherPort(sourcePort),
                                this,
                                message.getCurrentIpDestinationAddress(),
                                vLanId,
                                "send to all",
                                true
                        );
                        History lastHistory = message.getHistoryList().get(message.getHistoryList().size() - 1);
                        msg.updateLastHistoryPacketInfo(
                                lastHistory.getPacketInfoSourceIp(),
                                lastHistory.getPacketInfoDestinationIp()
                        );
                        msg.updateLastHistoryFrameInfo(
                                lastHistory.getFrameInfoSourceMac(),
                                lastHistory.getFrameInfoDestinationMac()
                        );
                        messageList.add(msg);
                    }
                }
                return messageList;
            }
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
                                        Engine.getInstance().getPortById(connection.getPortPair().getValue()),
                                        this,
                                        message.getCurrentIpDestinationAddress(),
                                        0,
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
                                        0,
                                        "",
                                        true
                                )
                        );
                    }
                }
            }
            return messageList;
        }
    }

    public List<AssociationTableEntry> getAssociationTable() {
        return associationTable;
    }

    private void addNewRecord(int vLanId, String macAddress, Port port) {
        if (!associationTableContainsKey(vLanId, macAddress)) {
            associationTable.add(new AssociationTableEntry(vLanId, macAddress, port.getId()));
            if (associationTable.size() > Values.DEVICE_SWITCH_ASSOCIATION_TABLE_SIZE_LIMIT) {
                associationTable.remove(0);
            }
        }
    }

    public void setAssociationTable(List<AssociationTableEntry> associationTable) {
        this.associationTable.clear();
        this.associationTable.addAll(associationTable);
    }

    private boolean associationTableContainsKey(int vLanId, String macAddress) {
        for (AssociationTableEntry entry : associationTable) {
            if (entry.getvLanId() == vLanId && entry.getMacAddress().equals(macAddress)) {
                return true;
            }
        }
        return false;
    }

    private Port getAssociationTablePort(int vLanId, String macAddress) {
        for (AssociationTableEntry entry : associationTable) {
            if (entry.getvLanId() == vLanId && entry.getMacAddress().equals(macAddress)) {
                return Engine.getInstance().getPortById(entry.getPortId());
            }
        }
        return null;
    }

    private List<Port> getPortListByVLanAndTrunk(Integer vLanId) {
        List<Port> list = new ArrayList<>();
        for (Port port : getPortList()) {
            if (port.isPortTaken() && port.hasVLan()) {
                if (port.getVLanId() == vLanId || port.getTrunkModeAllowedIds().contains(vLanId)) {
                    list.add(port);
                }
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

    private boolean nextHopBelongsToVLan(int vLanId, String destinationMac, Port currentDestinationPort) {
        Port sourcePort = getAssociationTablePort(vLanId, destinationMac);
        if (sourcePort != null && sourcePort.hasVLan()) {
            return sourcePort.getVLanId() == currentDestinationPort.getVLanId();
        } else {
            return sourcePort != null && !sourcePort.hasVLan();
        }
    }

    @Override
    public void onClearMacTableClicked() {
        associationTable.clear();
    }
}
