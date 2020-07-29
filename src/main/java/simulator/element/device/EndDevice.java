package simulator.element.device;

import simulator.element.Connection;
import simulator.element.Message;

import java.util.ArrayList;
import java.util.List;

public class EndDevice extends Element {
    public static String fileName = "/endDevice.png";
    public static String deviceType = "End Device";

    public EndDevice(int x, int y) {
        super(fileName, x, y, deviceType);
    }

    @Override
    public List<Message> handleMessage(Element source, List<Connection> connectionList) {
        return new ArrayList<>();
    }
}
