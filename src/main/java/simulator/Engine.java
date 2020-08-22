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
import simulator.element.Port;
import simulator.element.device.Device;
import simulator.element.Message;
import util.Values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Engine {
    private static final Engine INSTANCE = new Engine();
    private Controller controller;
    private GraphicsContext ctx;
    private final List<Device> deviceList;
    private final List<Connection> connectionList;
    private final List<Message> messageList;
    private Integer elementToConnect;
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
        this.elementToConnect = null;
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

    public Device getDeviceByMac(String macAddress) {
        for (Device device : deviceList) {
            if (device.getMacAddress().equals(macAddress)) {
                return device;
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
        controller.log(device.getDeviceType() + " added");
    }

    public void selectDevice(int x, int y) {
        if (elementToConnect != null) {
            connectTo(x, y);
        } else {
            for (Device device : deviceList) {
                if (x >= device.getX() && x <= device.getX() + Values.DEVICE_SIZE && y >= device.getY() && y <= device.getY() + Values.DEVICE_SIZE) {
                    if (presentlyClickedDevice != device) {
                        presentlyClickedDevice = device;
                        selectedDevice = presentlyClickedDevice;
                        controller.showDeviceInfo(selectedDevice);
                        controller.showConnectionList(selectedDevice, getDeviceConnections(selectedDevice));
                    }
                    break;
                }
            }
        }
    }

    public void deselectDevice() {
        presentlyClickedDevice = null;
    }

    public void dropSelection() {
        if (elementToConnect != null) {
            elementToConnect = null;
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
        if (elementToConnect != null) {
            elementToConnect = null;
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
        getDeviceById(connection.getFirstElementId()).removePort(connection.getPortPair().getKey());
        getDeviceById(connection.getSecondElementId()).removePort(connection.getPortPair().getValue());
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

    public void onConnectClicked() {
        if (selectedDevice != null) {
            elementToConnect = selectedDevice.getId();
        }
    }

    private void connectTo(int x, int y) {
        for (Device device : deviceList) {
            if (x >= device.getX() && x <= device.getX() + Values.DEVICE_SIZE && y >= device.getY() && y <= device.getY() + Values.DEVICE_SIZE) {
                if (!elementToConnect.equals(device.getId())) {
                    if (!connectionAlreadyExists(elementToConnect, device.getId())) {
                        connectionList.add(new Connection(getDeviceById(elementToConnect).getNewPort(), device.getNewPort()));
                    }
                    elementToConnect = null;
                }
            }
        }
        controller.showConnectionList(selectedDevice, getDeviceConnections(selectedDevice));
    }

    private boolean connectionAlreadyExists(Integer id1, int id2) {
        for (Connection connection : connectionList) {
            if (id1.equals(connection.getFirstElementId()) && id2 == connection.getSecondElementId()
                    || id2 == connection.getFirstElementId() && id1.equals(connection.getSecondElementId())) {
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

    public boolean isInConnectionMode() {
        return elementToConnect != null;
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
                    getDeviceById(elementToConnect).getX() + (Values.DEVICE_SIZE / 2.0),
                    getDeviceById(elementToConnect).getY() + (Values.DEVICE_SIZE / 2.0),
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

    public void startSimulation(String sourceMac, String destinationMac) {
        if (runSimulation) return;
        Service<Void> service = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        prepareSimulation(sourceMac, destinationMac);
                        while (runSimulation) {
                            nextSimulationStep();
                            checkSimulationProgress();
                            Thread.sleep(Values.ENGINE_MILLISECONDS_PAUSE);
                        }
                        return null;
                    }
                };
            }

            private void prepareSimulation(String sourceMac, String destinationMac) {
                runSimulation = true;
                messageList.clear();
                Device sourceDevice = getDeviceByMac(sourceMac);
                if (sourceDevice != null) {
                    for (Port port : sourceDevice.getPortList()) {
                        messageList.add(new Message(sourceMac, destinationMac, port, getConnectionByPort(port).getOtherPort(port)));
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