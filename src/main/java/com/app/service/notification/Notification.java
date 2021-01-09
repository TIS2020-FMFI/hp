package com.app.service.notification;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;


public class Notification extends HBox {
    private String message;
    private NotificationType type;
    private ImageView removeIcon;
    private Notification previous;

    public Notification(String msg, NotificationType t, ImageView icon) {
        message = msg;
        type = t;
        removeIcon = icon;
    }

    public Notification show() {
        Label label = new Label(message);
        label.setPrefWidth(800);
        this.getChildren().addAll(label, removeIcon);
        this.setAlignment(Pos.CENTER_RIGHT);
        this.getStyleClass().addAll("notification", NotificationType.getClassName(type), "border");
        return this;
    }

    public Notification getPrevious() { return previous; }

    public void setPrevious(Notification notification) { previous = notification; }

    public String getMessage() { return message; }
    public NotificationType getType() { return type; }

}
