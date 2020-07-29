package simulator.element;

import javafx.scene.image.Image;
import simulator.element.device.Element;
import util.Values;

public class Message {
    public static String fileName = "/message.png";

    private final Element from;
    private final Element to;
    private int progress;
    private final Image image;

    public Message(Element from, Element to) {
        this.from = from;
        this.to = to;
        this.progress = 0;
        this.image = new Image(fileName);
    }

    public int getX() {
        return from.getX() + (to.getX() - from.getX()) * progress / Values.MESSAGE_PROGRESS_MAX;
    }

    public int getY() {
        return from.getY() + (to.getY() - from.getY()) * progress / Values.MESSAGE_PROGRESS_MAX;
    }

    public Element getFrom() {
        return from;
    }

    public Element getTo() {
        return to;
    }

    public int getProgress() {
        return progress;
    }

    public Image getImage() {
        return image;
    }

    public void nextStep() {
        progress += Values.MESSAGE_PROGRESS_STEP;
    }
}
