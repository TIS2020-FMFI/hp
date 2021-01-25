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
import java.io.FileNotFoundException;


public class Graph {
    private AnchorPane scene;
    private ChartViewer chartViewer;
    private GraphState state;
    private GraphType type;
    private Measurement measurement;
    private CustomChart chart;

    public Graph(Parent root, String fxmlId, GraphType type) {
        this.state = GraphState.EMPTY;
        this.type = type;
        this.chartViewer = createChartViewer();

        this.scene = (AnchorPane) root.lookup(fxmlId);
        if (this.scene == null) {
            AppMain.notificationService.createNotification("Graph could not be initialized -> pane with id '" + fxmlId + "' not found", NotificationType.ERROR);
        }
    }

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

    public Measurement getMeasurement() { return measurement; }
    public GraphState getState() { return state; }
    public GraphType getType() { return type; }

    public void setState(GraphState state) { this.state = state; }

    public void run() {
        state = GraphState.RUNNING;
        measurement = new Measurement(AppMain.environmentParameters.getActive());
        scene.getChildren().clear();
        scene.getChildren().add(chartViewer);
        chart = new CustomChart(measurement);
        chartViewer.setChart(chart.getChart());
    }

    public void load() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/main/resources"));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            state = GraphState.LOADED;
            measurement = AppMain.fileService.loadMeasurement(selectedFile.getPath());
            scene.getChildren().clear();
            scene.getChildren().add(chartViewer);
            chart = new CustomChart(measurement, true);
            chartViewer.setChart(chart.getChart());
        }
    }

    public void abort() {
        chart.abortMeasurement();
        measurement.setState(MeasurementState.ABORTED);
        scene.getChildren().clear();
        state = GraphState.EMPTY;
    }

}
