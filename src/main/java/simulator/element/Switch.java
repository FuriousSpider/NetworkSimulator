package simulator.element;

public class Switch extends Element {
    public static String fileName = "/switch.png";
    public static String deviceType = "Switch";

    public Switch(int x, int y) {
        super(fileName, x, y, deviceType);
    }

}
