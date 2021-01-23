package com.app.service;

import com.app.service.calibration.CalibrationService;
import com.app.service.communication.CommunicationService;
import com.app.service.file.DataNotSavedDialog;
import com.app.service.file.FileService;
import com.app.service.file.parameters.EnvironmentParameters;
import com.app.service.graph.GraphService;
import com.app.service.help.HelpService;
import com.app.service.notification.NotificationService;
import com.app.service.notification.NotificationType;
import com.app.service.utils.Utils;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class AppMain extends Application {

    public static Stage ps;
    public static NotificationService notificationService;
    public static CalibrationService calibrationService;
    public static HelpService helpService;
    public static DataNotSavedDialog dataNotSavedDialog;
    public static FileService fileService;
    public static GraphService graphService;
    public static CommunicationService communicationService;
    public static EnvironmentParameters environmentParameters;
    public static boolean debugMode = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // its important to keep this method in this order!
        ps = primaryStage;

        fileService = new FileService("src/main/resources/persistent/config.json");
        environmentParameters = fileService.loadConfig();
        graphService = new GraphService();

        Parent root = FXMLLoader.load(getClass().getResource("/views/mainScreen.fxml"));
        primaryStage.setTitle("Super machine");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);

        graphService.setRoot(root);

        VBox notificationContainer = (VBox) root.lookup("#notificationContainer");
        if (notificationContainer == null) {
            throw new Exception("Notification container not found in this window!");
        }
        notificationService = new NotificationService(notificationContainer);

        communicationService = new CommunicationService();

        calibrationService = new CalibrationService("/views/calibrationScreen.fxml");
        helpService = new HelpService("/views/helpScreen.fxml");
        dataNotSavedDialog = new DataNotSavedDialog("/views/dataNotSavedDialog.fxml");


        // if all runs successfully then show
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
