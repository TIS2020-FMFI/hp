package com.app.service;

import com.app.service.calibration.CalibrationService;
import com.app.service.communication.CommunicationService;
import com.app.service.file.FileService;
import com.app.service.file.parameters.EnvironmentParameters;
import com.app.service.graph.GraphService;
import com.app.service.help.HelpWindow;
import com.app.service.notification.NotificationService;
import com.app.service.measurement.AbortDataWindow;
import com.app.service.measurement.DataNotSavedWindow;
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
    public static HelpWindow helpWindow;
    public static DataNotSavedWindow dataNotSavedWindow;
    public static AbortDataWindow abortDataWindow;
    public static FileService fileService;
    public static GraphService graphService;
    public static CommunicationService communicationService;
    public static EnvironmentParameters environmentParameters;
    public static boolean debugMode = true;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // its important to keep this method in this order!
        ps = primaryStage;

        fileService = new FileService("config.json");
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
        helpWindow = new HelpWindow("/views/helpScreen.fxml");
        dataNotSavedWindow = new DataNotSavedWindow("/views/dataNotSavedDialog.fxml");
        abortDataWindow = new AbortDataWindow("/views/abortDataDialog.fxml");


        // if all runs successfully then show
        primaryStage.show();
        communicationService.autoConnect();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
