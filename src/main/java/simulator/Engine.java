package simulator;

import javafx.concurrent.Task;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.util.Pair;
import simulator.element.Element;
import util.Values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Engine {
    private final Controller controller;
    private final GraphicsContext ctx;
    private final List<Element> elementList;
    private final List<Pair<Integer, Integer>> connectionList;
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

    private Element getElementById(int id) {
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
        }
    }

    public void removeSelectedElement() {
        removeConnections();
        elementList.remove(selectedElement);
        selectedElement = null;
        controller.hideElementInfo();
    }

    private void removeConnections() {
        List<Pair<Integer, Integer>> connectionsToRemove = new ArrayList<>();
        for (Pair<Integer, Integer> pair : connectionList) {
            if (selectedElement.getId() == pair.getKey() || selectedElement.getId() == pair.getValue()) {
                connectionsToRemove.add(pair);
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
                        connectionList.add(new Pair<>(elementToConnect, element.getId()));
                    }
                    elementToConnect = null;
                }
            }
        }
    }

    private boolean connectionAlreadyExists(Integer id1, int id2) {
        for (Pair<Integer, Integer> pair : connectionList) {
            if ((id1.equals(pair.getKey()) && id2 == pair.getValue()) || (id1.equals(pair.getValue()) && id2 == pair.getKey())) {
                return true;
            }
        }
        return false;
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
                    for (Pair<Integer, Integer> pair : connectionList) {
                        Element element1 = getElementById(pair.getKey());
                        Element element2 = getElementById(pair.getValue());
                        if (element1 != null && element2 != null) {
                            ctx.strokeLine(
                                    element1.getX() + (Values.ELEMENT_SIZE / 2.0),
                                    element1.getY() + (Values.ELEMENT_SIZE / 2.0),
                                    element2.getX() + (Values.ELEMENT_SIZE / 2.0),
                                    element2.getY() + (Values.ELEMENT_SIZE / 2.0));
                        }
                    }
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