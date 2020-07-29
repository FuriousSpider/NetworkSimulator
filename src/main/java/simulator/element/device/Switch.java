package simulator.element.device;

import simulator.element.Connection;
import simulator.element.Message;
import simulator.element.device.Element;

import java.util.ArrayList;
import java.util.List;

public class Switch extends Element {
    public static String fileName = "/switch.png";
    public static String deviceType = "Switch";

    public Switch(int x, int y) {
        super(fileName, x, y, deviceType);
    }

    @Override
    public List<Message> handleMessage(Element source, List<Connection> connectionList) {
        return new ArrayList<>();
    }
}
