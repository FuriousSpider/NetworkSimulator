package simulator.view;

import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.paint.Color;

public class ColorPickerDialog extends Dialog<String> {
    private final ColorPicker colorPicker;
    private OnColorSelectedListener onColorSelectedListener;

    public ColorPickerDialog() {
        this.colorPicker = new ColorPicker();

        this.colorPicker.setOnAction(this::onColorPickerAction);

        this.getDialogPane().setContent(colorPicker);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        this.setTitle("Select color");
    }

    public void setColor(Color color) {
        this.colorPicker.setValue(color);
    }

    private void onColorPickerAction(ActionEvent actionEvent) {
        if (onColorSelectedListener != null) {
            onColorSelectedListener.onColorSelected(colorPicker.getValue());
        }
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.onColorSelectedListener = listener;
    }

    public interface OnColorSelectedListener {
        void onColorSelected(Color color);
    }
}
