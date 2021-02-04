package com.app.service.graph;

import com.app.service.graph.dataset.AutoUpdatingDataset;
import com.app.service.graph.dataset.DatasetType;
import com.app.service.graph.dataset.StaticDataset;
import com.app.service.measurement.Measurement;
import com.app.service.measurement.MeasurementState;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.AbstractXYDataset;

import java.awt.*;
import java.text.DecimalFormat;


public class CustomChart extends ChartPanel {
    private static AbstractXYDataset series1;
    private static AbstractXYDataset series2;
    private static JFreeChart chart;

    /**
     * Constructor for loading chart, calls loadChart(Measurement measurement) with its parameter
     *
     * @param measurement
     * @param isLoad
     */
    public CustomChart(Measurement measurement, boolean isLoad) {
        super(loadChart(measurement));
    }

    /**
     * Constructor for running chart, calls createChart(Measurement measurement) with its parameter
     *
     * @param measurement
     */
    public CustomChart(Measurement measurement) {
        super(createChart(measurement));
    }

    /**
     * creates datasets for chart, calls createChart(String Xname, String Y1name, String Y2name)
     * with measurement parameters (from already loaded file into measurement)
     *
     * @param measurement
     * @return JFreeChart
     */
    private static JFreeChart loadChart(Measurement measurement) {
        series1 = new StaticDataset(measurement, DatasetType.LEFT);
        series2 = new StaticDataset(measurement, DatasetType.RIGHT);
        measurement.setState(MeasurementState.LOADED);
        createChart(measurement.getParameters().getDisplayYY().getX().name(), measurement.getParameters().getDisplayYY().getA(), measurement.getParameters().getDisplayYY().getB());

        StaticDataset st1 = (StaticDataset) series1;
        StaticDataset st2 = (StaticDataset) series2;
        st1.start();
        st2.start();
        return chart;
    }

    /**
     * creates datasets for chart, calls createChart(String Xname, String Y1name, String Y2name)
     * with measurement parameters, starts timer inside datasets
     *
     * @param measurement
     * @return JFreeChart
     */
    private static JFreeChart createChart(Measurement measurement) {
        series1 = new AutoUpdatingDataset(measurement, DatasetType.LEFT);
        series2 = new AutoUpdatingDataset(measurement, DatasetType.RIGHT);
        measurement.setState(MeasurementState.STARTED);
        createChart(measurement.getParameters().getDisplayYY().getX().name(), measurement.getParameters().getDisplayYY().getA(), measurement.getParameters().getDisplayYY().getB());

        AutoUpdatingDataset as1 = (AutoUpdatingDataset) series1;
        AutoUpdatingDataset as2 = (AutoUpdatingDataset) series2;
        as1.start();
        as2.start();
        return chart;
    }

    /**
     * Creates pannable plot with automatic ranging with 2 y axis and 1 x axis and adds it to chart variable
     *
     * @param Xname
     * @param Y1name
     * @param Y2name
     */
    private static void createChart(String Xname, String Y2name, String Y1name) {
        //construct the plot
        XYPlot plot = new XYPlot();
        plot.setDataset(0, series2);
        plot.setDataset(1, series1);

        //customize the plot with renderers and axis
        XYSplineRenderer splinerenderer1 = new XYSplineRenderer();
//        splinerenderer1.setSeriesItemLabelsVisible(0,true);
        plot.setRenderer(0, splinerenderer1);
//        splinerenderer1.setAutoPopulateSeriesFillPaint(true);
//        splinerenderer1.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator());

        XYSplineRenderer splinerenderer0 = new XYSplineRenderer();
        splinerenderer0.setSeriesFillPaint(1, Color.BLUE);
//        splinerenderer0.setSeriesItemLabelsVisible(0,true);
        plot.setRenderer(1, splinerenderer0);
//        splinerenderer0.setAutoPopulateSeriesFillPaint(true);
//        splinerenderer0.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator());
//        splinerenderer0.setDefaultItemLabelsVisible(true);
//        splinerenderer0.setItemLabelAnchorOffset(-8);

        plot.setRangeCrosshairVisible(true);
        plot.setDomainCrosshairVisible(true);


        NumberAxis yaxis1 = new NumberAxis(Y1name);
        NumberAxis yaxis2 = new NumberAxis(Y2name);
        yaxis1.setAutoRangeIncludesZero(false);
        yaxis1.setAutoRangeMinimumSize(Double.MIN_NORMAL);
        plot.setRangeAxis(0, yaxis1);
        yaxis2.setAutoRangeIncludesZero(false);
        yaxis2.setAutoRangeMinimumSize(Double.MIN_NORMAL);
        plot.setRangeAxis(1, yaxis2);

        NumberAxis xaxis = new NumberAxis(Xname);
        xaxis.setAutoRangeIncludesZero(false);
        plot.setDomainAxis(xaxis);

        DecimalFormat decimalFormat = new DecimalFormat("0.#############E0");
        xaxis.setNumberFormatOverride(decimalFormat);
        yaxis1.setNumberFormatOverride(decimalFormat);
        yaxis2.setNumberFormatOverride(decimalFormat);

        //Map the data to the appropriate axis
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);

        //configure the chart
        plot.getRangeAxis(0).setLabelPaint(Color.BLUE);
        plot.getRangeAxis(1).setLabelPaint(Color.RED);
        plot.setOutlinePaint(null);
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        plot.getDomainAxis().setAutoRange(true);
        plot.getRangeAxis(0).setAutoRange(true);
        plot.getRangeAxis(1).setAutoRange(true);


        //generate the chart
        chart = new JFreeChart(plot);
        chart.setBackgroundPaint(null);
        chart.setBorderVisible(false);
        chart.removeLegend();
    }

    /**
     * @return JFreeChart
     */
    public JFreeChart getChart() {
        return chart;
    }

    /**
     * Aborts measurement by canceling DatasetTimer in AutoUpdatingDataset
     */
    public void abortMeasurement() {
        AutoUpdatingDataset as1 = (AutoUpdatingDataset) series1;
        AutoUpdatingDataset as2 = (AutoUpdatingDataset) series2;
        as1.abortMeasurement();
        as2.abortMeasurement();
    }
}
