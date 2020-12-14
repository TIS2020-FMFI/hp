package com.app.service;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;


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
        this.getChildren().addAll(new Label(message), removeIcon);
        this.setAlignment(Pos.CENTER_RIGHT);
        this.setSpacing(620);
        this.getStyleClass().addAll("notification", NotificationType.getClassName(type), "border");
    }

    public String getMessage() { return message; }
    public NotificationType getType() { return type; }

}
