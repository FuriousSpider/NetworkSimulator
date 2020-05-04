package simulator.element;

public class Router extends Element {
    public static String fileName = "/router.png";
    public static String deviceType = "Router";

    public Router(int x, int y) {
        super(fileName, x, y, deviceType);
    }

}
