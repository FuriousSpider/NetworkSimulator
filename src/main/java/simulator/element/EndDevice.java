package simulator.element;

public class EndDevice extends Element {
    public static String fileName = "/endDevice.png";
    public static String deviceType = "End Device";

    public EndDevice(int x, int y) {
        super(fileName, x, y, deviceType);
    }

}
