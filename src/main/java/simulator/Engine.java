package simulator;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.Pair;
import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.device.additionalElements.Policy;
import simulator.element.device.additionalElements.Port;
import simulator.element.device.Device;
import simulator.element.device.EndDevice;
import simulator.element.device.Router;
import simulator.view.PortListDialog;
import util.Values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Engine implements PortListDialog.OnPortSelectedListener {
    private static final Engine INSTANCE = new Engine();
    private Controller controller;
    private GraphicsContext ctx;
    private final List<Device> deviceList;
    private final List<Connection> connectionList;
    private final List<Message> messageList;
    private Port portToConnect;
    private Device presentlyClickedDevice;
    private Device selectedDevice;
    private boolean runSimulation;
    private Pair<Integer, Integer> mousePosition;
    private Pair<Integer, Integer> connectMousePosition;
    private static Timeline timeline;

    private Engine() {
        this.deviceList = new ArrayList<>();
        this.connectionList = new ArrayList<>();
        this.messageList = new ArrayList<>();
        this.presentlyClickedDevice = null;
        this.selectedDevice = null;
        this.runSimulation = false;
        this.mousePosition = null;
    }

    public static Engine getInstance() {
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
        controller.hideConnectionList();
    }

    public void loadData(DataManager.Data data) {
        startNewProject();
        deviceList.addAll(data.getDeviceArrayList());
        connectionList.addAll(data.getConnectionArrayList());
    }

    public void saveData() {
        DataManager.Data data = new DataManager.Data();
        data.setDeviceArrayList(new ArrayList<>(deviceList));
        data.setConnectionArrayList(new ArrayList<>(connectionList));
        DataManager.save(data);
    }

    public void saveDataAs() {
        DataManager.Data data = new DataManager.Data();
        data.setDeviceArrayList(new ArrayList<>(deviceList));
        data.setConnectionArrayList(new ArrayList<>(connectionList));
        DataManager.saveAs(data);
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
            if (device instanceof EndDevice && ((EndDevice) device).getIpAddress().split("/")[0].equals(ipAddress)) {
                return (EndDevice) device;
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
            controller.hideConnectionList();
        }
    }

    public void removeSelectedElement() {
        removeConnections();
        deviceList.remove(selectedDevice);
        selectedDevice = null;
        controller.hideElementInfo();
        controller.hideConnectionList();
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
            } else if (device instanceof Router) {
                Router router = (Router) device;
                for (Port port : router.getPortList()) {
                    if (port.hasInterface() && port.getIpAddress().contains(address)) {
                        return port.getIpAddress();
                    }
                }
            }
        }
        return null;
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
                ctx.setStroke(connection.getColor());
                ctx.strokeLine(
                        device1.getX() + (Values.DEVICE_SIZE / 2.0),
                        device1.getY() + (Values.DEVICE_SIZE / 2.0),
                        device2.getX() + (Values.DEVICE_SIZE / 2.0),
                        device2.getY() + (Values.DEVICE_SIZE / 2.0));
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
        }
    }

    private void drawMessages() {
        for (Message message : messageList) {
            ctx.drawImage(message.getImage(), message.getX(), message.getY(), Values.MESSAGE_SIZE, Values.MESSAGE_SIZE);
        }
    }

    public void startSimulation(String sourceIPAddress, String destinationIPAddress) {
        if (runSimulation) return;
        Service<Void> service = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        prepareSimulation(sourceIPAddress, destinationIPAddress);
                        while (runSimulation) {
                            nextSimulationStep();
                            checkSimulationProgress();
                            Thread.sleep(Values.ENGINE_MILLISECONDS_PAUSE);
                        }
                        return null;
                    }
                };
            }

            private void prepareSimulation(String sourceIPAddress, String destinationIPAddress) {
                runSimulation = true;
                messageList.clear();
                EndDevice sourceDevice = getDeviceByIPAddress(sourceIPAddress);
                EndDevice destinationDevice = getDeviceByIPAddress(destinationIPAddress);
                if (sourceDevice != null) {
                    for (Port port : sourceDevice.getPortList()) {
                        String sourceIPAddressWithMask = null;
                        String destinationIPAddressWithMask = null;

                        for (Device device : deviceList) {
                            if (device instanceof EndDevice) {
                                if (((EndDevice) device).getIpAddress().contains(sourceIPAddress)) {
                                    sourceIPAddressWithMask = ((EndDevice) device).getIpAddress();
                                }
                                if (((EndDevice) device).getIpAddress().contains(destinationIPAddress)) {
                                    destinationIPAddressWithMask = ((EndDevice) device).getIpAddress();
                                }
                            }
                        }

                        //TODO: set application from "label"
                        messageList.add(new Message(
                                sourceIPAddressWithMask,
                                destinationIPAddressWithMask,
                                sourceDevice.getMacAddress(),
                                destinationDevice.getMacAddress(),
                                port,
                                getConnectionByPort(port).getOtherPort(port),
                                Policy.Application.UDP
                        ));
                    }
                } else {
                    runSimulation = false;
                }
            }

            private void nextSimulationStep() {
                List<Message> messagesToRemove = new ArrayList<>();
                List<Message> messagesToAdd = new ArrayList<>();
                for (Message message : messageList) {
                    if (message.getProgress() >= Values.MESSAGE_PROGRESS_MAX) {
                        //TODO: throws a lot of errors after simulation complete
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
    }
}