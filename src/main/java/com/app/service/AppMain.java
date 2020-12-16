package com.app.service;

import com.app.service.calibration.CalibrationService;
import com.app.service.file.FileService;
import com.app.service.file.parameters.EnvironmentParameters;
import com.app.service.notification.NotificationService;
import com.app.service.notification.NotificationType;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AppMain extends Application {

    public static Stage ps;
    public static NotificationService notificationService;
    public static CalibrationService calibrationService;
    public static FileService fileService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/mainScreen.fxml"));
        primaryStage.setTitle("Super machine");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);

        ps = primaryStage;
        calibrationService = new CalibrationService("/views/calibrationScreen.fxml");
        fileService = new FileService("src/main/resources/persistent/config.json");
        EnvironmentParameters parameters = fileService.loadConfig();

        VBox notificationContainer = (VBox) root.lookup("#notificationContainer");
        if (notificationContainer == null) {
            throw new Exception("Notification container not found in this window!");
        }
        notificationService = new NotificationService(notificationContainer);
        notificationService.createNotification("First try", NotificationType.SUCCESS).show();


        // if all runs successfully then show
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
