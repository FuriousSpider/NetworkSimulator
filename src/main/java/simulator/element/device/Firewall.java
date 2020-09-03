package simulator.element.device;

import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.Port;
import util.Values;

import java.util.ArrayList;
import java.util.List;

public class Firewall extends Device {
    public static String fileName = "/firewall.png";
    public static String deviceType = "Firewall";

    public Firewall(int x, int y) {
        super(fileName, x, y, deviceType);
    }


    @Override
    void initPorts() {
        for (int i = 0; i < Values.DEVICE_FIREWALL_NUMBER_OF_PORTS; i++) {
            getPortList().add(new Port(i + 1));
        }
    }

    @Override
    public List<Message> handleMessage(Message message, List<Connection> connectionList) {
        return new ArrayList<>();
    }
}
