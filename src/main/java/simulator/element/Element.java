package simulator.element;

import javafx.scene.image.Image;

public class Element {
    private final int id;
    private final Image image;
    private int x;
    private int y;
    private final String deviceType;
    private static int idCounter = 0;

    protected Element(String imagePath, int x, int y, String deviceType) {
        this.image = new Image(imagePath);
        this.x = x;
        this.y = y;
        this.deviceType = deviceType;
        this.id = idCounter++;
    }

    public int getId() {
        return id;
    }

    public Image getImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
