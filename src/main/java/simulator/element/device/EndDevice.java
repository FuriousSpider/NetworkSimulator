package simulator.element.device;

import javafx.application.Platform;
import simulator.Engine;
import simulator.element.Connection;
import simulator.element.device.additionalElements.Interface;
import simulator.element.Message;
import simulator.element.device.additionalElements.Port;
import simulator.view.IPTextField;
import util.Utils;

import java.util.ArrayList;
import java.util.List;

public class EndDevice extends Device implements IPTextField.OnSaveClickedListener {
    public static String fileName = "/endDevice.png";
    public static String deviceType = "End Device";
    private final Interface anInterface;

    public EndDevice(int x, int y) {
        super(fileName, x, y, deviceType);
        this.anInterface = new Interface();
    }

    @Override
    void initPorts() {
        getPortList().add(new Port(1));
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

    private List<Message> handleNormalMessage(Message message, List<Connection> connectionList) {
        //TODO: check if proper message received and handle message -> end simulation (or send confirmation)
        if (message.getDestinationIpAddress().equals(getIpAddress())) {
            Engine.getInstance().finishSimulation(message.getHistoryList());
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
        }
        return new ArrayList<>();
    }

    public String getIpAddress() {
        return anInterface.getAddress();
    }

    public void setIpAddress(String ipAddress) {
        anInterface.setAddress(ipAddress);
    }

    @Override
    public void onSaveClicked(String ipAddress) {
        anInterface.setAddress(ipAddress);
    }
}
