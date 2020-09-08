package simulator.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class TrunkModeAllowedVlanRow extends GridPane {
    private final Label vLanIdLabel;
    private final Button removeIdButton;

    private OnRemoveButtonClickedListener onRemoveButtonClickedListener;

    public TrunkModeAllowedVlanRow() {
        this.vLanIdLabel = new Label();
        this.removeIdButton = new Button("Remove");

        this.add(vLanIdLabel, 0, 0);
        this.add(removeIdButton, 1, 0);

        this.setHgap(10);

        this.removeIdButton.setOnMouseClicked(this::onRemoveClicked);
    }

    private void onRemoveClicked(MouseEvent mouseEvent) {
        if (onRemoveButtonClickedListener != null) {
            onRemoveButtonClickedListener.onRemoveClicked(Integer.parseInt(vLanIdLabel.getText()));
        }
    }

    public void setVLanId(int vLanId) {
        this.vLanIdLabel.setText(String.valueOf(vLanId));
    }

    public void setOnRemoveButtonClickedListener(OnRemoveButtonClickedListener listener) {
        this.onRemoveButtonClickedListener = listener;
    }

    public interface OnRemoveButtonClickedListener {
        void onRemoveClicked(Integer vLanId);
    }
}
