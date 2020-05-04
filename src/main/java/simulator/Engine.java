package simulator;

import javafx.concurrent.Task;
import javafx.scene.canvas.GraphicsContext;
import simulator.element.Element;
import util.Values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Engine {
    private final GraphicsContext ctx;
    private final List<Element> elementList;
    private Element selectedElement;
    private boolean shouldUpdate;
    private static boolean closeTask;

    public Engine(GraphicsContext context) {
        this.ctx = context;
        this.elementList = new ArrayList<>();
        this.shouldUpdate = false;
        closeTask = false;

        startEngine();
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
