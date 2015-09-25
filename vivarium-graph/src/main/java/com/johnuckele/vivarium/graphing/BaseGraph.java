package com.johnuckele.vivarium.graphing;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class BaseGraph
{
    protected JFreeChart _chart;

    protected ChartPanel getPanel()
    {
        return new ChartPanel(_chart);
    }

}
