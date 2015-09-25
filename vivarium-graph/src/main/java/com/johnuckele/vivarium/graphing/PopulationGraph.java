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

import com.johnuckele.vivarium.graphing.util.Utils;

public class PopulationGraph
{
    private JFreeChart _chart;

    public PopulationGraph()
    {
        PlotOrientation orientation = PlotOrientation.VERTICAL;
        XYSeries series = new XYSeries("Species 1");
        series.add(0, 0);
        series.add(1, 2);
        series.add(2, 3);
        series.add(3, 1);
        XYDataset dataset = new XYSeriesCollection(series);
        series = new XYSeries("Species 2");
        series.add(0, 2);
        series.add(1, 3);
        series.add(2, 4);
        series.add(3, 3);
        ((XYSeriesCollection) dataset).addSeries(series);
        boolean urls = false;
        boolean tooltips = false;
        boolean legend = true;
        _chart = ChartFactory.createXYLineChart("Line Chart XY Fun", "Time", "Population", dataset, orientation, legend,
                tooltips, urls);
        Utils.setChartToDefaultFont(_chart);
    }

    private ChartPanel getPanel()
    {
        return new ChartPanel(_chart);
    }

    public static void main(String[] args)
    {
        PopulationGraph graph = new PopulationGraph();
        JFrame frame = new JFrame();
        frame.add(graph.getPanel());
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
