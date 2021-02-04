package com.app.service.graph;

import com.app.service.AppMain;
import com.app.service.measurement.Measurement;
import com.app.service.measurement.MeasurementState;
import com.app.service.notification.NotificationType;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jfree.chart.fx.ChartViewer;

import java.io.File;
import java.util.MissingFormatArgumentException;


public class Graph {
    private AnchorPane scene;
    private ChartViewer chartViewer;
    private GraphState state;
    private GraphType type;
    private Measurement measurement;
    private CustomChart chart;

    /**
     * Constructor sets GraphState as EMPTY. Sets AnchorPane through linking it with java fxml element.
     * Creates notification if Graph could not be initialized.
     *
     * @param root
     * @param fxmlId
     * @param type
     */
    public Graph(Parent root, String fxmlId, GraphType type) {
        this.state = GraphState.EMPTY;
        this.type = type;
        this.chartViewer = createChartViewer();

        this.scene = (AnchorPane) root.lookup(fxmlId);
        if (this.scene == null) {
            AppMain.notificationService.createNotification("Graph could not be initialized -> pane with id '" + fxmlId + "' not found", NotificationType.ERROR);
        }
    }

    /**
     * Configures ChartViewer resolution and prepares AnchorPane for ChartViewer by setting Anchors to 0
     *
     * @return
     */
    private ChartViewer createChartViewer() {
        ChartViewer chartViewer = new ChartViewer();
        chartViewer.setPrefWidth(680);
        chartViewer.setPrefHeight(260);

        AnchorPane.setBottomAnchor(chartViewer, 0.0);
        AnchorPane.setLeftAnchor(chartViewer, 0.0);
        AnchorPane.setRightAnchor(chartViewer, 0.0);
        AnchorPane.setTopAnchor(chartViewer, 0.0);
        return chartViewer;
    }

    /**
     *
     * @return
     */
    public Measurement getMeasurement() { return measurement; }

    /**
     *
     * @return
     */
    public GraphState getState() { return state; }

    /**
     *
     * @return
     */
    public GraphType getType() { return type; }

    /**
     *
     * @param state
     */
    public void setState(GraphState state) { this.state = state; }

    /**
     * Runs the graph by setting Measurement, ChartViewer, AnchorPane and CustomChart.
     * Sets new CustomChart with loaded measurement.
     * Sets ChartViewer with CustomChart.
     * Clears AnchorPane and Adds ChartViewer to AnchorPane.
     */
    public void run() {
        state = GraphState.RUNNING;
        measurement = new Measurement(AppMain.environmentParameters.getActive());
        scene.getChildren().clear();
        scene.getChildren().add(chartViewer);
        chart = new CustomChart(measurement);
        chartViewer.setChart(chart.getChart());
    }

    /**
     * Loads graph by setting Measurement, ChartViewer, AnchorPane and CustomChart.
     * Lets user choose file that is afterwards loaded.
     * If user chooses file, measurement and graph are set.
     * Sets new CustomChart with loaded measurement.
     * Sets ChartViewer with CustomChart.
     * Clears AnchorPane and Adds ChartViewer to AnchorPane
     */
    public void load() throws MissingFormatArgumentException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("../"));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            measurement = AppMain.fileService.loadMeasurement(selectedFile.getPath());
            if (measurement != null) {
                state = GraphState.LOADED;
                scene.getChildren().clear();
                scene.getChildren().add(chartViewer);
                chart = new CustomChart(measurement, true);
                chartViewer.setChart(chart.getChart());
            }
        }
    }

    /**
     * Calls chart.abortMeasurement() to stop datasets' timers,
     * Sets MeasurementState as ABORTED,
     * Clears the javafx AnchorPane,
     * Sets GraphState as EMPTY -> makes it ready for another measurement
     */
    public void abort() {
        chart.abortMeasurement();
        measurement.setState(MeasurementState.ABORTED);
        scene.getChildren().clear();
        state = GraphState.EMPTY;
    }

}
