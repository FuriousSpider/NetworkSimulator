package simulator.element;

import javafx.scene.image.Image;

public class Element {
    private final Image image;
    private int x;
    private int y;

     protected Element(String imagePath, int x, int y) {
         this.image = new Image(imagePath);
         this.x = x;
         this.y = y;
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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
