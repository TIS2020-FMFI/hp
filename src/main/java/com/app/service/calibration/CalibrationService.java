package com.app.service.calibration;

import com.app.service.notification.NotificationService;
import com.app.service.notification.NotificationType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CalibrationService {
    private final String path;
    private NotificationService notificationService;

    public CalibrationService(String controllerPath) {
        path = controllerPath;
    }

    public void openCalibration() throws Exception {
        Stage stage = new Stage();

        Parent calibrationRoot = FXMLLoader.load(getClass().getResource(path));
        stage.setScene(new Scene(calibrationRoot));
        stage.setTitle("Calibration");
        stage.setResizable(false);
        VBox notificationContainer = (VBox) calibrationRoot.lookup("#notificationContainer");
        if (notificationContainer == null) {
            throw new Exception("Notification container not found in this window!");
        }
        notificationService = new NotificationService(notificationContainer);
        stage.show();
    }

    public void runCalibration() {
        notificationService.createNotification("super fungujem", NotificationType.WARNING).show();
    }
}
