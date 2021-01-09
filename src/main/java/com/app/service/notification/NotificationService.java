package com.app.service.notification;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService {
    private final Image removeImg = new Image("assets/remove.png");
    private VBox notificationContainer;
    private Notification notification;

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
        removeIcon.setFitWidth(25);
        addNotification(new Notification(message, type, removeIcon));
        removeIcon.setOnMouseReleased(event -> {
            removeNotification(notification);
            notification = null;
        });
    }

    private void addNotification(Notification newNotification) {
        newNotification.setPrevious(notification);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isNotificationContainerEmpty()) {
                    Platform.runLater(() -> {
                        if (notificationContainer.getChildren().contains(newNotification)) {
                            notificationTimeOut(newNotification);
                        }
                    });
                }
            }
        }, 8000);
        moveInQueue();
        notification = newNotification.show();
        notificationContainer.getChildren().add(notification);
    }

    private void removeNotification(Notification notification) {
        if (notification == null) {
            notificationContainer.getChildren().clear();
            return;
        }
        if (this.notification == notification && this.notification.getPrevious() != null) {
            this.notification = this.notification.getPrevious();
        }
        notificationContainer.getChildren().remove(notification);
    }

    private void moveInQueue() {
        if (notification != null && notification.getPrevious() != null) {
            removeNotification(notification.getPrevious());
            notification.setPrevious(null);
        }
    }

    private void notificationTimeOut(Notification newNotification) {
        if (notification == newNotification) {
            removeNotification(notification);
            notification = null;
        } else {
            removeNotification(notification.getPrevious());
            notification.setPrevious(null);
        }
    }

}
