package simulator.element;

public class Hub extends Element {
    public static String fileName = "/hub.png";
    public static String deviceType = "Hub";

    public Hub(int x, int y) {
        super(fileName, x, y, deviceType);
    }

}
