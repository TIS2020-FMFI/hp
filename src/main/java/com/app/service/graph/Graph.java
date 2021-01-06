package com.app.service.graph;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class Graph extends ChartPanel
{
    private long value=0;
    public static AutoUpdatingDataset series1;
    public static AutoUpdatingDataset series2 ;
    public static JFreeChart chart;

    public Graph(String title, String yaxisName1, String yaxisName2, String xaxisName)
    {
        super(createChart(title,yaxisName1,yaxisName2,xaxisName));
    }

    private static JFreeChart createChart(String title, String yaxisName1, String yaxisName2, String xaxisName){

        series1 = new AutoUpdatingDataset(yaxisName1,100000, 400, 1000);
        series2 = new AutoUpdatingDataset(yaxisName2,100000,400, 500);

        series2.setDelay(250, 1000000000);


        //construct the plot
        XYPlot plot = new XYPlot();
        plot.setDataset(0, series1);
        plot.setDataset(1, series2);

        //customize the plot with renderers and axis
        plot.setRenderer(0, new SamplingXYLineRenderer());//use default fill paint for first series
        SamplingXYLineRenderer splinerenderer = new  SamplingXYLineRenderer();
        splinerenderer.setSeriesFillPaint(0, Color.BLUE);
        plot.setRenderer(1, splinerenderer);
        plot.setRangeAxis(0, new NumberAxis(yaxisName1));
        plot.setRangeAxis(1, new NumberAxis(yaxisName2));
        plot.setDomainAxis(new NumberAxis(xaxisName));

        //Map the data to the appropriate axis
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);

        //generate the chart
        chart = new JFreeChart(plot);
//        chart.setBackgroundPaint(Color.WHITE);

        //configure the chart
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
//        plot.getDomainAxis().setLowerBound(0);
        plot.getDomainAxis().setAutoRange(true);
        plot.getRangeAxis(0).setAutoRange(true);
        plot.getRangeAxis(1).setAutoRange(true);
        plot.getDomainAxis().setFixedAutoRange(30);
//        plot.getDomainAxis().setLowerBound(0);
//        plot.getRangeAxis(0).setFixedAutoRange(50);
//        plot.getRangeAxis(1).setFixedAutoRange(50);
        plot.getRangeAxis(0).setUpperMargin(0.1);
        plot.getRangeAxis(1).setUpperMargin(1.5);
        plot.setOutlinePaint(null);
        chart.setBackgroundPaint(null);
        chart.setBorderVisible(false);
        chart.getLegend(0).setItemPaint(Color.BLUE);
        chart.removeLegend();

//        plot.getRangeAxis(0).setLowerMargin(0.1);
//        plot.getRangeAxis(0).setLowerMargin(1);
//        plot.getRangeAxis(0).setDefaultAutoRange(new Range(50,110));
        series1.start();
        series2.start();

        return chart;
    }

    private Double[] inputChange(String measurement) {

        measurement = "F 0500.0000,NZN 028.55E+00,NDN-089.77E+00";
        String[] values = measurement.split(",");
        for (int i = 0; i < values.length; i++) {
            if (i == 0) values[i] = values[i].substring(1,values[i].length());
            else values[i] = values[i].substring(3,values[i].length());
        }

        Double[] values_long = new Double[3];
        values_long[0] = Double.parseDouble(values[0]);
        values_long[1] = Double.parseDouble(values[1]);
        values_long[2] = Double.parseDouble(values[2]);

        return values_long;
    }
}
