package com.app.service.graph;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;

import java.awt.*;

public class Graph extends ChartPanel
{
    private long value=0;
    public static AutoUpdatingDataset series1;
    public static AutoUpdatingDataset series2 ;
    private static JFreeChart chart;

    public Graph(String yaxisName1, String yaxisName2, String xaxisName,boolean running)
    {
        super(createChart(yaxisName1,yaxisName2,xaxisName,running));
    }

    public JFreeChart getChart() {
        return chart;
    }

    private static JFreeChart createChart(String yaxisName1, String yaxisName2, String xaxisName, boolean running){ // ak no running, tak klasicky chart z dat, ktore poslem cez parameter

        series1 = new AutoUpdatingDataset(yaxisName1,100000, 400, 500);
        series2 = new AutoUpdatingDataset(yaxisName2,100000,400, 500);


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

        //configure the chart
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        plot.getDomainAxis().setAutoRange(true);
        plot.getRangeAxis(0).setAutoRange(true);
        plot.getRangeAxis(1).setAutoRange(true);
        plot.getDomainAxis().setFixedAutoRange(30);
        plot.getRangeAxis(0).setUpperMargin(0.1);
        plot.getRangeAxis(1).setUpperMargin(1.5);
        plot.getRangeAxis(0).setLabelPaint(Color.BLUE);
        plot.getRangeAxis(1).setLabelPaint(Color.RED);
        plot.setOutlinePaint(null);
        chart.setBackgroundPaint(null);
        chart.setBorderVisible(false);
        chart.removeLegend();

        if (running == true) {
            series1.start();
            series2.start();
        }
        if (running == false) { // teda ideme loadovat

        }

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
