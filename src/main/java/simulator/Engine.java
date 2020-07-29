package simulator;

import javafx.concurrent.Task;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import simulator.element.Connection;
import simulator.element.Port;
import simulator.element.device.Element;
import simulator.element.Message;
import util.Values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Engine {
    private static final Engine INSTANCE = new Engine();
    private Controller controller;
    private GraphicsContext ctx;
    private final List<Element> elementList;
    private final List<Connection> connectionList;
    private final List<Message> messageList;
    private Integer elementToConnect;
    private Element presentlyClickedElement;
    private Element selectedElement;
    private boolean shouldUpdate;
    private static boolean closeTask;
    private boolean runSimulation;
    private Pair<Integer, Integer> mousePosition;

    private Engine() {
        this.elementList = new ArrayList<>();
        this.connectionList = new ArrayList<>();
        this.messageList = new ArrayList<>();
        this.elementToConnect = null;
        this.presentlyClickedElement = null;
        this.selectedElement = null;
        this.shouldUpdate = false;
        closeTask = false;
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

    public Element getElementById(int id) {
        for (Element element : elementList) {
            if (element.getId() == id) {
                return element;
            }
        }
        return null;
    }

    public void addDevice(Element element) {
        this.elementList.add(0, element);
        shouldUpdate = true;
    }

    public void selectElement(int x, int y) {
        if (elementToConnect != null) {
            connectTo(x, y);
        } else {
            for (Element element : elementList) {
                if (x >= element.getX() && x <= element.getX() + Values.ELEMENT_SIZE && y >= element.getY() && y <= element.getY() + Values.ELEMENT_SIZE) {
                    if (presentlyClickedElement != element) {
                        presentlyClickedElement = element;
                        selectedElement = presentlyClickedElement;
                        controller.showElementInfo(selectedElement);
                        controller.showConnectionList(selectedElement, getElementConnections(selectedElement));
                    }
                    break;
                }
            }
            shouldUpdate = true;
        }
    }

    public void deselectElement() {
        presentlyClickedElement = null;
        shouldUpdate = true;
    }

    public void dropSelection() {
        if (elementToConnect != null) {
            elementToConnect = null;
        } else {
            presentlyClickedElement = null;
            selectedElement = null;
            controller.hideElementInfo();
            controller.hideConnectionList();
        }
    }

    public void removeSelectedElement() {
        removeConnections();
        elementList.remove(selectedElement);
        selectedElement = null;
        controller.hideElementInfo();
        controller.hideConnectionList();
    }

    public void removeConnection(int id) {
        for (Connection connection : connectionList) {
            if (connection.getId() == id) {
                connectionList.remove(connection);
                removeConnectionPorts(connection);
                break;
            }
        }
        controller.showConnectionList(selectedElement, getElementConnections(selectedElement));
    }

    private void removeConnections() {
        if (selectedElement == null) return;
        List<Connection> connectionsToRemove = new ArrayList<>();
        for (Port port : selectedElement.getPortList()) {
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
        getElementById(connection.getFirstElementId()).removePort(connection.getPortPair().getKey());
        getElementById(connection.getSecondElementId()).removePort(connection.getPortPair().getValue());
    }

    public void moveElement(int x, int y) {
        if (presentlyClickedElement != null && mousePosition != null) {
            presentlyClickedElement.setX(presentlyClickedElement.getX() + (x - mousePosition.getKey()));
            presentlyClickedElement.setY(presentlyClickedElement.getY() + (y - mousePosition.getValue()));
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
            for (Element element : elementList) {
                element.setX(element.getX() + (x - mousePosition.getKey()));
                element.setY(element.getY() + (y - mousePosition.getValue()));
            }
            mousePosition = new Pair<>(x, y);
        }
    }

    public void onConnectClicked() {
        if (selectedElement != null) {
            elementToConnect = selectedElement.getId();
        }
    }

    private void connectTo(int x, int y) {
        for (Element element : elementList) {
            if (x >= element.getX() && x <= element.getX() + Values.ELEMENT_SIZE && y >= element.getY() && y <= element.getY() + Values.ELEMENT_SIZE) {
                if (!elementToConnect.equals(element.getId())) {
                    if (!connectionAlreadyExists(elementToConnect, element.getId())) {
                        connectionList.add(new Connection(getElementById(elementToConnect).getNewPort() , element.getNewPort()));
                    }
                    elementToConnect = null;
                }
            }
        }
        controller.showConnectionList(selectedElement, getElementConnections(selectedElement));
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

    private List<Connection> getElementConnections(Element element) {
        List<Connection> selectedElementConnections = new ArrayList<>();
        for (Port port : element.getPortList()) {
            for (Connection connection : connectionList) {
                if (connection.containsPort(port)) {
                    selectedElementConnections.add(connection);
                }
            }
        }
        return selectedElementConnections;
    }

        public static void stopEngine() {
        closeTask = true;
    }

    private void startEngine() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (!closeTask) {
                    shouldUpdate = false;
                    clearScreen();
                    drawConnections();
                    drawElements();
                    drawMessages();
                    if (!shouldUpdate) {
                        Thread.sleep(Values.ENGINE_MILLISECONDS_PAUSE);
                    }
                }
                return null;
            }

            private void clearScreen() {
                ctx.clearRect(0, 0, ctx.getCanvas().getWidth(), ctx.getCanvas().getHeight());
            }

            private void drawConnections() {
                List<Element> reversedList = new ArrayList<>(elementList);
                Collections.reverse(reversedList);
                for (Connection connection : connectionList) {
                    Element element1 = getElementById(connection.getFirstElementId());
                    Element element2 = getElementById(connection.getSecondElementId());
                    if (element1 != null && element2 != null) {
                        ctx.setStroke(connection.getColor());
                        ctx.strokeLine(
                                element1.getX() + (Values.ELEMENT_SIZE / 2.0),
                                element1.getY() + (Values.ELEMENT_SIZE / 2.0),
                                element2.getX() + (Values.ELEMENT_SIZE / 2.0),
                                element2.getY() + (Values.ELEMENT_SIZE / 2.0));
                    }
                }
            }

            private void drawElements() {
                ctx.setStroke(Color.BLACK);
                List<Element> reversedList = new ArrayList<>(elementList);
                Collections.reverse(reversedList);
                for (Element element : reversedList) {
                    if (element == selectedElement) {
                        ctx.strokeRect(
                                element.getX() - Values.ELEMENT_STROKE,
                                element.getY() - Values.ELEMENT_STROKE,
                                Values.ELEMENT_SIZE + Values.ELEMENT_STROKE * 2,
                                Values.ELEMENT_SIZE + Values.ELEMENT_STROKE * 2
                        );
                    }
                    ctx.drawImage(element.getImage(), element.getX(), element.getY(), Values.ELEMENT_SIZE, Values.ELEMENT_SIZE);
                }
            }

            private void drawMessages() {
                for (Message message : messageList) {
                    ctx.drawImage(message.getImage(), message.getX(), message.getY(), Values.MESSAGE_SIZE, Values.MESSAGE_SIZE);
                }
            }
        };
        new Thread(task).start();
    }

    public void startSimulation() {
        if (runSimulation) return;
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                prepareSimulation();
                while(runSimulation) {
                    nextSimulationStep();
                    checkSimulationProgress();
                    Thread.sleep(Values.ENGINE_MILLISECONDS_PAUSE);
                }
                return null;
            }

            private void prepareSimulation() {
                runSimulation = true;
                messageList.clear();
                for (Connection connection : getElementConnections(selectedElement)) {
                    int idFrom;
                    int idTo;
                    if (connection.getFirstElementId() == selectedElement.getId()) {
                        idFrom = connection.getFirstElementId();
                        idTo = connection.getSecondElementId();
                    } else {
                        idFrom = connection.getSecondElementId();
                        idTo = connection.getFirstElementId();
                    }
                    messageList.add(new Message(getElementById(idFrom), getElementById(idTo)));
                }
            }

            private void nextSimulationStep() {
                List<Message> messagesToRemove = new ArrayList<>();
                List<Message> messagesToAdd = new ArrayList<>();
                for (Message message : messageList) {
                    if (message.getProgress() >= Values.MESSAGE_PROGRESS_MAX) {
                        List<Connection> elementConnectionList = new ArrayList<>();
                        for (Connection connection : connectionList) {
                            if (connection.getFirstElementId() == message.getTo().getId() || connection.getSecondElementId() == message.getTo().getId()) {
                                elementConnectionList.add(connection);
                            }
                        }
                        messagesToAdd.addAll(message.getTo().handleMessage(message.getFrom(), elementConnectionList));
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
        new Thread(task).start();
    }

    public void stopSimulation() {
        this.runSimulation = false;
    }
}