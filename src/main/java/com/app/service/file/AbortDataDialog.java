package com.app.service.file;

import com.app.screen.controller.MainController;
import com.app.service.AppMain;
import com.app.service.graph.GraphState;
import com.app.service.graph.GraphType;
import com.app.service.measurement.Measurement;
import com.app.service.measurement.MeasurementState;
import com.app.service.notification.NotificationType;
import com.app.service.utils.Utils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class AbortDataDialog {
    private final String path;
    private GraphType graphType;
    private Stage stage;
    private MainController mainController;
    private boolean isRun;

    public AbortDataDialog(String controllerPath) {
        path = controllerPath;
    }

    public void openDialog(GraphType graphType, MainController mainController, boolean isRun) {
        this.graphType = graphType;
        this.mainController = mainController;
        this.isRun = isRun;
        try {
            stage = new Stage();

            Parent calibrationRoot = FXMLLoader.load(getClass().getResource(path));
            stage.setScene(new Scene(calibrationRoot));
            stage.setTitle("Abort measurement");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            AppMain.notificationService.createNotification("ignore unsaved data dialog failed to open -> " + e.getMessage(), NotificationType.ERROR);
        }
    }

    public void abortMeasurement() {
        AppMain.graphService.getGraphByType(graphType).abort();
        if (isRun) {
            if (graphType == GraphType.UPPER) {
                mainController.startUpperGraphMeasurement();
            } else {
                mainController.startLowerGraphMeasurement();
            }
        } else {
            if (graphType == GraphType.UPPER) {
                mainController.loadUpperGraph(null);
            } else {
                mainController.loadLowerGraph(null);
            }
        }
        close();
    }

    public void close() {
        stage.close();
    }

}
