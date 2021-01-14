package com.app.service.graph;

import com.app.service.measurement.MeasurementState;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;

import java.io.File;


public class GraphService {
    private AnchorPane anchorPaneUpper;
    private AnchorPane anchorPaneLower;
    private GraphState stateUpper = GraphState.NOT_RUNNING;
    private GraphState stateLower = GraphState.NOT_RUNNING;
    private boolean running = false;
    private String loadingTo = null;
    private ChartViewer chartViewerUpper = new ChartViewer();
    private ChartViewer chartViewerLower = new ChartViewer();
    public Graph rtcpUpper = null;
    public Graph rtcpLower = null;


    public GraphService(Parent rootPrimary) {

        anchorPaneUpper = (AnchorPane) rootPrimary.lookup("#upperPane");
        anchorPaneLower = (AnchorPane) rootPrimary.lookup("#lowerPane");

        configChartViewer(chartViewerUpper);
        configChartViewer(chartViewerLower);

        anchorPaneUpper.getChildren().addAll(chartViewerUpper);
        anchorPaneLower.getChildren().addAll(chartViewerLower);
    }

    private void configChartViewer(ChartViewer chartViewer) {
        chartViewer.setPrefWidth(680);
        chartViewer.setPrefHeight(260);

        AnchorPane.setBottomAnchor(chartViewer, 0.0);
        AnchorPane.setLeftAnchor(chartViewer, 0.0);
        AnchorPane.setRightAnchor(chartViewer, 0.0);
        AnchorPane.setTopAnchor(chartViewer, 0.0);
    }

    public void setUpperRunning() { if (!isRunning()) stateUpper = GraphState.UPPER_RUNNING; }

    public void setLowerRunning() { if (!isRunning()) stateLower = GraphState.LOWER_RUNNING; }

    public boolean isUpperRunning() {
        return (stateUpper == GraphState.UPPER_RUNNING);
    }

    public boolean isLowerRunning() {
        return (stateLower == GraphState.LOWER_RUNNING);
    }

    public boolean isRunning(){
        return running;
    }

    public void setUpperLoaded() {
        if (stateUpper != GraphState.UPPER_RUNNING)  {
            stateUpper = GraphState.UPPER_LOADED;
            loadingTo = "upper";
        }
    }

    public void setLowerLoaded() {
        if (stateLower != GraphState.LOWER_RUNNING)  {
            stateLower = GraphState.LOWER_LOADED;
            loadingTo = "lower";
        }
    }

    public boolean isLowerLoaded(){ return (stateLower == GraphState.LOWER_LOADED); }

    public boolean isUpperLoaded(){ return (stateUpper == GraphState.UPPER_LOADED); }
    public void setStateUpper(GraphState state) {
        stateUpper = state;
    }
    public void setStateLower(GraphState state) {
        stateLower = state;
    }

    public boolean isLoaded(){
        return (stateUpper == GraphState.UPPER_LOADED || stateLower == GraphState.LOWER_LOADED);
    }

    public void createGraphRun(String XAxisName, String Yaxis1, String Yaxis2) throws Exception {
        if (!running) {
            running = true;
            if (stateUpper == GraphState.UPPER_RUNNING) {
                anchorPaneUpper.getChildren().clear();
                rtcpUpper = new Graph(Yaxis1, Yaxis2, XAxisName, running, null);
                chartViewerUpper.setChart(rtcpUpper.getChart());
            } else if (stateLower == GraphState.LOWER_RUNNING) {
                anchorPaneLower.getChildren().clear();
                rtcpLower = new Graph(Yaxis1, Yaxis2, XAxisName, running, null);
                chartViewerLower.setChart(rtcpLower.getChart());
            }
        }
    }

    public void LoadGraph() throws Exception {
        if (isLoaded() && loadingTo != null) {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("src/main/resources"));
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
            File selectedFile = fileChooser.showOpenDialog(new Stage());

            Graph rtcp = new Graph(" ", " ", " ", false, selectedFile);
            JFreeChart chart = rtcp.getChart();
            if (chart == null) {
                loadingTo = null;
                return;
            }
            if (stateUpper == GraphState.UPPER_LOADED && loadingTo.equals("upper")) {
                rtcpUpper = rtcp;
                chartViewerUpper.setChart(chart);
            }
            if (stateLower == GraphState.LOWER_LOADED && loadingTo.equals("lower")) {
                rtcpLower = rtcp;
                chartViewerLower.setChart(chart);
            }
            loadingTo = null;
        }
    }

    public void abortMeasurement() {
        if (isLowerRunning()) {
            rtcpLower.getMeasurement().setState(MeasurementState.ABORTED);
            stateLower = GraphState.NOT_RUNNING;
            rtcpLower = null;
        } else {
            rtcpUpper.getMeasurement().setState(MeasurementState.ABORTED);
            stateUpper = GraphState.NOT_RUNNING;
            rtcpUpper = null;
        }
    }



}
