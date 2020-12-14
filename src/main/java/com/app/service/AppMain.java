package com.app.service;

import com.app.screen.handler.ScreensController;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AppMain extends Application {

    public static Stage ps;
    public static NotificationService notificationService;

    @Override
    public void start(Stage primaryStage) {
        ScreensController mainContainer = new ScreensController();

        mainContainer.loadScreen("mainScreen", "/views/mainScreen.fxml");
        mainContainer.loadScreen("calibrationScreen", "/views/calibrationScreen.fxml");

        mainContainer.setScreen("mainScreen");

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        primaryStage.setTitle("Super machine");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setResizable(false);

        ps = primaryStage;
        VBox notificationContainer = (VBox) mainContainer.lookup("#notificationContainer");
        if (notificationContainer == null) {
            System.out.println("shit, notification container not found");
            return;
        }
        notificationService = new NotificationService(notificationContainer);
        notificationService.createNotification("First try", NotificationType.SUCCESS);
    }

    public static Stage getPrimaryStage() { return ps; }

    public static void main(String[] args) {
        launch(args);
    }
}
