package com.app.service.graph;

import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import org.jfree.chart.fx.ChartViewer;


public class GraphService {
    AnchorPane anchorPaneUpper;
    AnchorPane anchorPaneLower;
    private STATE stateUpper = STATE.NOTRUNNING;
    private STATE stateLower = STATE.NOTRUNNING;
    private Parent root;
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


    public GraphService(Parent rootPrimary) {
        root = rootPrimary;

        anchorPaneUpper = (AnchorPane) root.lookup("#upperPane");
        anchorPaneLower = (AnchorPane) root.lookup("#lowerPane");

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

    public void createGraphRun() throws Exception {
        if (running == false) {
            running = true;
            Graph rtcp = new Graph("Resistance", "Capacity", "Frequency", running);
            if (stateUpper == STATE.UPPERRUNNING) {
                chartViewerUpper.setChart(rtcp.getChart());
            }
            if (stateLower == STATE.LOWERRUNNING) {
                chartViewerLower.setChart(rtcp.getChart());
            }
        }
    }

    public void LoadGraph() {
        if (isLoaded() && loadingTo != null) {

            Graph rtcp = new Graph("Resistance", "Capacity", "Frequency", false);

            if (stateUpper == STATE.UPPERLOADED && loadingTo == "upper") {
                chartViewerUpper.setChart(rtcp.getChart());
            }
            if (stateLower == STATE.LOWERLOADED && loadingTo == "lower") {
                chartViewerLower.setChart(rtcp.getChart());
            }
            loadingTo = null;
        }
    }
}
