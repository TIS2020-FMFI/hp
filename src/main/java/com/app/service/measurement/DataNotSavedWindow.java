package com.app.service.measurement;

import com.app.service.AppMain;
import com.app.service.notification.NotificationType;
import com.app.service.utils.Utils;
import com.app.service.Window;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Window that's triggered when closing the app but unsaved data is present
 */
public class DataNotSavedWindow implements Window {
    private final String path;
    private Stage stage;


    /**
     * Initializes data not saved window
     *
     * @param controllerPath path to the view
     */
    public DataNotSavedWindow(String controllerPath) {
        path = controllerPath;
    }

    @Override
    public void open() {
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

    @Override
    public void close() {
        AppMain.fileService.saveConfig();
        stage.close();
        Utils.closeApp();
    }

    /**
     * Saves all unsaved data and closes the app
     */
    public void saveAndClose() {
        boolean success = true;
        if (AppMain.graphService.upperGraph.getMeasurement() != null) {
            if (AppMain.fileService.isAutoSave()) {
                success = AppMain.fileService.autoSaveMeasurement(AppMain.graphService.upperGraph.getMeasurement());
            } else {
                success = AppMain.fileService.saveAsMeasurement(AppMain.graphService.upperGraph.getMeasurement());
            }
        }
        if (AppMain.graphService.lowerGraph.getMeasurement() != null) {
            if (AppMain.fileService.isAutoSave()) {
                success = AppMain.fileService.autoSaveMeasurement(AppMain.graphService.lowerGraph.getMeasurement());
            } else {
                success = AppMain.fileService.saveAsMeasurement(AppMain.graphService.lowerGraph.getMeasurement());
            }
        }
        if (success) {
            AppMain.notificationService.createNotification("Data saved successfully.", NotificationType.SUCCESS);
            AppMain.fileService.saveConfig();
            close();
            Utils.closeApp();
        }
    }

}
