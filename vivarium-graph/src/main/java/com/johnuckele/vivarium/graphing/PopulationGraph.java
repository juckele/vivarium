package com.johnuckele.vivarium.graphing;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class PopulationGraph
{
    public static void main(String[] args)
    {
        PlotOrientation orientation = PlotOrientation.VERTICAL;
        XYSeries series = new XYSeries(0);
        series.add(0, 0);
        series.add(1, 2);
        series.add(2, 3);
        series.add(3, 1);
        XYDataset dataset = new XYSeriesCollection(series);
        boolean urls = false;
        boolean tooltips = false;
        boolean legend = false;
        JFreeChart chart = ChartFactory.createXYLineChart("Line Chart XY Fun", "Time", "Population", dataset,
                orientation, legend, tooltips, urls);
        ChartPanel panel = new ChartPanel(chart);
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
