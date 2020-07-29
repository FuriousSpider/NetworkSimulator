package simulator.element.device;

import simulator.Engine;
import simulator.element.Connection;
import simulator.element.Message;

import java.util.ArrayList;
import java.util.List;

public class Hub extends Element {
    public static String fileName = "/hub.png";
    public static String deviceType = "Hub";

    public Hub(int x, int y) {
        super(fileName, x, y, deviceType);
    }

    @Override
    public List<Message> handleMessage(Element source, List<Connection> connectionList) {
        List<Message> messageList = new ArrayList<>();
        for (Connection connection : connectionList) {
            Element other;
            if (Engine.getInstance().getElementById(connection.getFirstElementId()) == this) {
                other = Engine.getInstance().getElementById(connection.getSecondElementId());
            } else {
                other = Engine.getInstance().getElementById(connection.getFirstElementId());
            }
            if (other != source) {
                messageList.add(new Message(this, other));
            }
        }
        return messageList;
    }
}
