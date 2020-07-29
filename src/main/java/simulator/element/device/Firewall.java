package simulator.element.device;

import simulator.element.Connection;
import simulator.element.Message;

import java.util.ArrayList;
import java.util.List;

public class Firewall extends Device {
    public static String fileName = "/firewall.png";
    public static String deviceType = "Firewall";

    public Firewall(int x, int y) {
        super(fileName, x, y, deviceType);
    }

    @Override
    public List<Message> handleMessage(Message message, List<Connection> connectionList) {
        return new ArrayList<>();
    }
}
