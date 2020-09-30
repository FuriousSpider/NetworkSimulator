package simulator;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;
import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.device.*;
import simulator.element.device.additionalElements.History;
import simulator.element.device.additionalElements.Policy;
import simulator.element.device.additionalElements.Port;
import simulator.view.PortListDialog;
import util.Utils;
import util.Values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Manager implements PortListDialog.OnPortSelectedListener {
    private static final Manager INSTANCE = new Manager();
    private Controller controller;
    private GraphicsContext ctx;
    private final List<Device> deviceList;
    private final List<Connection> connectionList;
    private final List<Message> messageList;
    private final List<Message> testMessageList;
    private Port portToConnect;
    private Device presentlyClickedDevice;
    private Device selectedDevice;
    private boolean runSimulation;
    private Pair<Integer, Integer> mousePosition;
    private Pair<Integer, Integer> connectMousePosition;
    private static Timeline timeline;
    private static boolean testNetworkSuccessful;

    private Manager() {
        this.deviceList = new ArrayList<>();
        this.connectionList = new ArrayList<>();
        this.messageList = new ArrayList<>();
        this.testMessageList = new ArrayList<>();
        this.presentlyClickedDevice = null;
        this.selectedDevice = null;
        this.runSimulation = false;
        this.mousePosition = null;
        testNetworkSuccessful = false;
    }

    public static Manager getInstance() {
        return INSTANCE;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setGraphicsContext(GraphicsContext context) {
        this.ctx = context;

        setCtxConfig();
        startEngine();
    }

    private void setCtxConfig() {
        ctx.setStroke(Color.BLACK);
        ctx.setLineWidth(2.0);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.setFont(new Font(Font.getFamilies().get(0), Values.FONT_SIZE));
    }

    public void startNewProject() {
        stopSimulation();
        connectMousePosition = null;
        mousePosition = null;
        selectedDevice = null;
        presentlyClickedDevice = null;
        messageList.clear();
        connectionList.clear();
        deviceList.clear();
        controller.hideElementInfo();
        Values.DEVICE_SIZE = Values.DEVICE_SIZE_DEFAULT;
        Values.MESSAGE_PROGRESS_STEP = Values.MESSAGE_PROGRESS_STEP_DEFAULT;
        Values.SHOW_DEVICE_NAME = Values.SHOW_DEVICE_NAME_DEFAULT;
        Values.SHOW_PORTS = Values.SHOW_PORTS_DEFAULT;
        EndDevice.nameCounter = 1;
        Hub.nameCounter = 1;
        Switch.nameCounter = 1;
        Router.nameCounter = 1;
        Firewall.nameCounter = 1;
    }

    public void loadData(DataManager.Data data) {
        stopSimulation();
        connectMousePosition = null;
        mousePosition = null;
        selectedDevice = null;
        presentlyClickedDevice = null;
        messageList.clear();
        connectionList.clear();
        deviceList.clear();
        controller.hideElementInfo();
        deviceList.addAll(data.getDeviceArrayList());
        connectionList.addAll(data.getConnectionArrayList());
    }

    public void saveData() {
        DataManager.Data data = new DataManager.Data();
        data.setDeviceArrayList(new ArrayList<>(deviceList));
        data.setConnectionArrayList(new ArrayList<>(connectionList));
        DataManager.save(data);
    }

    public Device getSelectedDevice() {
        return selectedDevice;
    }

    public Device getDeviceById(int id) {
        for (Device device : deviceList) {
            if (device.getId() == id) {
                return device;
            }
        }
        return null;
    }

    public Device getDeviceByPort(Port port) {
        for (Device device : deviceList) {
            if (device.getPortList().contains(port)) {
                return device;
            }
        }
        return null;
    }

    public Device getDeviceByPortId(int id) {
        for (Device device : deviceList) {
            for (Port port : device.getPortList()) {
                if (port.getId() == id) {
                    return device;
                }
            }
        }
        return null;
    }

    public EndDevice getDeviceByIPAddress(String ipAddress) {
        for (Device device : deviceList) {
            if (device instanceof EndDevice && ((EndDevice) device).getIpAddress().contains(ipAddress)) {
                return (EndDevice) device;
            }
        }
        return null;
    }

    public Device getDeviceByPortIpAddress(String ipAddress) {
        for (Device device : deviceList) {
            for (Port port : device.getPortList()) {
                if (port.hasInterface() && port.getIpAddress().contains(ipAddress)) {
                    return device;
                }
            }
        }
        return null;
    }

    public Device getDeviceByMacAddress(String macAddress) {
        for (Device device : deviceList) {
            if (device.getMacAddress().equals(macAddress)) {
                return device;
            }
        }
        return null;
    }

    public Device getDeviceByPosition(int x, int y) {
        for (Device device : deviceList) {
            if (x >= device.getX() && x <= device.getX() + Values.DEVICE_SIZE && y >= device.getY() && y <= device.getY() + Values.DEVICE_SIZE) {
                return device;
            }
        }
        return null;
    }

    public Port getPortById(int id) {
        for (Device device : deviceList) {
            for (Port port : device.getPortList()) {
                if (port.getId() == id) {
                    return port;
                }
            }
        }
        return null;
    }

    public Connection getConnectionByPort(Port port) {
        for (Connection connection : connectionList) {
            if (connection.containsPort(port)) {
                return connection;
            }
        }
        return null;
    }

    public void addDevice(Device device) {
        this.deviceList.add(0, device);
        selectDevice(device.getX(), device.getY());
    }

    public void selectDevice(int x, int y) {
        Device device = getDeviceByPosition(x, y);
        if (portToConnect != null) {
            if (device != null && !device.hasPort(portToConnect) && device.hasEmptyPort()) {
                controller.showDevicePortListDialog(device, this);
            } else {
                if (device != null && device.hasPort(portToConnect)) {
                    logError(Values.ERROR_CANNOT_CONNECT_TO_SAME_DEVICE);
                } else if (device != null && !device.hasEmptyPort()) {
                    logError(device.getDeviceType() + Values.ERROR_NO_FREE_PORT_AVAILABLE);
                }
            }
        } else {
            if (device != null && presentlyClickedDevice != device) {
                presentlyClickedDevice = device;
                selectedDevice = presentlyClickedDevice;
                controller.showDeviceInfo(selectedDevice);
                controller.showConnectionList(selectedDevice, getDeviceConnections(selectedDevice));
            }
        }
    }

    public void deselectDevice() {
        presentlyClickedDevice = null;
    }

    public void dropSelection() {
        if (portToConnect != null) {
            portToConnect = null;
        } else {
            presentlyClickedDevice = null;
            selectedDevice = null;
            controller.hideElementInfo();
        }
    }

    public void removeSelectedElement() {
        removeConnections();
        deviceList.remove(selectedDevice);
        selectedDevice = null;
        controller.hideElementInfo();
        if (portToConnect != null) {
            portToConnect = null;
        }
    }

    public void removeConnection(int id) {
        for (Connection connection : connectionList) {
            if (connection.getId() == id) {
                connectionList.remove(connection);
                removeConnectionPorts(connection);
                break;
            }
        }
        controller.showConnectionList(selectedDevice, getDeviceConnections(selectedDevice));
    }

    private void removeConnections() {
        if (selectedDevice == null) return;
        List<Connection> connectionsToRemove = new ArrayList<>();
        for (Port port : selectedDevice.getPortList()) {
            for (Connection connection : connectionList) {
                if (connection.containsPort(port)) {
                    connectionsToRemove.add(connection);
                }
            }
        }
        if (!connectionsToRemove.isEmpty()) {
            for (Connection connection : connectionsToRemove) {
                removeConnectionPorts(connection);
            }
            connectionList.removeAll(connectionsToRemove);
        }
    }

    private void removeConnectionPorts(Connection connection) {
        Pair<Integer, Integer> pair = connection.getPortPair();
        getPortById(pair.getKey()).releasePort();
        getPortById(pair.getValue()).releasePort();
    }

    public void moveElement(int x, int y) {
        if (presentlyClickedDevice != null && mousePosition != null) {
            presentlyClickedDevice.setX(presentlyClickedDevice.getX() + (x - mousePosition.getKey()));
            presentlyClickedDevice.setY(presentlyClickedDevice.getY() + (y - mousePosition.getValue()));
            mousePosition = new Pair<>(x, y);
        }
    }

    public void onMousePressed(int x, int y) {
        mousePosition = new Pair<>(x, y);
    }

    public void onMouseReleased() {
        mousePosition = null;
    }

    public void moveAll(int x, int y) {
        if (mousePosition != null) {
            for (Device device : deviceList) {
                device.setX(device.getX() + (x - mousePosition.getKey()));
                device.setY(device.getY() + (y - mousePosition.getValue()));
            }
            mousePosition = new Pair<>(x, y);
        }
    }

    public void changeConnectMousePosition(int x, int y) {
        this.connectMousePosition = new Pair<>(x, y);
    }

    public void onConnectClicked(int portId) {
        if (selectedDevice != null) {
            portToConnect = getPortById(portId);
        }
    }

    private void connectTo(int portId) {
        if (!connectionAlreadyExists(portToConnect.getId(), portId)) {
            connectionList.add(new Connection(portToConnect.getId(), portId));
            portToConnect.reservePort();
            getPortById(portId).reservePort();
            portToConnect = null;
            controller.showConnectionList(selectedDevice, getDeviceConnections(selectedDevice));
        }
    }

    @Override
    public void onPortSelected(int portId) {
        connectTo(portId);
    }

    private boolean connectionAlreadyExists(int portId1, int portId2) {
        for (Connection connection : connectionList) {
            if ((portId1 == connection.getFirstElementId() && portId2 == connection.getSecondElementId())
                    || (portId2 == connection.getFirstElementId() && portId1 == connection.getSecondElementId())) {
                return true;
            }
        }
        return false;
    }

    private List<Connection> getDeviceConnections(Device device) {
        List<Connection> selectedElementConnections = new ArrayList<>();
        for (Port port : device.getPortList()) {
            for (Connection connection : connectionList) {
                if (connection.containsPort(port)) {
                    selectedElementConnections.add(connection);
                }
            }
        }
        return selectedElementConnections;
    }

    public void copyToClipboard(String text) {
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(text);
        Clipboard.getSystemClipboard().setContent(clipboardContent);
        controller.log("Value: \"" + text + "\" has been copied to the clipboard");
    }

    public String getIpWithMaskByIp(String address) {
        for (Device device : deviceList) {
            if (device instanceof EndDevice && ((EndDevice) device).getIpAddress().contains(address)) {
                return ((EndDevice) device).getIpAddress();
            } else if (device instanceof Router || device instanceof Firewall) {
                for (Port port : device.getPortList()) {
                    if (port.hasInterface() && port.getIpAddress().contains(address)) {
                        return port.getIpAddress();
                    }
                }
            }
        }
        return null;
    }

    public void showConnectionList() {
        controller.showConnectionList(selectedDevice, getDeviceConnections(selectedDevice));
    }

    public void log(String message) {
        controller.log(message);
    }

    public void logError(String errorMessage) {
        controller.logError(errorMessage);
    }

    public boolean isInConnectionMode() {
        return portToConnect != null;
    }

    public static void setTestNetworkSuccessful(boolean successful) {
        testNetworkSuccessful = successful;
    }

    public static void stopEngine() {
        timeline.stop();
    }

    private void startEngine() {
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), actionEvent -> {
                    clearScreen();
                    drawConnections();
                    drawElements();
                    drawMessages();
                }),
                new KeyFrame(Duration.millis(Values.ENGINE_MILLISECONDS_PAUSE))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void clearScreen() {
        ctx.clearRect(0, 0, ctx.getCanvas().getWidth(), ctx.getCanvas().getHeight());
    }

    private void drawConnections() {
        List<Device> reversedList = new ArrayList<>(deviceList);
        Collections.reverse(reversedList);
        for (Connection connection : connectionList) {
            Device device1 = getDeviceById(connection.getFirstElementId());
            Device device2 = getDeviceById(connection.getSecondElementId());
            if (device1 != null && device2 != null) {
                ctx.setLineWidth(3.0);
                ctx.setStroke(connection.getColor());
                double x1 = device1.getX() + (Values.DEVICE_SIZE / 2.0);
                double y1 = device1.getY() + (Values.DEVICE_SIZE / 2.0);
                double x2 = device2.getX() + (Values.DEVICE_SIZE / 2.0);
                double y2 = device2.getY() + (Values.DEVICE_SIZE / 2.0);
                ctx.strokeLine(
                        x1,
                        y1,
                        x2,
                        y2);

                if (Values.SHOW_PORTS) {
                    Pair<Integer, Integer> portPair = connection.getPortPair();
                    Port port1 = getPortById(portPair.getKey());
                    Port port2 = getPortById(portPair.getValue());

                    if (!device1.hasPort(port1)) {
                        port1 = getPortById(portPair.getValue());
                        port2 = getPortById(portPair.getKey());
                    }

                    double dx = Math.abs(x1 - x2);
                    double dy = Math.abs(y1 - y2);
                    double port1X, port1Y, port2X, port2Y;
                    double multiplier = 0.3;
                    double ipAddressTopPadding = 20.0;
                    if (x1 < x2) {
                        port1X = Math.min(x1 + dx * multiplier, x1 + Values.DEVICE_SIZE * 2);
                        port2X = Math.max(x2 - dx * multiplier, x2 - Values.DEVICE_SIZE * 2);
                    } else {
                        port1X = Math.max(x1 - dx * multiplier, x1 - Values.DEVICE_SIZE * 2);
                        port2X = Math.min(x2 + dx * multiplier, x2 + Values.DEVICE_SIZE * 2);
                    }
                    if (y1 < y2) {
                        port1Y = Math.min(y1 + dy * multiplier, y1 + Values.DEVICE_SIZE * 2);
                        port2Y = Math.max(y2 - dy * multiplier, y2 - Values.DEVICE_SIZE * 2);
                    } else {
                        port1Y = Math.max(y1 - dy * multiplier, y1 - Values.DEVICE_SIZE * 2);
                        port2Y = Math.min(y2 + dy * multiplier, y2 + Values.DEVICE_SIZE * 2);
                    }
                    ctx.fillText(port1.getPortName(), port1X, port1Y);
                    if (port1.hasInterface()) {
                        ctx.fillText(port1.getIpAddress(), port1X, port1Y + ipAddressTopPadding);
                    }
                    ctx.fillText(port2.getPortName(), port2X, port2Y);
                    if (port2.hasInterface()) {
                        ctx.fillText(port2.getIpAddress(), port2X, port2Y + ipAddressTopPadding);
                    }
                    if (port1.hasVLan() && port1.isInTrunkMode()) {
                        ctx.fillText("TRUNK", port1X, port1Y + ipAddressTopPadding);
                    } else if (port1.hasVLan()) {
                        ctx.fillText("vlan " + port1.getVLanId(), port1X, port1Y + ipAddressTopPadding);
                    }
                    if (port2.hasVLan() && port2.isInTrunkMode()) {
                        ctx.fillText("TRUNK", port2X, port2Y + ipAddressTopPadding);
                    } else if (port2.hasVLan()) {
                        ctx.fillText("vlan " + port2.getVLanId(), port2X, port2Y + ipAddressTopPadding);
                    }
                }
            }
        }
        if (isInConnectionMode() && connectMousePosition != null) {
            ctx.setStroke(Color.BLACK);
            ctx.strokeLine(
                    getDeviceByPort(portToConnect).getX() + (Values.DEVICE_SIZE / 2.0),
                    getDeviceByPort(portToConnect).getY() + (Values.DEVICE_SIZE / 2.0),
                    connectMousePosition.getKey(),
                    connectMousePosition.getValue());
        }
    }

    private void drawElements() {
        ctx.setLineWidth(2.0);
        ctx.setStroke(Color.BLACK);
        List<Device> reversedList = new ArrayList<>(deviceList);
        Collections.reverse(reversedList);
        for (Device element : reversedList) {
            if (element == selectedDevice) {
                ctx.strokeRect(
                        element.getX() - Values.DEVICE_STROKE,
                        element.getY() - Values.DEVICE_STROKE,
                        Values.DEVICE_SIZE + Values.DEVICE_STROKE * 2,
                        Values.DEVICE_SIZE + Values.DEVICE_STROKE * 2
                );
            }
            ctx.drawImage(element.getImage(), element.getX(), element.getY(), Values.DEVICE_SIZE, Values.DEVICE_SIZE);
            if (Values.SHOW_DEVICE_NAME) {
                ctx.fillText(element.getDeviceName(), element.getX() + Values.DEVICE_SIZE / 2.0, element.getY() + Values.DEVICE_SIZE * 1.2);
                if (element instanceof EndDevice) {
                    ctx.fillText(((EndDevice) element).getIpAddress(), element.getX() + Values.DEVICE_SIZE / 2.0, element.getY() + Values.DEVICE_SIZE * 1.2 + 20.0);
                }
            }
        }
    }

    private void drawMessages() {
        List<Message> tempMessageList = new ArrayList<>(messageList);
        for (Message message : tempMessageList) {
            ctx.drawImage(message.getImage(), message.getX(), message.getY(), Values.MESSAGE_SIZE, Values.MESSAGE_SIZE);
        }
    }

    public void startSimulation(String sourceIPAddress, String destinationIPAddress, Policy.Application application) {
        if (runSimulation) return;
        Service<Void> service = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        testNetwork();
                        generateFirewallRoutingTable();
                        if (testNetworkSuccessful) {
                            Platform.runLater(() -> Manager.getInstance().log("Network configured properly"));
                            prepareSimulation(sourceIPAddress, destinationIPAddress, application);
                            if (runSimulation) {
                                controller.lockSimulationButton();
                            }
                            while (runSimulation) {
                                nextSimulationStep();
                                checkSimulationProgress();
                                Thread.sleep(Values.ENGINE_MILLISECONDS_PAUSE);
                            }
                            controller.unlockSimulationButton();
                        }
                        return null;
                    }
                };
            }

            private void testNetwork() {
                try {
                    testNetworkSuccessful = true;
                    testMessageList.clear();
                    for (Device device : deviceList) {
                        if (device instanceof EndDevice) {
                            EndDevice endDevice = (EndDevice) device;
                            Port sourcePort = device.getPortList().get(0);
                            if (sourcePort.isPortTaken()) {
                                testMessageList.add(new Message(
                                        endDevice.getIpAddress(),
                                        endDevice.getIpAddress(),
                                        endDevice.getMacAddress(),
                                        endDevice.getMacAddress(),
                                        sourcePort,
                                        getConnectionByPort(sourcePort).getOtherPort(sourcePort),
                                        "",
                                        0,
                                        Policy.Application.UDP,
                                        false,
                                        Message.Type.TEST,
                                        null
                                ));
                            }
                        } else if (device instanceof Router || device instanceof Firewall) {
                            for (Port port : device.getPortList()) {
                                if (port.isPortTaken()) {
                                    testMessageList.add(new Message(
                                            port.getIpAddress(),
                                            port.getIpAddress(),
                                            device.getMacAddress(),
                                            device.getMacAddress(),
                                            port,
                                            getConnectionByPort(port).getOtherPort(port),
                                            "",
                                            0,
                                            Policy.Application.UDP,
                                            false,
                                            Message.Type.TEST,
                                            null
                                    ));
                                }
                            }
                        }
                    }
                    while (!testMessageList.isEmpty()) {
                        nextTestStep();
                        if (!testNetworkSuccessful) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void generateFirewallRoutingTable() {
                boolean startGeneration = false;
                for (Device device : deviceList) {
                    if (device instanceof Firewall) {
                        ((Firewall) device).clearRoutingTable();
                        startGeneration = true;
                    }
                }

                if (startGeneration) {
                    try {
                        testMessageList.clear();
                        for (Device device : deviceList) {
                            if (device instanceof EndDevice) {
                                EndDevice endDevice = (EndDevice) device;
                                Port sourcePort = device.getPortList().get(0);
                                if (sourcePort.isPortTaken()) {
                                    testMessageList.add(new Message(
                                            endDevice.getIpAddress(),
                                            endDevice.getIpAddress(),
                                            endDevice.getMacAddress(),
                                            endDevice.getMacAddress(),
                                            sourcePort,
                                            getConnectionByPort(sourcePort).getOtherPort(sourcePort),
                                            "",
                                            0,
                                            Policy.Application.UDP,
                                            false,
                                            Message.Type.GENERATE,
                                            null
                                    ));
                                }
                            } else if (device instanceof Router || device instanceof Firewall) {
                                for (Port port : device.getPortList()) {
                                    if (port.isPortTaken()) {
                                        testMessageList.add(new Message(
                                                port.getIpAddress(),
                                                port.getIpAddress(),
                                                device.getMacAddress(),
                                                device.getMacAddress(),
                                                port,
                                                getConnectionByPort(port).getOtherPort(port),
                                                "",
                                                0,
                                                Policy.Application.UDP,
                                                false,
                                                Message.Type.GENERATE,
                                                null
                                        ));
                                    }
                                }
                            }
                        }
                        while (!testMessageList.isEmpty()) {
                            nextTestStep();
                            nextSimulationStep();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            private void prepareSimulation(String sourceIPAddress, String destinationIPAddress, Policy.Application application) {
                runSimulation = true;
                messageList.clear();
                EndDevice sourceDevice = getDeviceByIPAddress(sourceIPAddress);
                EndDevice destinationDevice = getDeviceByIPAddress(destinationIPAddress);
                if (sourceDevice != null && destinationDevice != null && application != null) {
                    for (Port port : sourceDevice.getPortList()) {
                        String sourceIPAddressWithMask = null;
                        String destinationIPAddressWithMask = null;
                        String currentDestinationIpAddress = "";

                        for (Device device : deviceList) {
                            if (device instanceof EndDevice) {
                                if (((EndDevice) device).getIpAddress().contains(sourceIPAddress)) {
                                    sourceIPAddressWithMask = ((EndDevice) device).getIpAddress();
                                    currentDestinationIpAddress = ((EndDevice) device).getGateway();
                                }
                                if (((EndDevice) device).getIpAddress().contains(destinationIPAddress)) {
                                    destinationIPAddressWithMask = ((EndDevice) device).getIpAddress();
                                }
                            }
                        }

                        Message message = new Message(
                                sourceIPAddressWithMask,
                                destinationIPAddressWithMask,
                                sourceDevice.getMacAddress(),
                                destinationDevice.getMacAddress(),
                                port,
                                getConnectionByPort(port).getOtherPort(port),
                                currentDestinationIpAddress,
                                null,
                                application,
                                false,
                                Message.Type.NORMAL,
                                null
                        );

                        History history = new History(message, sourceDevice, "send message", null, port);
                        history.setPacketInfo(sourceIPAddress, destinationIPAddress);
                        if (Utils.belongToTheSameNetwork(sourceIPAddressWithMask, destinationIPAddressWithMask)) {
                            history.setFrameInfo(sourceDevice.getMacAddress(), destinationDevice.getMacAddress());
                        } else {
                            history.setFrameInfo(sourceDevice.getMacAddress(), getDeviceByPortIpAddress(sourceDevice.getGateway()).getMacAddress());
                        }
                        message.addHistory(history);

                        messageList.add(message);
                    }
                } else {
                    if (sourceDevice == null) {
                        Platform.runLater(() -> logError("Simulation: Invalid Source IP Address - no device found"));
                    }
                    if (destinationDevice == null) {
                        Platform.runLater(() -> logError("Simulation: Invalid Destination IP Address - no device found"));
                    }
                    if (application == null) {
                        Platform.runLater(() -> logError("Simulation: You have to select an application"));
                    }
                    runSimulation = false;
                }
            }

            private void nextTestStep() {
                List<Message> messagesToRemove = new ArrayList<>();
                List<Message> messagesToAdd = new ArrayList<>();
                for (Message message : testMessageList) {
                    if (message.getProgress() >= Values.MESSAGE_PROGRESS_MAX) {
                        messagesToAdd.addAll(getDeviceByPort(message.getCurrentDestinationPort()).handleMessage(message, getDeviceConnections(getDeviceByPort(message.getCurrentDestinationPort()))));
                        messagesToRemove.add(message);
                    } else {
                        message.nextStep();
                    }
                }

                if (!messagesToAdd.isEmpty()) {
                    testMessageList.addAll(messagesToAdd);
                }
                if (!messagesToRemove.isEmpty()) {
                    testMessageList.removeAll(messagesToRemove);
                }
            }

            private void nextSimulationStep() {
                List<Message> messagesToRemove = new ArrayList<>();
                List<Message> messagesToAdd = new ArrayList<>();
                for (Message message : messageList) {
                    if (message.getProgress() >= Values.MESSAGE_PROGRESS_MAX) {
                        messagesToAdd.addAll(getDeviceByPort(message.getCurrentDestinationPort()).handleMessage(message, getDeviceConnections(getDeviceByPort(message.getCurrentDestinationPort()))));
                        messagesToRemove.add(message);
                    } else {
                        message.nextStep();
                    }
                }

                if (!messagesToAdd.isEmpty()) {
                    messageList.addAll(messagesToAdd);
                }
                if (!messagesToRemove.isEmpty()) {
                    messageList.removeAll(messagesToRemove);
                }
            }

            private void checkSimulationProgress() {
                if (messageList.isEmpty()) {
                    runSimulation = false;
                }
            }
        };
        service.start();
    }

    public void stopSimulation() {
        this.runSimulation = false;
        this.messageList.clear();
        controller.unlockSimulationButton();
    }

    public void finishSimulation(List<History> historyList) {
        stopSimulation();
        Platform.runLater(() -> controller.showResultDiagram(historyList));
        Platform.runLater(() -> controller.unlockSimulationButton());
    }
}