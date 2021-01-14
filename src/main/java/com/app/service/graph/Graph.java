package com.app.service.graph;

import com.app.service.AppMain;
import com.app.service.file.parameters.EnvironmentParameters;
import com.app.service.measurement.Measurement;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Graph extends ChartPanel
{
    private static boolean setAxisData= false;
    private static AutoUpdatingDataset series1;
    private static AutoUpdatingDataset series2 ;
    private static JFreeChart chart;
    private static Measurement measurement;

    public Graph(String yaxisName1, String yaxisName2, String xaxisName,boolean running, File data) throws Exception {
        super(createChart(yaxisName1,yaxisName2,xaxisName,running, data));
    }

    public JFreeChart getChart() {
        return chart;
    }

    private static JFreeChart createChart(String yaxisName1, String yaxisName2, String xaxisName, boolean running, File data ) throws Exception { // ak no running, tak klasicky chart z dat, ktore poslem cez parameter
        measurement = new Measurement(AppMain.environmentParameters);

        series1 = new AutoUpdatingDataset(measurement, yaxisName1,100000, 400, 500,0);
        series2 = new AutoUpdatingDataset(measurement, yaxisName2,100000,400, 500,1);

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

        if (running) {
            series1.start();
            series2.start();
            return chart ;
        }
        if (!running) {
            if (data != null) {
                parseAndAddData(data);
                return chart ;
            }
        }
        chart = null;
        return null;
    }

    public static void  parseAndAddData(File data) throws Exception {
        Scanner scanner = new Scanner(data);
        ArrayList<ArrayList<Double>> all_values = new ArrayList<ArrayList<Double>>();
        while(scanner.hasNext()){
            try {
                ArrayList<Double> values_long = inputChange(scanner.nextLine());
                all_values.add(values_long);
            } catch (Exception e) {
                throw new Exception("Could not parse data");
            }
        }

        final int COLUMN = 0;
        Comparator<ArrayList<Double>> myComparator = new Comparator<ArrayList<Double>>() {
            @Override
            public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
                return o1.get(COLUMN).compareTo(o2.get(COLUMN));
            }
        };
        Collections.sort(all_values, myComparator);

        for (int i = 0; i < all_values.size(); i++) {
            series1.addValue(all_values.get(i).get(0),all_values.get(i).get(1));
            series2.addValue(all_values.get(i).get(0),all_values.get(i).get(2));
        }
    }
    public static int findAxisEnd(String s) {
        int poc = 0;
        Character Char = s.charAt(poc);;
        while (Char != '-' & !Character.isDigit(Char) & Char != ' ') {
            Char = s.charAt(poc);
            poc++;
        }
        return poc-1;
    }
    public static ArrayList<Double> inputChange(String measurement) {


        String[] values = measurement.split(",");
        if (setAxisData == false) {
            String Xaxis = values[0].substring(0,findAxisEnd(values[0]));
            String Yaxis1 = values[1].substring(0,findAxisEnd(values[1]));
            String Yaxis2 = values[2].substring(0,findAxisEnd(values[2]));
            setAxisData = true;
            chart.getXYPlot().getDomainAxis().setLabel(Xaxis);
            chart.getXYPlot().getRangeAxis(0).setLabel(Yaxis1);
            chart.getXYPlot().getRangeAxis(1).setLabel(Yaxis2);

        }

        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].substring(findAxisEnd(values[i]),values[i].length());
        }

        ArrayList<Double> values_long = new ArrayList<Double>();
        values_long.add(Double.parseDouble(values[0])); // frequency / voltage
        values_long.add(Double.parseDouble(values[1]));
        values_long.add(Double.parseDouble(values[2]));

        return values_long;
    }


}
