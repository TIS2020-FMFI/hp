package com.app.service.file;

import com.app.service.AppMain;
import com.app.service.notification.NotificationType;
import com.app.service.utils.Utils;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class DataNotSavedDialog {
    private final String path;
    private Stage stage;

    public DataNotSavedDialog(String controllerPath) {
        path = controllerPath;
    }

    public void openDialog() {
        try {
            stage = new Stage();

            Parent calibrationRoot = FXMLLoader.load(getClass().getResource(path));
            stage.setScene(new Scene(calibrationRoot));
            stage.setTitle("Exit");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            AppMain.notificationService.createNotification("data not saved dialog failed to open -> " + e.getMessage(), NotificationType.ERROR);
        }
    }

    public void saveAndCloseDialog() {
        //TODO: if autosave off prompt user to choose a folder, otherwise save automatically
        Utils.closeApp();
    }

    public void closeWithoutSaving() {
        stage.close();
        Utils.closeApp();
    }

}
