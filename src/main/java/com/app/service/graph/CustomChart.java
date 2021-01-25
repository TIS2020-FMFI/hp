package com.app.service.graph;

import com.app.service.graph.dataset.AutoUpdatingDataset;
import com.app.service.graph.dataset.DatasetType;
import com.app.service.graph.dataset.StaticDataset;
import com.app.service.measurement.Measurement;
import com.app.service.measurement.SingleValue;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.data.xy.AbstractXYDataset;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class CustomChart extends ChartPanel {
    private static AbstractXYDataset series1;
    private static AbstractXYDataset series2;
    private static JFreeChart chart;

    public CustomChart(Measurement measurement, boolean isLoad) {
        super(loadChart(measurement));
    }
    public CustomChart(Measurement measurement) {
        super(createChart(measurement));
    }

    public JFreeChart getChart() {
        return chart;
    }

    private static JFreeChart loadChart(Measurement measurement) {
        series1 = new StaticDataset(measurement, DatasetType.LEFT);
        series2 = new StaticDataset(measurement, DatasetType.RIGHT);
        createChart(measurement.getParameters().getDisplayYY().getX().name(), measurement.getParameters().getDisplayYY().getA(), measurement.getParameters().getDisplayYY().getB());
        return chart;
    }

    private static JFreeChart createChart(Measurement measurement) {
        series1 = new AutoUpdatingDataset(measurement, DatasetType.LEFT);
        series2 = new AutoUpdatingDataset(measurement, DatasetType.RIGHT);
        createChart(measurement.getParameters().getDisplayYY().getX().name(), measurement.getParameters().getDisplayYY().getA(), measurement.getParameters().getDisplayYY().getB());

        AutoUpdatingDataset as1 = (AutoUpdatingDataset) series1;
        AutoUpdatingDataset as2 = (AutoUpdatingDataset) series2;
        as1.start();
        as2.start();
        return chart;
    }

    private static void createChart(String Xname, String Y1name, String Y2name) {
        //construct the plot
        XYPlot plot = new XYPlot();
        plot.setDataset(0, series1);
        plot.setDataset(1, series2);

        //customize the plot with renderers and axis
        plot.setRenderer(0, new SamplingXYLineRenderer());//use default fill paint for first series
        SamplingXYLineRenderer splinerenderer = new SamplingXYLineRenderer();
        splinerenderer.setSeriesFillPaint(0, Color.BLUE);
        plot.setRenderer(1, splinerenderer);
        plot.setRangeAxis(0, new NumberAxis(Y1name));
        plot.setRangeAxis(1, new NumberAxis(Y2name));
        plot.setDomainAxis(new NumberAxis(Xname));

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

    public void abortMeasurement() {
        AutoUpdatingDataset as1 = (AutoUpdatingDataset) series1;
        AutoUpdatingDataset as2 = (AutoUpdatingDataset) series2;
        as1.abortMeasurement();
        as2.abortMeasurement();
    }

}
