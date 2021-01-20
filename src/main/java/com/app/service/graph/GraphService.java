package com.app.service.graph;

import com.app.service.AppMain;
import com.app.service.measurement.MeasurementState;
import com.app.service.notification.NotificationType;
import javafx.scene.Parent;

import java.io.FileNotFoundException;


public class GraphService {
    public Graph upperGraph;
    public Graph lowerGraph;

    public GraphService() {}

    public Graph getGraph(GraphType type) {
        return type.equals(GraphType.UPPER) ? upperGraph:lowerGraph;
    }

    public boolean isRunningGraph() {
        if (upperGraph != null && lowerGraph != null) {
            return upperGraph.getState().equals(GraphState.RUNNING) || lowerGraph.getState().equals(GraphState.RUNNING);
        }
        return false;
    }

    public boolean isLoadedGraph(GraphType type) {
        return type.equals(GraphType.UPPER) ? upperGraph.getState().equals(GraphState.LOADED) : lowerGraph.getState().equals(GraphState.LOADED);
    }

    public Graph getRunningGraph() {
        if (upperGraph != null && lowerGraph != null && isRunningGraph()) {
            return upperGraph.getState().equals(GraphState.RUNNING) ? upperGraph : lowerGraph;
        }
        return null;
    }

    public void setRoot(Parent root) {
        upperGraph = new Graph(root, "#upperPane", GraphType.UPPER);
        lowerGraph = new Graph(root, "#lowerPane", GraphType.LOWER);
    }

    public void run(GraphType type) {
        try {
            getGraph(type).run();
        } catch (Exception e) {
            AppMain.notificationService.createNotification("Error occurred while running measurement -> " + e.getMessage(), NotificationType.ERROR);
        }
    }

    public void loadGraph(GraphType type) {
        try {
            getGraph(type).load();
        } catch (FileNotFoundException e) {
            AppMain.notificationService.createNotification("File you are trying to load does not exist", NotificationType.ERROR);
        } catch (NumberFormatException e) {
            AppMain.notificationService.createNotification("Could not parse loaded data -> " + e.getMessage(), NotificationType.ERROR);
        } catch (Exception e) {
            AppMain.notificationService.createNotification("Error occurred while loading measurement -> " + e.getMessage(), NotificationType.ERROR);
        }
    }

    public void abortGraph(GraphType type) {
        try {
            getGraph(type).abort();
        } catch (Exception e) {
            AppMain.notificationService.createNotification("Error occurred while aborting measurement -> " + e.getMessage(), NotificationType.ERROR);
        }
    }

    public boolean measurementSaved(GraphType type) {
        Graph temp = getGraph(type);
        if (temp.getState().equals(GraphState.EMPTY) || temp.getState().equals(GraphState.RUNNING)) {
            return true;
        }
        return !temp.getMeasurement().getState().equals(MeasurementState.WAITING) && !temp.getMeasurement().getState().equals(MeasurementState.STARTED);
    }

}
