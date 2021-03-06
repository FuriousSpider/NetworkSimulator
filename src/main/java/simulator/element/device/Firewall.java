package simulator.element.device;

import javafx.application.Platform;
import javafx.util.Pair;
import simulator.Manager;
import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.device.additionalElements.History;
import simulator.element.device.additionalElements.Policy;
import simulator.element.device.additionalElements.Port;
import simulator.view.FirewallPoliciesView;
import util.Utils;
import util.Values;

import java.util.*;

public class Firewall extends Device implements FirewallPoliciesView.OnPoliciesListUpdatedListener {
    public static String fileName = "/firewall.jpg";
    public static String deviceType = "Firewall";
    private final List<Policy> policyList;
    private Policy.Rule defaultRule;
    private final List<Pair<Port, String>> routingTable;
    public static int nameCounter = 1;

    public Firewall(int x, int y) {
        super(fileName, x, y, deviceType);
        this.policyList = new ArrayList<>();
        this.defaultRule = Policy.Rule.DENY;
        this.routingTable = new ArrayList<>();
    }

    @Override
    void initPorts() {
        for (int i = 0; i < Values.DEVICE_FIREWALL_NUMBER_OF_PORTS; i++) {
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
            case GENERATE:
                return handleGenerateMessage(message, connectionList);
        }
        return new ArrayList<>();
    }

    private List<Message> handleNormalMessage(Message message, List<Connection> connectionList) {
        if (isProperRecipient(message)) {
            for (Port sourcePort : getPortList()) {
                if (!message.getCurrentDestinationPort().equals(sourcePort)) {
                    Connection connection = getConnectionByPort(sourcePort, connectionList);
                    if (connection != null) {
                        Port destinationPort = connection.getOtherPort(sourcePort);
                        for (Policy policy : policyList) {
                            String sourceNetworkAddress = policy.getSourceNetworkAddress();
                            String destinationNetworkAddress = policy.getDestinationNetworkAddress();
                            Set<Policy.Application> applicationSet = policy.getApplicationSet();

                            if (!(sourceNetworkAddress == null || sourceNetworkAddress.contains(Utils.getNetworkAddressFromIp(message.getSourceIpAddress())))) {
                                continue;
                            }
                            if (!(destinationNetworkAddress == null || destinationNetworkAddress.contains(Utils.getNetworkAddressFromIp(message.getDestinationIpAddress())))) {
                                continue;
                            }
                            if (!applicationSet.isEmpty() && !applicationSet.contains(message.getApplication())) {
                                continue;
                            }

                            return executePolicy(
                                    policy.getRule(),
                                    message,
                                    sourcePort,
                                    destinationPort,
                                    "by policy number : " + (policyList.indexOf(policy) + 1));
                        }
                        return executePolicy(
                                defaultRule,
                                message,
                                sourcePort,
                                destinationPort,
                                "by default rule: " + defaultRule);
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    private List<Message> handleTestMessage(Message message, List<Connection> connectionList) {
        if (!Utils.belongToTheSameNetwork(message.getCurrentDestinationPort().getIpAddress(), message.getSourceIpAddress())) {
            Device otherDevice = Manager.getInstance().getDeviceByMacAddress(message.getSourceMac());
            String errorMessage = "Wrong network configuration\n" +
                    getDeviceName() +
                    " and " +
                    otherDevice.getDeviceName() +
                    " should belong to the same network";
            Platform.runLater(() -> Manager.getInstance().logError(errorMessage));
            Manager.setTestNetworkSuccessful(false);
        } else if (message.getCurrentDestinationPort().getIpAddress().equals(message.getSourceIpAddress())) {
            Device otherDevice = Manager.getInstance().getDeviceByIPAddress(message.getSourceIpAddress());
            if (otherDevice == null) {
                otherDevice = Manager.getInstance().getDeviceByPortIpAddress(message.getSourceIpAddress());
            }
            String errorMessage = "Wrong network configuration\n" +
                    getDeviceName() +
                    " and " +
                    otherDevice.getDeviceName() +
                    " should not have the same ip address";
            Platform.runLater(() -> Manager.getInstance().logError(errorMessage));
            Manager.setTestNetworkSuccessful(false);
        }
        return new ArrayList<>();
    }

    private List<Message> handleGenerateMessage(Message message, List<Connection> connectionList) {
        routingTable.add(new Pair<>(message.getCurrentDestinationPort(), message.getSourceIpAddress()));
        return new ArrayList<>();
    }

    private List<Message> executePolicy(Policy.Rule rule, Message message, Port sourcePort, Port destinationPort, String decisionValue) {
        if (rule.equals(Policy.Rule.PERMIT)) {
            List<Message> messageList = new ArrayList<>();
            for (Pair<Port, String> pair : routingTable) {
                if (pair.getKey().equals(sourcePort)) {
                    Message msg = new Message(
                            message,
                            sourcePort,
                            destinationPort,
                            this,
                            pair.getValue(),
                            null,
                            decisionValue,
                            true
                    );

                    History lastHistory = message.getHistoryList().get(message.getHistoryList().size() - 1);
                    msg.updateLastHistoryPacketInfo(
                            lastHistory.getPacketInfoSourceIp(),
                            lastHistory.getPacketInfoDestinationIp()
                    );
                    String destinationMacAddress;
                    try {
                        destinationMacAddress = Manager.getInstance().getDeviceByPortIpAddress(pair.getValue()).getMacAddress();
                    }catch (Exception e) {
                        destinationMacAddress = Manager.getInstance().getDeviceByIPAddress(pair.getValue()).getMacAddress();
                    }
                    msg.updateLastHistoryFrameInfo(
                            getMacAddress(),
                            destinationMacAddress
                    );

                    messageList.add(msg);
                }
            }
            return messageList;
        } else {
            return new ArrayList<>();
        }
    }

    private Connection getConnectionByPort(Port port, List<Connection> connectionList) {
        for (Connection connection : connectionList) {
            if (connection.containsPort(port)) {
                return connection;
            }
        }
        return null;
    }

    public List<Policy> getPolicyList() {
        return new ArrayList<>(policyList);
    }

    public void setPolicyList(List<Policy> policyList) {
        this.policyList.clear();
        this.policyList.addAll(policyList);
    }

    public Policy.Rule getDefaultRule() {
        return defaultRule;
    }

    public void setDefaultRule(Policy.Rule defaultRule) {
        this.defaultRule = defaultRule;
    }

    public void clearRoutingTable() {
        this.routingTable.clear();
    }

    private boolean isProperRecipient(Message message) {
        return (!message.getCurrentIpDestinationAddress().isEmpty()
                && message.getCurrentDestinationPort().getIpAddress().contains(message.getCurrentIpDestinationAddress())
                && !Utils.belongToTheSameNetwork(message.getDestinationIpAddress(), message.getCurrentDestinationPort().getIpAddress()));
    }

    @Override
    public void onPoliciesListUpdated(List<Policy> policyList, Policy.Rule defaultRule) {
        this.policyList.clear();
        this.policyList.addAll(policyList);
        this.defaultRule = defaultRule;
    }
}
