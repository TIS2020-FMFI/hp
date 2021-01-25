package com.app.service.notification;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;


public class Notification extends HBox {
    private final String message;
    private final NotificationType type;
    private final ImageView removeIcon;

    public Notification(String message, NotificationType type, ImageView removeIcon) {
        this.message = message;
        this.type = type;
        this.removeIcon = removeIcon;

        removeIcon.getStyleClass().add("border");
        removeIcon.setPreserveRatio(true);
        removeIcon.setCursor(Cursor.HAND);
        removeIcon.setFitWidth(25);
    }

    public Notification show() {
        Label label = new Label(message);
        label.setPrefWidth(650);
        this.getChildren().addAll(label, removeIcon);
        this.setAlignment(Pos.CENTER_RIGHT);
        this.getStyleClass().addAll("notification", NotificationType.getClassName(type), "border");
        return this;
    }

}
