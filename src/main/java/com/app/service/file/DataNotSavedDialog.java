package com.app.service.file;

import com.app.service.AppMain;
import com.app.service.graph.GraphState;
import com.app.service.notification.NotificationType;
import com.app.service.utils.Utils;
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
        if(AppMain.graphService.upperGraph != null && !AppMain.graphService.upperGraph.getState().equals(GraphState.RUNNING)) {
            boolean success;
            if (AppMain.fileService.isAutoSave()) {
                success = AppMain.fileService.autoSaveMeasurement(AppMain.graphService.upperGraph.getMeasurement());
            } else {
                AppMain.notificationService.createNotification("Select a folder to save the measurement from the upper graph.", NotificationType.ANNOUNCEMENT);
                success = AppMain.fileService.saveAsMeasurement(AppMain.graphService.upperGraph.getMeasurement());
            }
            if(success){
                AppMain.notificationService.createNotification("The measurement in the upper graph is saved.", NotificationType.SUCCESS);
            }else{
                AppMain.notificationService.createNotification("The measurement in the upper graph was not saved.", NotificationType.ERROR);
            }
        }
        if(AppMain.graphService.lowerGraph != null && !AppMain.graphService.lowerGraph.getState().equals(GraphState.RUNNING)) {
            boolean success;
            if (AppMain.fileService.isAutoSave()) {
                success = AppMain.fileService.autoSaveMeasurement(AppMain.graphService.lowerGraph.getMeasurement());
            } else {
                AppMain.notificationService.createNotification("Select a folder to save the measurement from the lower graph.", NotificationType.ANNOUNCEMENT);
                success = AppMain.fileService.saveAsMeasurement(AppMain.graphService.lowerGraph.getMeasurement());
            }
            if(success){
                AppMain.notificationService.createNotification("The measurement in the lower graph is saved.", NotificationType.SUCCESS);
            }else{
                AppMain.notificationService.createNotification("The measurement in the lower graph was not saved.", NotificationType.ERROR);
            }
        }
        Utils.closeApp();
    }

    public void closeWithoutSaving() {
        stage.close();
        Utils.closeApp();
    }

}
