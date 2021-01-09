package com.app.service.graph;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;


public class GraphService {
    AnchorPane anchorPaneUpper;
    AnchorPane anchorPaneLower;
    Button upperLoadButton;
    Button lowerLoadButton;
    private STATE stateUpper = STATE.NOTRUNNING;
    private STATE stateLower = STATE.NOTRUNNING;
    boolean running = false;
    String loadingTo = null;
    ChartViewer chartViewerUpper = new ChartViewer();
    ChartViewer chartViewerLower = new ChartViewer();


    public enum STATE {
        UPPERRUNNING,
        LOWERRUNNING,
        NOTRUNNING,
        UPPERLOADED,
        LOWERLOADED
    }

    @FXML
    javafx.scene.control.Button lowerGraphLoad;
    @FXML
    javafx.scene.control.Button upperGraphLoad;


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
            stateUpper = STATE.UPPERRUNNING;
        }
    }

    public void setLowerRunning() {
        if (!isRunning()) {
            stateLower = STATE.LOWERRUNNING;
        }
    }

    public boolean isRunning(){
        return running;
    }

    public void setUpperLoaded() {
        if (stateUpper != STATE.UPPERRUNNING)  {
            stateUpper = STATE.UPPERLOADED;
            loadingTo = "upper";
        }
    }

    public void setLowerLoaded() {
        if (stateLower != STATE.LOWERRUNNING)  {
            stateLower = STATE.LOWERLOADED;
            loadingTo = "lower";
        }
    }

    public boolean isLoaded(){
        return (stateUpper == STATE.UPPERLOADED || stateLower == STATE.LOWERLOADED);
    }

    public void createGraphRun(String upperXAxisName, String lowerXAxisName) throws FileNotFoundException {
        if (!running) {
            running = true;

            if (stateUpper == STATE.UPPERRUNNING) {
                Graph rtcp = new Graph("Resistance", "Capacity", upperXAxisName, running, null);
                chartViewerUpper.setChart(rtcp.getChart());
            }
            if (stateLower == STATE.LOWERRUNNING) {
                Graph rtcp = new Graph("Resistance", "Capacity", lowerXAxisName, running, null);
                chartViewerLower.setChart(rtcp.getChart());
            }
        }
    }

    public void LoadGraph() throws FileNotFoundException {
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

            if (stateUpper == STATE.UPPERLOADED && loadingTo.equals("upper")) {
                chartViewerUpper.setChart(chart);
            }
            if (stateLower == STATE.LOWERLOADED && loadingTo.equals("lower")) {
                chartViewerLower.setChart(chart);
            }
            loadingTo = null;
        }
    }
}
