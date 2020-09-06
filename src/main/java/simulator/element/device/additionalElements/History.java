package simulator.element.device.additionalElements;

import simulator.element.Message;
import simulator.element.device.Device;

public class History {
    private final Message message;
    private final Device device;
    private final Decision decision;
    private final String decisionValue;
    private final Port fromPort;
    private final Port toPort;

    public History(Message message, Device device, Decision decision, String decisionValue, Port fromPort, Port toPort) {
        this.message = message;
        this.device = device;
        this.decision = decision;
        this.decisionValue = decisionValue;
        this.fromPort = fromPort;
        this.toPort = toPort;
    }

    public String getDeviceName() {
        return device.getDeviceName();
    }

    public String getDeviceType() {
        return device.getDeviceType();
    }

    public String getDecisionValue() {
        return decisionValue;
    }

    public String getFromPort() {
        return fromPort.getPortName();
    }

    public String getToPort() {
        return toPort.getPortName();
    }

    public enum Decision {
        HUB_FORWARD,
        SWITCH_FORWARD_ASSOCIATION_TABLE,
        SWITCH_FORWARD,
        ROUTER_FORWARD_ROUTING_TABLE,
        ROUTER_FORWARD,
        FIREWALL_FORWARD_POLICY,
        FIREWALL_FORWARD_DEFAULT_RULE
    }
}
