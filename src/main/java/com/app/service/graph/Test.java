package com.app.service.graph;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import org.jfree.chart.fx.ChartViewer;

public class Test
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        JFrame frame=new JFrame("Chart");
        Graph rtcp=new Graph("Chart", "Resistance", "Capacity", "Frequency");
        ChartViewer chartViewer = new ChartViewer(rtcp.chart);
        chartViewer.setPrefHeight(400);
        chartViewer.setPrefWidth(400);
        frame.getContentPane().add(rtcp,new BorderLayout().CENTER);
        frame.pack();
        frame.setVisible(true);

//        (new Thread(rtcp)).start(); 1st version

        // real-time plotting
        final double[] poc = {0};
        //now make your timer
        int delay = 500; //milliseconds
        ActionListener timerAction = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //add new data point and actualize graph
                rtcp.series1.add(poc[0], (double) (Math.random()*20+80));  // tu budu pribudat values
                rtcp.series2.add(poc[0], (double) (Math.random()*10+40));  // tu budu pribudat values
                poc[0]++;

            }
        };
        new Timer(delay, timerAction).start();

        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent windowevent)
            {
                System.exit(0);
            }

        });
    }
}