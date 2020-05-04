package simulator.element;

public class Firewall extends Element {
    public static String fileName = "/firewall.png";
    public static String deviceType = "Firewall";

    public Firewall(int x, int y) {
        super(fileName, x, y, deviceType);
    }

}
