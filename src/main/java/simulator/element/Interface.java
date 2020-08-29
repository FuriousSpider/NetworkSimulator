package simulator.element;

public class Interface {
    private String address;

    public Interface() {
        this.address = "0.0.0.0/0";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
