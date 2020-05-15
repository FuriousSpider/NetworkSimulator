package simulator.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import util.Values;

public class ConnectionRowView extends HBox {
    private final int id;
    private OnDeleteClickListener onDeleteClickListener;

    public ConnectionRowView(int id, String connectedWithName, Color color) {
        this.id = id;
        Label connectedWith = new Label(connectedWithName);
        Label colorLabel = new Label();
        Button deleteConnection = new Button("Remove");

        colorLabel.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        colorLabel.setMinHeight(Values.LABEL_COLOR_MIN_HEIGHT);
        colorLabel.setMinWidth(Values.LABEL_COLOR_MIN_WIDHT);
        deleteConnection.setOnMouseClicked(this::onMouseClicked);

        this.getChildren().add(connectedWith);
        this.getChildren().add(colorLabel);
        this.getChildren().add(deleteConnection);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    private void onMouseClicked(MouseEvent mouseEvent) {
        onDeleteClickListener.onClick(id);
    }

    public interface OnDeleteClickListener {
        void onClick(int id);
    }
}