package com.app.service.notification;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class NotificationService {
    private final Image removeImg = new Image("assets/remove.png");
    private final VBox notificationContainer;
    private final Vector<Notification> notificationQueue;

    public NotificationService(VBox container) {
        notificationContainer = container;
        notificationQueue = new Vector<>();
    }

    public boolean isNotificationContainerEmpty() { return notificationQueue.isEmpty(); }

    public void createNotification(String message, NotificationType type) {
        if (notificationQueue.size() > 1) {
            removeNotification(0);
        }
        ImageView removeIcon = new ImageView(removeImg);
        Notification notification = new Notification(message, type, removeIcon);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isNotificationContainerEmpty()) {
                    Platform.runLater(() -> {
                        if (notificationQueue.contains(notification)) {
                            removeNotification(notification);
                        }
                    });
                }
            }
        }, 8000);
        removeIcon.setOnMouseReleased(event -> removeNotification(notification));
        notificationQueue.add(notification);
        Platform.runLater(() -> notificationContainer.getChildren().add(notification.show()));
    }

    private void removeNotification(Notification notification) {
        Platform.runLater(() -> {
            notificationQueue.remove(notification);
            notificationContainer.getChildren().remove(notification);
        });
    }

    private void removeNotification(int nth) {
        Platform.runLater(() -> {
            Notification removed = notificationQueue.remove(nth);
            notificationContainer.getChildren().remove(removed);
        });
    }

}
