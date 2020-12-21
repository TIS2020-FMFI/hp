package com.app.service.graph;

import javafx.embed.swing.SwingNode;
import javafx.scene.layout.AnchorPane;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GraphService {
    AnchorPane anchorPane;

    public GraphService(AnchorPane aP) {
        anchorPane = aP;
    }

    public void createGraphRun() {
        Graph rtcp = new Graph("Chart", "Resistance", "Capacity", "Frequency");

        ChartPanel chartPanel = new ChartPanel(rtcp.chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(680, 260));
        chartPanel.setRangeZoomable(true);
        chartPanel.setDomainZoomable(true);

        final SwingNode swingNode = new SwingNode();
        swingNode.setContent(chartPanel);
        anchorPane.getChildren().addAll(swingNode);

        // real-time plotting
        final double[] poc = {0};
        //now make your timer
        int delay = 500; //milliseconds

        ActionListener timerAction = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //add new data point and actualize graph
                rtcp.series1.add(poc[0], (double) (Math.random() * 20 + 80));  // tu budu pribudat values
                rtcp.series2.add(poc[0], (double) (Math.random() * 20 + 80));  // tu budu pribudat values
                poc[0]++;

            }
        };
        new Timer(delay, timerAction).start();

        // da sa to robit aj mimo awt, ale error NoSuchMethod nezmizne
//        TimerTask task = new TimerTask() {
//            public void run() {
//                    rtcp.series1.add(poc[0], (double) (Math.random() * 20 + 80));  // tu budu pribudat values
//                    rtcp.series2.add(poc[0], (double) (Math.random() * 20 + 80));  // tu budu pribudat values
//                    poc[0]++;
//                };
//        Timer timer = new Timer("Timer");
//        int delay = 500;
//        timer.schedule(task, delay);
    }
}
