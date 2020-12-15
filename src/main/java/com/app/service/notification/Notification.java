package com.app.service.notification;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;


public class Notification extends HBox {
    String message;
    NotificationType type;
    ImageView removeIcon;

    public Notification(String msg, NotificationType t, ImageView icon) {
        message = msg;
        type = t;
        removeIcon = icon;
    }

    public void show() {
        Label label = new Label(message);
        label.setPrefWidth(650);
        this.getChildren().addAll(label, removeIcon);
        this.setAlignment(Pos.CENTER_RIGHT);
        this.getStyleClass().addAll("notification", NotificationType.getClassName(type), "border");
    }

    public String getMessage() { return message; }
    public NotificationType getType() { return type; }

}
