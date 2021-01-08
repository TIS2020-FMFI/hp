package com.app.service.help;

import com.app.service.AppMain;
import com.app.service.notification.NotificationType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class HelpService {
    private final String path;
    private Stage stage;

    public HelpService(String controllerPath) {
        path = controllerPath;
    }

    public void openHelp() {
        try {
            stage = new Stage();

            Parent calibrationRoot = FXMLLoader.load(getClass().getResource(path));
            stage.setScene(new Scene(calibrationRoot));
            stage.setTitle("Help");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            AppMain.notificationService.createNotification(e.getMessage(), NotificationType.ERROR);
        }
    }

    public void closeHelp() {
        stage.close();
    }

}
