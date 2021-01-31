package com.app.service.notification;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


/**
 * Service for handling notifications
 */
public class NotificationService {
    private final Image removeImg = new Image("assets/remove.png");
    private final VBox notificationContainer;
    private final Vector<Notification> notificationQueue;

    /**
     * Constructs a {@code NotificationService} with the specified container.
     * @param container
     *        Gui component where notifications should appear
     */
    public NotificationService(VBox container) {
        notificationContainer = container;
        notificationQueue = new Vector<>();
    }

    /**
     * Creates an instance of a notification and adds it to gui and notification queue
     * @param message
     *        Text to be displayed with the notification
     * @param type
     *        The type of notification
     */
    public void createNotification(String message, NotificationType type) {
        if (notificationQueue.size() > 1) {
            removeNotification(0);
        }
        ImageView removeIcon = new ImageView(removeImg);
        Notification notification = new Notification(message, type, removeIcon);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!notificationQueue.isEmpty()) {
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

    /**
     * Removes notification from gui and queue of notifications
     * @param notification
     *        Number of the notification in queue to be removed
     */
    private void removeNotification(Notification notification) {
        Platform.runLater(() -> {
            notificationQueue.remove(notification);
            notificationContainer.getChildren().remove(notification);
        });
    }

    /**
     * Removes notification from gui and queue of notifications
     * @param nth
     *        Number of the notification in queue to be removed
     */
    private void removeNotification(int nth) {
        Platform.runLater(() -> {
            Notification removed = notificationQueue.remove(nth);
            notificationContainer.getChildren().remove(removed);
        });
    }

}
