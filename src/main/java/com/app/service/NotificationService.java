package com.app.service;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class NotificationService {
    final Image removeImg = new Image("assets/remove.png");
    VBox notificationContainer;
    Notification notification;

    public NotificationService(VBox container) {
        notificationContainer = container;
    }

    public Notification getNotification() { return notification; }
    public boolean isNotificationContainerEmpty() { return notification == null; }

    public void createNotification(String message, NotificationType type) {
        ImageView removeIcon = new ImageView(removeImg);
        removeIcon.getStyleClass().add("border");
        removeIcon.setPreserveRatio(true);
        removeIcon.setCursor(Cursor.HAND);
        removeIcon.setFitWidth(20);
        removeIcon.setOnMouseReleased(event -> {
            removeNotification();
        });
        notification = new Notification(message, type, removeIcon);
        notification.show();
        notificationContainer.getChildren().add(notification);
    }

    public void removeNotification() {
        notificationContainer.getChildren().remove(notification);
        notification = null;
    }

}
