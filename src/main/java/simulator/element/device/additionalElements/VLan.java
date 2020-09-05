package simulator.element.device.additionalElements;

import java.util.ArrayList;
import java.util.List;

public class VLan {
    private int id;
    private boolean isInTrunkMode;
    private final List<Integer> trunkModeAllowedIds;

    public VLan(int id) {
        this.id = id;
        this.isInTrunkMode = false;
        this.trunkModeAllowedIds = new ArrayList<>();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isInTrunkMode() {
        return isInTrunkMode;
    }

    public void setInTrunkMode(boolean inTrunkMode) {
        isInTrunkMode = inTrunkMode;
    }

    public List<Integer> getTrunkModeAllowedIds() {
        return this.trunkModeAllowedIds;
    }

    public void setTrunkModeAllowedIds(List<Integer> list) {
        this.trunkModeAllowedIds.clear();
        this.trunkModeAllowedIds.addAll(list);
    }
}