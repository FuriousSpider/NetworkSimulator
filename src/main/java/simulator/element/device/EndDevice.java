package simulator.element.device;

import javafx.application.Platform;
import simulator.Engine;
import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.device.additionalElements.History;
import simulator.element.device.additionalElements.Interface;
import simulator.element.device.additionalElements.Policy;
import simulator.element.device.additionalElements.Port;
import simulator.view.DefaultGatewayView;
import simulator.view.IPTextField;
import util.Utils;

import java.util.ArrayList;
import java.util.List;

public class EndDevice extends Device implements IPTextField.OnSaveClickedListener, DefaultGatewayView.OnDefaultGatewayChangeListener {
    public static String fileName = "/endDevice.jpg";
    public static String deviceType = "End Device";
    private final Interface anInterface;
    private final Interface gateway;
    public static int nameCounter = 1;

    public EndDevice(int x, int y) {
        super(fileName, x, y, deviceType);
        this.anInterface = new Interface();
        this.gateway = new Interface();
        this.gateway.setAddress("");
    }

    @Override
    void initPorts() {
        getPortList().add(new Port(1));
    }

    @Override
    void initName() {
        setDeviceName("PC" + nameCounter++);
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
        if (message.getDestinationIpAddress().equals(getIpAddress()) && message.getHistoryList().get(message.getHistoryList().size() - 1).getFrameInfoDestinationMac().equals(getMacAddress())) {
            if (message.getApplication().equals(Policy.Application.TCP) && !message.isConfirmationMessage()) {
                List<Message> messageList = new ArrayList<>();
                Message msg = new Message(message.getDestinationIpAddress(),
                        message.getSourceIpAddress(),
                        message.getDestinationMac(),
                        message.getSourceMac(),
                        this.getPortList().get(0),
                        connectionList.get(0).getOtherPort(getPortList().get(0)),
                        this.getGateway(),
                        message.getVLanId(),
                        message.getApplication(),
                        true,
                        Message.Type.NORMAL,
                        message.getHistoryList());

                History history = new History(
                        msg,
                        this,
                        "send confirmation",
                        message.getCurrentDestinationPort(),
                        message.getCurrentSourcePort());

                history.setPacketInfo(message.getDestinationIpAddress(), message.getSourceIpAddress());
                if (Utils.belongToTheSameNetwork(message.getDestinationIpAddress(), message.getSourceIpAddress())) {
                    history.setFrameInfo(
                            Engine.getInstance().getDeviceByIPAddress(message.getDestinationIpAddress()).getMacAddress(),
                            Engine.getInstance().getDeviceByIPAddress(message.getSourceIpAddress()).getMacAddress()
                    );
                } else {
                    history.setFrameInfo(
                            Engine.getInstance().getDeviceByIPAddress(message.getDestinationIpAddress()).getMacAddress(),
                            Engine.getInstance().getDeviceByPortIpAddress(getGateway()).getMacAddress()
                    );
                }
                msg.addHistory(history);
                messageList.add(msg);

                return messageList;
            } else if (message.getApplication().equals(Policy.Application.TCP) && message.isConfirmationMessage()) {
                Message msg = new Message(message.getDestinationIpAddress(),
                        message.getSourceIpAddress(),
                        null,
                        message.getSourceMac(),
                        this.getPortList().get(0),
                        null,
                        this.getGateway(),
                        message.getVLanId(),
                        message.getApplication(),
                        true,
                        Message.Type.NORMAL,
                        message.getHistoryList());

                History lastHistory = message.getHistoryList().get(message.getHistoryList().size() - 1);
                History history = new History(
                        msg,
                        this,
                        "receive confirmation",
                        message.getCurrentDestinationPort(),
                        null);
                history.setPacketInfo(lastHistory.getPacketInfoSourceIp(), lastHistory.getPacketInfoDestinationIp());
                history.setFrameInfo(lastHistory.getFrameInfoSourceMac(), lastHistory.getFrameInfoDestinationMac());

                message.addHistory(history);

                Engine.getInstance().finishSimulation(message.getHistoryList());
            } else if (message.getApplication().equals(Policy.Application.UDP)) {
                Message msg = new Message(message.getDestinationIpAddress(),
                        message.getSourceIpAddress(),
                        null,
                        message.getSourceMac(),
                        this.getPortList().get(0),
                        null,
                        this.getGateway(),
                        message.getVLanId(),
                        message.getApplication(),
                        false,
                        Message.Type.NORMAL,
                        message.getHistoryList());

                History lastHistory = message.getHistoryList().get(message.getHistoryList().size() - 1);
                History history = new History(
                        msg,
                        this,
                        "receive message",
                        message.getCurrentDestinationPort(),
                        null);
                history.setPacketInfo(lastHistory.getPacketInfoSourceIp(), lastHistory.getPacketInfoDestinationIp());
                history.setFrameInfo(lastHistory.getFrameInfoSourceMac(), lastHistory.getFrameInfoDestinationMac());

                message.addHistory(history);

                Engine.getInstance().finishSimulation(message.getHistoryList());
            }
        }
        return new ArrayList<>();
    }

    private List<Message> handleTestMessage(Message message, List<Connection> connectionList) {
        if (!Utils.belongToTheSameNetwork(getIpAddress(), message.getSourceIpAddress())) {
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
        } else if (getIpAddress().equals(message.getSourceIpAddress())) {
            Device otherDevice = Engine.getInstance().getDeviceByMacAddress(message.getSourceMac());
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

    public String getIpAddress() {
        return anInterface.getAddress();
    }

    public void setIpAddress(String ipAddress) {
        anInterface.setAddress(ipAddress);
    }

    public String getGateway() {
        return gateway.getAddress();
    }

    public void setGateway(String gatewayAddress) {
        gateway.setAddress(gatewayAddress);
    }

    @Override
    public void onSaveClicked(String ipAddress) {
        anInterface.setAddress(ipAddress);
    }

    @Override
    public void onDefaultGatewayChanged(String defaultGateway) {
        gateway.setAddress(defaultGateway);
    }
}
