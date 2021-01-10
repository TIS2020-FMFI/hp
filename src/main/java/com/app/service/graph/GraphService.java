package com.app.service.graph;

import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;

import java.io.File;

enum STATE {
    UPPER_RUNNING,
    LOWER_RUNNING,
    NOT_RUNNING,
    UPPER_LOADED,
    LOWER_LOADED
}

public class GraphService {
    private AnchorPane anchorPaneUpper;
    private AnchorPane anchorPaneLower;
    private STATE stateUpper = STATE.NOT_RUNNING;
    private STATE stateLower = STATE.NOT_RUNNING;
    private boolean running = false;
    private String loadingTo = null;
    private ChartViewer chartViewerUpper = new ChartViewer();
    private ChartViewer chartViewerLower = new ChartViewer();




    public GraphService(Parent rootPrimary) {

        anchorPaneUpper = (AnchorPane) rootPrimary.lookup("#upperPane");
        anchorPaneLower = (AnchorPane) rootPrimary.lookup("#lowerPane");

        configChartViewer(chartViewerUpper);
        configChartViewer(chartViewerLower);

        anchorPaneUpper.getChildren().addAll(chartViewerUpper);
        anchorPaneLower.getChildren().addAll(chartViewerLower);
    }

    public void configChartViewer(ChartViewer chartViewer) {
        chartViewer.setPrefWidth(680);
        chartViewer.setPrefHeight(260);

        AnchorPane.setBottomAnchor(chartViewer, 0.0);
        AnchorPane.setLeftAnchor(chartViewer, 0.0);
        AnchorPane.setRightAnchor(chartViewer, 0.0);
        AnchorPane.setTopAnchor(chartViewer, 0.0);
    }

    public void setUpperRunning() {
        if (!isRunning())  {
            stateUpper = STATE.UPPER_RUNNING;
        }
    }

    public void setLowerRunning() {
        if (!isRunning()) {
            stateLower = STATE.LOWER_RUNNING;
        }
    }

    public boolean isRunning(){
        return running;
    }

    public void setUpperLoaded() {
        if (stateUpper != STATE.UPPER_RUNNING)  {
            stateUpper = STATE.UPPER_LOADED;
            loadingTo = "upper";
        }
    }

    public void setLowerLoaded() {
        if (stateLower != STATE.LOWER_RUNNING)  {
            stateLower = STATE.LOWER_LOADED;
            loadingTo = "lower";
        }
    }

    public boolean isLoaded(){
        return (stateUpper == STATE.UPPER_LOADED || stateLower == STATE.LOWER_LOADED);
    }

    public void createGraphRun(String upperXAxisName, String lowerXAxisName) throws Exception {
        if (!running) {
            running = true;

            if (stateUpper == STATE.UPPER_RUNNING) {
                Graph rtcp = new Graph("Resistance", "Capacity", upperXAxisName, running, null);
                chartViewerUpper.setChart(rtcp.getChart());
            }
            if (stateLower == STATE.LOWER_RUNNING) {
                Graph rtcp = new Graph("Resistance", "Capacity", lowerXAxisName, running, null);
                chartViewerLower.setChart(rtcp.getChart());
            }
        }
    }

    public void LoadGraph() throws Exception {
        if (isLoaded() && loadingTo != null) {

            FileChooser fileChooser = new FileChooser();
            // fileChooser.setInitialDirectory(); for later
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
            File selectedFile = fileChooser.showOpenDialog(new Stage());


            Graph rtcp = new Graph("Resistance", "Capacity", "Frequency", false, selectedFile);
            JFreeChart chart = rtcp.getChart();
            if (chart == null) {
                loadingTo = null;
                return;
            }

            if (stateUpper == STATE.UPPER_LOADED && loadingTo.equals("upper")) {
                chartViewerUpper.setChart(chart);
            }
            if (stateLower == STATE.LOWER_LOADED && loadingTo.equals("lower")) {
                chartViewerLower.setChart(chart);
            }
            loadingTo = null;
        }
    }
}
