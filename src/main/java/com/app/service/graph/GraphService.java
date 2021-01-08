package com.app.service.graph;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.jfree.chart.fx.ChartViewer;


public class GraphService {
    AnchorPane anchorPane;
    Stage stage;
    int poc = 0;

    public GraphService(AnchorPane aP, Stage s) {
        anchorPane = aP;
        stage = s;
    }

    public void createGraphRun() {
        Graph rtcp = new Graph("Chart", "Resistance", "Capacity", "Frequency");

        ChartViewer chartViewer = new ChartViewer(rtcp.chart);
        chartViewer.setPrefWidth(680);
        chartViewer.setPrefHeight(260);

        AnchorPane.setBottomAnchor(chartViewer, 0.0);
        AnchorPane.setLeftAnchor(chartViewer, 0.0);
        AnchorPane.setRightAnchor(chartViewer, 0.0);
        AnchorPane.setTopAnchor(chartViewer, 0.0);

        anchorPane.getChildren().addAll(chartViewer);
    }
}
