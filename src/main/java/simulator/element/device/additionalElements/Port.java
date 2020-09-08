package simulator.element.device.additionalElements;

import simulator.view.IPTextField;
import simulator.view.TrunkModeAllowedVlan;
import simulator.view.VLanTextField;
import util.Values;

import java.util.List;

public class Port implements IPTextField.OnSaveClickedListener, VLanTextField.OnSaveClickedListener, VLanTextField.OnChangeModeClickedListener, TrunkModeAllowedVlan.OnAllowedVLanChangeListener {
    private int id;
    private int portNumber;
    private Interface anInterface;
    private boolean isPortTaken;
    private VLan vLan;

    private static int idCounter;

    public Port(int portNumber) {
        this.portNumber = portNumber;
        this.id = idCounter++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getPortName() {
        return "Port" + portNumber;
    }

    public void setNewInterface() {
        this.anInterface = new Interface();
    }

    public boolean hasInterface() {
        return anInterface != null;
    }

    public int getVLanId() {
        return this.vLan.getId();
    }

    public boolean isInTrunkMode() {
        return this.vLan.isInTrunkMode();
    }

    public void setTrunkMode(boolean isInTrunkMode) {
        if (hasVLan()) {
            this.vLan.setInTrunkMode(isInTrunkMode);
        }
    }

    public void setVLanTrunkModeAllowedIds(List<Integer> vLanTrunkModeAllowedIds) {
        if (hasVLan()) {
            this.vLan.setTrunkModeAllowedIds(vLanTrunkModeAllowedIds);
        }
    }

    public List<Integer> getTrunkModeAllowedIds() {
        if (hasVLan()) {
            return this.vLan.getTrunkModeAllowedIds();
        }
        return null;
    }

    public void setVLan() {
        this.vLan = new VLan(Values.PORT_DEFAULT_VLAN_ID);
    }

    public void setVLanId(int vLanId) {
        if (hasVLan()) {
            this.vLan.setId(vLanId);
        }
    }

    public boolean hasVLan() {
        return vLan != null;
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

    @Override
    public void onChangeModeClicked(boolean isInTrunkMode) {
        if (hasVLan()) {
            vLan.setInTrunkMode(isInTrunkMode);
        }
    }

    @Override
    public void onSaveClicked(int vLanId) {
        if (hasVLan()) {
            vLan.setId(vLanId);
        }
    }

    @Override
    public void onAllowedVLanChange(List<Integer> allowedVLanList) {
        if (hasVLan()) {
            vLan.setTrunkModeAllowedIds(allowedVLanList);
        }
    }
}
