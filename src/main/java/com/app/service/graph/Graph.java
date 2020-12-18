package com.app.service.graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;


public class Graph extends ChartPanel implements Runnable
{
    private static TimeSeries timeSeries;
    private long value=0;

    public Graph(String chartContent,String title,String yaxisName)
    {
        super(createChart(chartContent,title,yaxisName));
    }

    private static JFreeChart createChart(String chartContent,String title,String yaxisName){

        timeSeries = new TimeSeries(chartContent);
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection(timeSeries);
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(title, "time (seconds)", yaxisName, timeseriescollection, true, true, false);
        XYPlot xyplot = jfreechart.getXYPlot();

        ValueAxis valueaxis = xyplot.getDomainAxis();
        valueaxis.setAutoRange(true);
        valueaxis.setFixedAutoRange(30000D);

        valueaxis = xyplot.getRangeAxis();
        //valueaxis.setRange(0.0D,200D);

        return jfreechart;
    }

    public void run()
    {
        while(true)
        {
            try
            {
                timeSeries.add(new Millisecond(), randomNum());  // tu budu pribudat values
                Thread.sleep(300);
            }
            catch (InterruptedException e)  {   }
        }
    }

    private long randomNum()
    {
        System.out.println((Math.random()*20+80));
        return (long)(Math.random()*20+80);
    }
}
