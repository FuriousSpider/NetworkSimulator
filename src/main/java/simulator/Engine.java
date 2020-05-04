package simulator;

import javafx.concurrent.Task;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import simulator.element.Element;
import util.Values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Engine {
    private final Controller controller;
    private final GraphicsContext ctx;
    private final List<Element> elementList;
    private Element selectedElement;
    private Element lastSelectedElement;
    private boolean shouldUpdate;
    private static boolean closeTask;

    public Engine(Controller controller, GraphicsContext context) {
        this.controller = controller;
        this.ctx = context;
        this.elementList = new ArrayList<>();
        this.shouldUpdate = false;
        closeTask = false;

        setCtxConfig();
        startEngine();
    }

    private void setCtxConfig() {
        ctx.setStroke(Paint.valueOf("000000"));
        ctx.setLineWidth(2.0);
    }

    public void addDevice(Element element) {
        this.elementList.add(0, element);
        shouldUpdate = true;
    }

    public void selectElement(int x, int y) {
        for (Element element : elementList) {
            if (x >= element.getX() && x <= element.getX() + Values.ELEMENT_SIZE && y >= element.getY() && y <= element.getY() + Values.ELEMENT_SIZE) {
                if (selectedElement != element) {
                    selectedElement = element;
                    lastSelectedElement = selectedElement;
                    controller.showElementInfo(selectedElement);
                }
                break;
            }
        }
        shouldUpdate = true;
    }

    public void deselectElement() {
        selectedElement = null;
        shouldUpdate = true;
    }

    public void dropSelection() {
        selectedElement = null;
        lastSelectedElement = null;
        controller.hideElementInfo();
    }

    public void removeSelectedElement() {
        elementList.remove(lastSelectedElement);
        lastSelectedElement = null;
        controller.hideElementInfo();
    }

    public void moveElement(int x, int y) {
        if (selectedElement != null) {
            selectedElement.setX(x - Values.ELEMENT_SIZE / 2);
            selectedElement.setY(y - Values.ELEMENT_SIZE / 2);
            shouldUpdate = true;
        }
    }

    public static void stopEngine() {
        closeTask = true;
    }

    private void startEngine() {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (!closeTask) {
                    shouldUpdate = false;
                    ctx.clearRect(0, 0, ctx.getCanvas().getWidth(), ctx.getCanvas().getHeight());
                    List<Element> reversedList = new ArrayList<>(elementList);
                    Collections.reverse(reversedList);
                    for (Element element : reversedList) {
                        if (element == lastSelectedElement) {
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