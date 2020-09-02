package simulator.element;

import simulator.view.IPTextField;

public class Port implements IPTextField.OnSaveClickedListener {
    private int id;
    private Interface anInterface;
    private boolean isPortTaken;

    private static int idCounter;

    public Port() {
        this.id = idCounter++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNewInterface() {
        this.anInterface = new Interface();
    }

    public boolean hasInterface() {
        return anInterface != null;
    }

    public boolean isPortTaken() {
        return isPortTaken;
    }

    public void reservePort() {
        this.isPortTaken = true;
    }

    public void releasePort() {
        this.isPortTaken = false;
    }

    public String getIpAddress() {
        if (hasInterface()) {
            return anInterface.getAddress();
        } else {
            return null;
        }
    }

    public void setIpAddress(String ipAddress) {
        if (hasInterface()) {
            anInterface.setAddress(ipAddress);
        }
    }

    public static int getIdCounter() {
        return idCounter;
    }

    public static void setIdCounter(int counter) {
        idCounter = counter;
    }

    @Override
    public void onSaveClicked(String ipAddress) {
        if (hasInterface()) {
            anInterface.setAddress(ipAddress);
        }
    }
}
