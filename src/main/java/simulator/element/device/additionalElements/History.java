package simulator.element.device.additionalElements;

import simulator.Manager;
import simulator.element.Message;
import simulator.element.device.Device;
import simulator.element.device.EndDevice;

public class History {
    private final Message message;
    private final Device device;
    private final String decisionValue;
    private final Port fromPort;
    private final Port toPort;
    private PacketInfo packetInfo;
    private FrameInfo frameInfo;

    public History(Message message, Device device, String decisionValue, Port fromPort, Port toPort) {
        this.message = message;
        this.device = device;
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
        if (fromPort != null) {
            return fromPort.getPortName();
        } else {
            return "";
        }
    }

    public String getToPort() {
        if (toPort != null) {
            return toPort.getPortName();
        } else {
            return "";
        }
    }

    public String getCurrentSourceMacAddress() {
        if (message.getCurrentSourcePort() != null) {
            return Manager.getInstance().getDeviceByPort(message.getCurrentSourcePort()).getMacAddress();
        } else {
            return "";
        }
    }

    public String getCurrentDestinationMacAddress() {
        if (message.getCurrentDestinationPort() != null) {
            return Manager.getInstance().getDeviceByPort(message.getCurrentDestinationPort()).getMacAddress();
        } else {
            return "";
        }
    }

    public String getCurrentSourceIpAddress() {
        if (message.getCurrentSourcePort() != null) {
            if (message.getCurrentSourcePort().hasInterface()) {
                return message.getCurrentSourcePort().getIpAddress();
            } else if (Manager.getInstance().getDeviceByPort(message.getCurrentSourcePort()) instanceof EndDevice) {
                return ((EndDevice) Manager.getInstance().getDeviceByPort(message.getCurrentSourcePort())).getIpAddress();
            }
        }
        return "";
    }

    public String getCurrentDestinationIpAddress() {
        return message.getCurrentIpDestinationAddress();
    }

    public boolean isConfirmationMessage() {
        return message.isConfirmationMessage();
    }

    public void setPacketInfo(String sourceIpAddress, String destinationIpAddress) {
        this.packetInfo = new PacketInfo(sourceIpAddress, destinationIpAddress);
    }

    public void setFrameInfo(String sourceMacAddress, String destinationMacAddress) {
        this.frameInfo = new FrameInfo(sourceMacAddress, destinationMacAddress);
    }

    public String getPacketInfoSourceIp() {
        if (packetInfo != null) {
            return packetInfo.getSourceIpAddress();
        } else {
            return "";
        }
    }

    public String getPacketInfoDestinationIp() {
        if (packetInfo != null) {
            return packetInfo.getDestinationIpAddress();
        } else {
            return "";
        }
    }

    public String getFrameInfoSourceMac() {
        if (frameInfo != null) {
            return frameInfo.getSourceMacAddress();
        } else {
            return "";
        }

    }

    public String getFrameInfoDestinationMac() {
        if (frameInfo != null) {
            return frameInfo.getDestinationMacAddress();
        } else {
            return "";
        }
    }

    private class PacketInfo {
        private final String sourceIpAddress;
        private final String destinationIpAddress;

        PacketInfo(String sourceIpAddress, String destinationIpAddress) {
            this.sourceIpAddress = sourceIpAddress;
            this.destinationIpAddress = destinationIpAddress;
        }

        public String getSourceIpAddress() {
            return sourceIpAddress;
        }

        public String getDestinationIpAddress() {
            return destinationIpAddress;
        }
    }

    private class FrameInfo {
        private final String sourceMacAddress;
        private final String destinationMacAddress;

        FrameInfo(String sourceMacAddress, String destinationMacAddress) {
            this.sourceMacAddress = sourceMacAddress;
            this.destinationMacAddress = destinationMacAddress;
        }

        public String getSourceMacAddress() {
            return sourceMacAddress;
        }

        public String getDestinationMacAddress() {
            return  destinationMacAddress;
        }
    }
}
