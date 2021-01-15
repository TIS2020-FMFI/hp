package com.app.service.graph;

import com.app.service.file.parameters.EnvironmentParameters;
import com.app.service.measurement.Measurement;
import com.app.service.measurement.MeasurementState;
import com.app.service.measurement.SingleValue;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;


public class CustomChart extends ChartPanel {
    private static AutoUpdatingDataset series1;
    private static AutoUpdatingDataset series2;
    private static JFreeChart chart;

    public CustomChart(Measurement measurement) {
        super(createChart(measurement));
    }
    public CustomChart(Measurement measurement, File file) throws FileNotFoundException {
        super(createChart(measurement, file));
    }

    public JFreeChart getChart() {
        return chart;
    }

    private static JFreeChart createChart(Measurement measurement, File file) throws FileNotFoundException {
        measurement.setState(MeasurementState.SAVED);
        measurement.setParameters(new EnvironmentParameters()); // TODO: parseMeasurementSettings and set envParams accordingly
        measurement.getData().addAll(parseMeasurement(file));
        chart = new JFreeChart(new XYPlot());

        chart.getXYPlot().getDomainAxis().setLabel(measurement.getParameters().getDisplayYY().getX().toString());
        chart.getXYPlot().getRangeAxis(0).setLabel(measurement.getParameters().getDisplayYY().getA());
        chart.getXYPlot().getRangeAxis(1).setLabel(measurement.getParameters().getDisplayYY().getB());
        return chart;
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

        return chart;
    }

    private static List<SingleValue> parseMeasurement(File file) throws FileNotFoundException, NumberFormatException {
        Scanner scanner = new Scanner(file);
        List<SingleValue> data = new ArrayList<>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            try {
                data.add(new SingleValue(line));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Could not create Single value from input: " + line);
            }
        }
        return data;
    }

    public static void addData(ArrayList<ArrayList<Double>> all_values) {
        final int COLUMN = 0;
        Comparator<ArrayList<Double>> myComparator = Comparator.comparing(o -> o.get(COLUMN));
        all_values.sort(myComparator);

        for (ArrayList<Double> all_value : all_values) {
            series1.addValue(all_value.get(0), all_value.get(1));
            series2.addValue(all_value.get(0), all_value.get(2));
        }
    }

}
