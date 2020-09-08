package simulator.element.device.additionalElements;

public class AssociationTableEntry {
    private final int vLanId;
    private final String macAddress;
    private final int portId;

    public AssociationTableEntry(int vLanId, String macAddress, int portId) {
        this.vLanId = vLanId;
        this.macAddress = macAddress;
        this.portId = portId;
    }

    public int getvLanId() {
        return vLanId;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public int getPortId() {
        return portId;
    }
}
