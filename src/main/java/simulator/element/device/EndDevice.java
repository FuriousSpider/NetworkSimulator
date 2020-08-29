package simulator.element.device;

import simulator.element.Connection;
import simulator.element.Interface;
import simulator.element.Message;
import simulator.view.IPTextField;

import java.util.ArrayList;
import java.util.List;

public class EndDevice extends Device implements IPTextField.OnSaveClickedListener {
    public static String fileName = "/endDevice.png";
    public static String deviceType = "End Device";
    private final Interface anInterface;

    public EndDevice(int x, int y) {
        super(fileName, x, y, deviceType);
        this.anInterface = new Interface();
    }

    @Override
    public List<Message> handleMessage(Message message, List<Connection> connectionList) {
        return new ArrayList<>();
    }

    public String getIpAddress() {
        return anInterface.getAddress();
    }

    @Override
    public void onSaveClicked(String ipAddress) {
        anInterface.setAddress(ipAddress);
    }
}
