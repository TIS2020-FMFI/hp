package com.app.service.graph;

import com.app.service.AppMain;
import com.app.service.notification.NotificationType;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;

import java.io.IOException;


public class GraphService {
    public Graph upperGraph;
    public Graph lowerGraph;

    /**
     *
     */
    public GraphService() {}

    /**
     *
     * @param type
     * @return
     */
    public Graph getGraphByType(GraphType type) {
        return type.equals(GraphType.UPPER) ? upperGraph:lowerGraph;
    }

    /**
     *
     * @param type
     * @return
     */
    public GraphState getStateByType(GraphType type) {
        return type.equals(GraphType.UPPER) ? upperGraph.getState():lowerGraph.getState();
    }

    /**
     *
     * @return
     */
    public boolean isRunningGraph() {
        if (upperGraph != null && lowerGraph != null) {
            return upperGraph.getState().equals(GraphState.RUNNING) || lowerGraph.getState().equals(GraphState.RUNNING);
        }
        return false;
    }

    /**
     *
     * @param type
     * @return
     */
    public boolean isLoadedGraph(GraphType type) {
        return type.equals(GraphType.UPPER) ? upperGraph.getState().equals(GraphState.LOADED) : lowerGraph.getState().equals(GraphState.LOADED);
    }

    /**
     *
     * @return
     */
    public Graph getRunningGraph() {
        if (upperGraph != null && lowerGraph != null && isRunningGraph()) {
            return upperGraph.getState().equals(GraphState.RUNNING) ? upperGraph : lowerGraph;
        }
        return null;
    }

    /**
     * Sets upperGraph and upperGraph by constructing Graph with appropriate fmxl ids(from AnchorPanes)
     * @param root
     */
    public void setRoot(Parent root) {
        upperGraph = new Graph(root, "#upperPane", GraphType.UPPER);
        lowerGraph = new Graph(root, "#lowerPane", GraphType.LOWER);
    }

    /**
     * Starts running Graph, which is got from getGraphByType(type), by calling Graph.run().
     * Runs the measurement.
     * Sends notification if error occurs during measurement.
     *
     * @param type
     */
    public void run(GraphType type) {
        try {
            getGraphByType(type).run();
            AppMain.communicationService.runMeasurement(getRunningGraph().getMeasurement());
        } catch (Exception e) {
            AppMain.notificationService.createNotification("Error occurred while running measurement -> " + e.getMessage(), NotificationType.ERROR);
        }
    }

    /**
     * Sends to CommunicationService notification that machine should do one next step of measurement.
     * If IOException occurs during this, notifies that error occurred.
     */
    public void runNextStep(TextArea currentValueDisplay) {
        try {
            AppMain.communicationService.nextStep(getRunningGraph().getMeasurement());
//            currentValueDisplay.setText(String.valueOf(getRunningGraph().getMeasurement().getData().get(getRunningGraph().getMeasurement().getData().size()-1)));
        } catch (IOException | InterruptedException e) {
            AppMain.notificationService.createNotification("Error occurred while running next step -> " + e.getMessage(), NotificationType.ERROR);
        }
    }

    /**
     * Loads Graph, which is got from getGraphByType(type), by calling Graph.load().
     * Sends notification if error occurs during loading.
     *
     * @param type
     */
    public void loadGraph(GraphType type) {
        try {
            getGraphByType(type).load();
        } catch (NumberFormatException e) {
            AppMain.notificationService.createNotification("Could not parse loaded data -> " + e.getMessage(), NotificationType.ERROR);
        } catch (Exception e) {
            AppMain.notificationService.createNotification("Error occurred while loading measurement -> " + e.getMessage(), NotificationType.ERROR);
        }
    }

    /**
     * Aborts the measurement by sending appropriate command to machine, cancels dataset timers and destroys graph.
     * If error occurs, notifies.
     *
     * @param type
     */
    public void abortGraph(GraphType type) {
        try {
            AppMain.communicationService.abortMeasurement();
            getGraphByType(type).abort();
        } catch (Exception e) {
            AppMain.notificationService.createNotification("Error occurred while aborting measurement -> " + e.getMessage(), NotificationType.ERROR);
        }
    }

}
