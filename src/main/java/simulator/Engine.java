package simulator;

import javafx.concurrent.Task;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Pair;
import simulator.element.Connection;
import simulator.element.Element;
import util.Values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Engine {
    private final Controller controller;
    private final GraphicsContext ctx;
    private final List<Element> elementList;
    private final List<Connection> connectionList;
    private Integer elementToConnect;
    private Element presentlyClickedElement;
    private Element selectedElement;
    private boolean shouldUpdate;
    private static boolean closeTask;
    private Pair<Integer, Integer> mousePosition;

    public Engine(Controller controller, GraphicsContext context) {
        this.controller = controller;
        this.ctx = context;
        this.elementList = new ArrayList<>();
        this.connectionList = new ArrayList<>();
        this.elementToConnect = null;
        this.presentlyClickedElement = null;
        this.selectedElement = null;
        this.shouldUpdate = false;
        closeTask = false;
        this.mousePosition = null;

        setCtxConfig();
        startEngine();
    }

    private void setCtxConfig() {
        ctx.setStroke(Paint.valueOf("000000"));
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
                        controller.showConnectionList(selectedElement.getId(), getSelectedElementConnections());
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
            if ((connection.getFirstId() == id && connection.getSecondId() == selectedElement.getId()) || (connection.getSecondId() == id && connection.getFirstId() == selectedElement.getId())) {
                connectionList.remove(connection);
                break;
            }
        }
        controller.showConnectionList(selectedElement.getId(), getSelectedElementConnections());
    }

    private void removeConnections() {
        if (selectedElement == null) return;
        List<Connection> connectionsToRemove = new ArrayList<>();
        for (Connection connection : connectionList) {
            if (selectedElement.getId() == connection.getFirstId() || selectedElement.getId() == connection.getSecondId()) {
                connectionsToRemove.add(connection);
            }
        }
        if (!connectionsToRemove.isEmpty()) {
            connectionList.removeAll(connectionsToRemove);
        }
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
                        connectionList.add(new Connection(elementToConnect, element.getId()));
                    }
                    elementToConnect = null;
                }
            }
        }
        controller.showConnectionList(selectedElement.getId(), getSelectedElementConnections());
    }

    private boolean connectionAlreadyExists(Integer id1, int id2) {
        for (Connection connection : connectionList) {
            if ((id1.equals(connection.getFirstId()) && id2 == connection.getSecondId()) || (id1.equals(connection.getSecondId()) && id2 == connection.getFirstId())) {
                return true;
            }
        }
        return false;
    }

    private List<Connection> getSelectedElementConnections() {
        List<Connection> selectedElementConnections = new ArrayList<>();
        for (Connection connection : connectionList) {
            if (connection.getFirstId() == selectedElement.getId() || connection.getSecondId() == selectedElement.getId()) {
                selectedElementConnections.add(connection);
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
                    ctx.clearRect(0, 0, ctx.getCanvas().getWidth(), ctx.getCanvas().getHeight());
                    List<Element> reversedList = new ArrayList<>(elementList);
                    Collections.reverse(reversedList);
                    for (Connection connection : connectionList) {
                        Element element1 = getElementById(connection.getFirstId());
                        Element element2 = getElementById(connection.getSecondId());
                        if (element1 != null && element2 != null) {
                            ctx.setStroke(connection.getColor());
                            ctx.strokeLine(
                                    element1.getX() + (Values.ELEMENT_SIZE / 2.0),
                                    element1.getY() + (Values.ELEMENT_SIZE / 2.0),
                                    element2.getX() + (Values.ELEMENT_SIZE / 2.0),
                                    element2.getY() + (Values.ELEMENT_SIZE / 2.0));
                        }
                    }
                    ctx.setStroke(Color.BLACK);
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

                    if (!shouldUpdate) {
                        Thread.sleep(Values.ENGINE_MILLISECONDS_PAUSE);
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }
}