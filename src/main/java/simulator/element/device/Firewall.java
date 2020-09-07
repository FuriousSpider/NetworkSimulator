package simulator.element.device;

import javafx.application.Platform;
import simulator.Engine;
import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.device.additionalElements.Policy;
import simulator.element.device.additionalElements.Port;
import simulator.view.FirewallPoliciesView;
import util.Utils;
import util.Values;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Firewall extends Device implements FirewallPoliciesView.OnPoliciesListUpdatedListener {
    public static String fileName = "/firewall.png";
    public static String deviceType = "Firewall";
    private final List<Policy> policyList;
    private Policy.Rule defaultRule;

    public Firewall(int x, int y) {
        super(fileName, x, y, deviceType);
        this.policyList = new ArrayList<>();
        this.defaultRule = Policy.Rule.DENY;
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
        setDeviceName(deviceType);
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

    //TODO: add
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

                            //TODO: check if works properly
                            if (!(sourceNetworkAddress == null || !sourceNetworkAddress.equals(Utils.getNetworkAddressFromIp(message.getSourceIpAddress())))) {
                                continue;
                            }
                            if (!(destinationNetworkAddress == null || !destinationNetworkAddress.equals(Utils.getNetworkAddressFromIp(message.getDestinationIpAddress())))) {
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
                                    "by policy number :" + policyList.indexOf(policy) + 1);
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
            Device otherDevice = Engine.getInstance().getDeviceByIPAddress(message.getSourceIpAddress());
            if (otherDevice == null) {
                otherDevice = Engine.getInstance().getDeviceByPortIpAddress(message.getSourceIpAddress());
            }
            String errorMessage = "Wrong network configuration\n" +
                    getDeviceName() +
                    " and " +
                    otherDevice.getDeviceName() +
                    " should belong to the same network";
            Platform.runLater(() -> Engine.getInstance().logError(errorMessage));
            Engine.setTestNetworkSuccessful(false);
        } else if (message.getCurrentDestinationPort().getIpAddress().equals(message.getSourceIpAddress())) {
            Device otherDevice = Engine.getInstance().getDeviceByIPAddress(message.getSourceIpAddress());
            if (otherDevice == null) {
                otherDevice = Engine.getInstance().getDeviceByPortIpAddress(message.getSourceIpAddress());
            }
            String errorMessage = "Wrong network configuration\n" +
                    getDeviceName() +
                    " and " +
                    otherDevice.getDeviceName() +
                    " should not have the same ip address";
            Platform.runLater(() -> Engine.getInstance().logError(errorMessage));
            Engine.setTestNetworkSuccessful(false);
        }
        return new ArrayList<>();
    }

    private List<Message> executePolicy(Policy.Rule rule, Message message, Port sourcePort, Port destinationPort, String decisionValue) {
        if (rule.equals(Policy.Rule.PERMIT)) {
            List<Message> messageList = new ArrayList<>();
            messageList.add(new Message(
                    message,
                    sourcePort,
                    destinationPort,
                    this,
                    "0.0.0.0",
                    decisionValue));
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

    private boolean isProperRecipient(Message message) {
        return (!message.getCurrentIpDestinationAddress().isEmpty()
                && message.getCurrentDestinationPort().getIpAddress().contains(message.getCurrentIpDestinationAddress())
                && !Utils.belongToTheSameNetwork(message.getDestinationIpAddress(), message.getCurrentDestinationPort().getIpAddress())
                || message.getCurrentIpDestinationAddress().equals("0.0.0.0"));
    }

    @Override
    public void onPoliciesListUpdated(List<Policy> policyList, Policy.Rule defaultRule) {
        this.policyList.clear();
        this.policyList.addAll(policyList);
        this.defaultRule = defaultRule;
    }
}
