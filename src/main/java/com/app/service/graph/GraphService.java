package com.app.service.graph;

import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import org.jfree.chart.fx.ChartViewer;


public class GraphService {
    AnchorPane anchorPaneUpper;
    AnchorPane anchorPaneLower;
    boolean running = false;
    private String whichRunning = null;
    private Parent root;

    public GraphService(String path, Parent rootPrimary) throws Exception {
        root = rootPrimary;

        anchorPaneUpper = (AnchorPane) root.lookup("#upperPane");
        anchorPaneLower = (AnchorPane) root.lookup("#lowerPane");
    }

    public void setUpperRunning() {
        whichRunning = "upper";
    }

    public void setLowerRunning() {
        whichRunning = "lower";
    }

    public boolean isRunning(){
        return whichRunning == null;
    }

    public void createGraphRun() {
        if (running == false) {

            Graph rtcp = new Graph("Resistance", "Capacity", "Frequency", whichRunning);

            ChartViewer chartViewer = new ChartViewer(rtcp.chart);
            chartViewer.setPrefWidth(680);
            chartViewer.setPrefHeight(260);

            AnchorPane.setBottomAnchor(chartViewer, 0.0);
            AnchorPane.setLeftAnchor(chartViewer, 0.0);
            AnchorPane.setRightAnchor(chartViewer, 0.0);
            AnchorPane.setTopAnchor(chartViewer, 0.0);

            if (whichRunning == "upper") {
                anchorPaneUpper.getChildren().addAll(chartViewer);
            }
            if (whichRunning == "lower") {
                anchorPaneLower.getChildren().addAll(chartViewer);
            }
            running = true;
        }
    }

    public void LoadGraphRun() {
        if (running == false) {

            Graph rtcp = new Graph("Resistance", "Capacity", "Frequency", whichRunning);

            ChartViewer chartViewer = new ChartViewer(rtcp.chart);
            chartViewer.setPrefWidth(680);
            chartViewer.setPrefHeight(260);

            AnchorPane.setBottomAnchor(chartViewer, 0.0);
            AnchorPane.setLeftAnchor(chartViewer, 0.0);
            AnchorPane.setRightAnchor(chartViewer, 0.0);
            AnchorPane.setTopAnchor(chartViewer, 0.0);

            if (whichRunning == "upper") {
                anchorPaneUpper.getChildren().addAll(chartViewer);
            }
            if (whichRunning == "lower") {
                anchorPaneLower.getChildren().addAll(chartViewer);
            }
            running = true;
        }
    }

}
