package com.app.service.graph;

import com.app.service.file.parameters.EnvironmentParameters;
import com.app.service.measurement.Measurement;
import com.app.service.measurement.MeasurementState;
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


public class CustomChart extends ChartPanel {
    private static boolean setAxisData = false;
    private static AutoUpdatingDataset series1;
    private static AutoUpdatingDataset series2;
    private static JFreeChart chart;

//    public static boolean running;

    public CustomChart(Measurement measurement) throws Exception {
        super(createChart(measurement));
    }
    public CustomChart(Measurement measurement, File file) {
        super(createChart(measurement, file));
    }

    public JFreeChart getChart() {
        return chart;
    }

    private static JFreeChart createChart(Measurement measurement, File file) {
        // TODO: load graph
        measurement.setState(MeasurementState.SAVED);
        measurement.setParameters(new EnvironmentParameters());
        return new JFreeChart(new XYPlot());
    }

    private static JFreeChart createChart(Measurement measurement) {
        series1 = new AutoUpdatingDataset(measurement, measurement.getParameters().getDisplayYY().getA(),100000, 100, 100,0);
        series2 = new AutoUpdatingDataset(measurement, measurement.getParameters().getDisplayYY().getB(),100000,100, 100,1);

        //construct the plot
        XYPlot plot = new XYPlot();
        plot.setDataset(0, series1);
        plot.setDataset(1, series2);

        //customize the plot with renderers and axis
        plot.setRenderer(0, new SamplingXYLineRenderer());//use default fill paint for first series
        SamplingXYLineRenderer splinerenderer = new SamplingXYLineRenderer();
        splinerenderer.setSeriesFillPaint(0, Color.BLUE);
        plot.setRenderer(1, splinerenderer);
        plot.setRangeAxis(0, new NumberAxis(measurement.getParameters().getDisplayYY().getA()));
        plot.setRangeAxis(1, new NumberAxis(measurement.getParameters().getDisplayYY().getB()));
        plot.setDomainAxis(new NumberAxis(measurement.getParameters().getDisplayYY().getX().toString()));

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

//        if (state.equals(GraphState.MEASURING)) {
//            series1.start();
//            series2.start();
//            return chart;
//        }
//        if (!running) { // loaded
//            if (data != null) {
//                addData(parseData(data));
//                return chart;
//            }
//        }
//        chart = null;
        return null;
    }

    public static ArrayList<ArrayList<Double>> parseData(File data) throws Exception {
        Scanner scanner = new Scanner(data);
        ArrayList<ArrayList<Double>> all_values = new ArrayList<ArrayList<Double>>();
        while (scanner.hasNext()) {
            try {
                ArrayList<Double> values_long = inputChange(scanner.nextLine());
                all_values.add(values_long);
            } catch (Exception e) {
                throw new Exception("Could not parse data");
            }
        }
        return all_values;
    }

    public static void addData(ArrayList<ArrayList<Double>> all_values) {
        final int COLUMN = 0;
        Comparator<ArrayList<Double>> myComparator = new Comparator<ArrayList<Double>>() {
            @Override
            public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
                return o1.get(COLUMN).compareTo(o2.get(COLUMN));
            }
        };
        Collections.sort(all_values, myComparator);

        for (int i = 0; i < all_values.size(); i++) {
            series1.addValue(all_values.get(i).get(0), all_values.get(i).get(1));
            series2.addValue(all_values.get(i).get(0), all_values.get(i).get(2));
        }
    }

    public static int findAxisEnd(String s) {
        int poc = 0;
        Character Char = s.charAt(poc);
        while (Char != '-' & !Character.isDigit(Char) & Char != ' ') {
            Char = s.charAt(poc);
            poc++;
        }
        return poc - 1;
    }

    public static void setAxesName(String[] values) {
        String Xaxis = values[0].substring(0,findAxisEnd(values[0]));
        String Yaxis1 = values[1].substring(0,findAxisEnd(values[1]));
        String Yaxis2 = values[2].substring(0,findAxisEnd(values[2]));
        chart.getXYPlot().getDomainAxis().setLabel(Xaxis);
        chart.getXYPlot().getRangeAxis(0).setLabel(Yaxis1);
        chart.getXYPlot().getRangeAxis(1).setLabel(Yaxis2);
    }

    public static ArrayList<Double> inputChange(String measurement) {
        String[] values = measurement.split(",");
        if (setAxisData == false) {
            setAxesName(values);
            setAxisData = true;
        }

        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].substring(findAxisEnd(values[i]), values[i].length());
        }

        ArrayList<Double> values_long = new ArrayList<Double>();
        values_long.add(Double.parseDouble(values[0])); // frequency / voltage
        values_long.add(Double.parseDouble(values[1]));
        values_long.add(Double.parseDouble(values[2]));

        return values_long;
    }

}
