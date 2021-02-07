package com.app.service.measurement;

import com.app.screen.controller.MainController;
import com.app.service.AppMain;
import com.app.service.graph.GraphType;
import com.app.service.notification.NotificationType;
import com.app.service.Window;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Window that's triggered when replacing unsaved data in a certain graph type
 */
public class AbortDataWindow implements Window {
    private final String path;
    private GraphType graphType;
    private Stage stage;
    private MainController mainController;
    private boolean isRun;

    /**
     * Initializes abort data window
     *
     * @param controllerPath path to the view
     */
    public AbortDataWindow(String controllerPath) {
        path = controllerPath;
    }

    @Override
    public void open() {
        stage = new Stage();
        try {
            Parent calibrationRoot = FXMLLoader.load(getClass().getResource(path));
            stage.setScene(new Scene(calibrationRoot));
            stage.setTitle("Abort measurement");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            AppMain.notificationService.createNotification("Ignore unsaved data popup failed to open -> " + e.getMessage(), NotificationType.ERROR);
        }
    }

    /**
     * Opens window according to graphType from specified controller with switch parameter
     *
     * @param graphType type of the graph to be handled
     * @param mainController controller that triggers the window
     * @param isRun switch to differentiate between trigger type
     */
    public void open(GraphType graphType, MainController mainController, boolean isRun) {
        this.graphType = graphType;
        this.mainController = mainController;
        this.isRun = isRun;
       open();
    }

    @Override
    public void close() {
        stage.close();
    }

    /**
     * Ignores unsaved data and discards them
     */
    public void abort() {
        AppMain.graphService.getGraphByType(graphType).abort();
        if (isRun) {
            if (graphType == GraphType.UPPER) {
                mainController.runUpperGraph(null);
            } else {
                mainController.runLowerGraph(null);
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
}
