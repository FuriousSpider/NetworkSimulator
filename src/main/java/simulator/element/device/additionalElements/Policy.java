package simulator.element.device.additionalElements;

import util.Utils;

import java.util.HashSet;
import java.util.Set;

public class Policy {
    private int id;
    private String sourceNetworkAddress;
    private String destinationNetworkAddress;
    private final Set<Application> applicationSet;
    private Rule rule;
    private static int idCounter = 0;

    public Policy() {
        this.applicationSet = new HashSet<>();
        this.rule = Rule.DENY;
        this.id = idCounter++;
    }

    public int getId() {
        return id;
    }

    public void setId(int policyId) {
        this.id = policyId;
    }

    public String getSourceNetworkAddress() {
        return sourceNetworkAddress;
    }

    public void setSourceNetworkAddress(String address) {
        if (address != null && address.isEmpty() || address != null && !Utils.isNetworkAddress(address)) {
            sourceNetworkAddress = null;
        } else {
            sourceNetworkAddress = address;
        }
    }

    public String getDestinationNetworkAddress() {
        return destinationNetworkAddress;
    }

    public void setDestinationNetworkAddress(String address) {
        if (address != null && address.isEmpty() || address != null && !Utils.isNetworkAddress(address)) {
            destinationNetworkAddress = null;
        } else {
            destinationNetworkAddress = address;
        }
    }

    public Set<Application> getApplicationSet() {
        return applicationSet;
    }

    public void setApplicationSet(Set<Application> applicationSet) {
        this.applicationSet.clear();
        this.applicationSet.addAll(applicationSet);
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public static int getIdCounter() {
        return idCounter;
    }

    public static void setIdCounter(int counter) {
        idCounter = counter;
    }

    public enum Application {
        UDP,
        TCP
    }

    public enum Rule {
        PERMIT,
        DENY
    }
}
