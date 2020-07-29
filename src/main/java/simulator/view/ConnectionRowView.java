package simulator.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import simulator.Engine;
import simulator.element.Connection;
import simulator.element.Port;
import util.Values;

public class ConnectionRowView extends HBox {
    private final int id;
    private OnDeleteClickListener onDeleteClickListener;

    public ConnectionRowView(Connection connection, Port port) {
        this.id = connection.getId();
        Engine engine = Engine.getInstance();
        Label connectedWith = new Label(engine.getDeviceByPort(port).getDeviceType());
        Label colorLabel = new Label();
        Button deleteConnection = new Button("Remove");

        colorLabel.setBackground(new Background(new BackgroundFill(connection.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
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