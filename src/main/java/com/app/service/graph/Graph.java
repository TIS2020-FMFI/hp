package com.app.service.graph;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;


public class Graph extends ChartPanel implements Runnable
{
    private long value=0;
    public static XYSeries series1;
    public static XYSeries series2 ;
    private static XYSeriesCollection dataset1;
    private static XYSeriesCollection dataset2;
    public static JFreeChart chart;

    public Graph(String title, String yaxisName1, String yaxisName2, String xaxisName)
    {
        super(createChart(title,yaxisName1,yaxisName2,xaxisName));
    }

    private static JFreeChart createChart(String title, String yaxisName1, String yaxisName2, String xaxisName){



        series1 = new XYSeries(yaxisName1);
        series2 = new XYSeries(yaxisName2);

        dataset1 = new XYSeriesCollection(series1);
        dataset2 = new XYSeriesCollection(series2);


        //construct the plot
        XYPlot plot = new XYPlot();
        plot.setDataset(0, dataset1);
        plot.setDataset(1, dataset2);

        //customize the plot with renderers and axis
        plot.setRenderer(0, new XYSplineRenderer());//use default fill paint for first series
        XYSplineRenderer splinerenderer = new XYSplineRenderer();
        splinerenderer.setSeriesFillPaint(0, Color.BLUE);
        plot.setRenderer(1, splinerenderer);
        plot.setRangeAxis(0, new NumberAxis(yaxisName1));
        plot.setRangeAxis(1, new NumberAxis(yaxisName2));
        plot.setDomainAxis(new NumberAxis(xaxisName));

        //Map the data to the appropriate axis
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);

        //generate the chart
        chart = new JFreeChart(title,plot);
        chart.setBackgroundPaint(Color.WHITE);
        JPanel chartPanel = new ChartPanel(chart);


        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        plot.getDomainAxis().setLowerBound(0);
        plot.getDomainAxis().setAutoRange(true);
        plot.getRangeAxis(0).setAutoRange(true);
        plot.getRangeAxis(1).setAutoRange(true);
        plot.getDomainAxis().setFixedAutoRange(30D);
//        plot.getDomainAxis().setLowerBound(0);
//        plot.getRangeAxis(0).setFixedAutoRange(50);
//        plot.getRangeAxis(1).setFixedAutoRange(50);
        plot.getRangeAxis(0).setUpperMargin(0.1);
        plot.getRangeAxis(1).setUpperMargin(1.5);
//        plot.getRangeAxis(0).setLowerMargin(0.1);
//        plot.getRangeAxis(0).set
//        plot.getRangeAxis(0).setLowerMargin(1);
//        plot.getRangeAxis(0).setDefaultAutoRange(new Range(50,110));

        return chart;
    }

    public void run()
    {
        double poc = 1;
        while(true)
        {
            try {

                //inputChange("String measurement"); //

                series1.add(poc, (double) randomNum());
                series2.add(poc, (double) randomNum());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            } finally {
                poc += 1;
            }

        }
    }

    private long randomNum()
    {
        System.out.println((Math.random()*20+80));
        return (long) (Math.random()*20+80);
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
