package io.vivarium.graphing.util;

import java.awt.Font;

import javax.swing.JLabel;

import org.jfree.chart.JFreeChart;

public class Utils
{
    private static final Font DEFAULT_FONT = new JLabel().getFont();

    public static void setChartToDefaultFont(JFreeChart chart)
    {
        System.out.println(DEFAULT_FONT);
        setChartFont(chart, DEFAULT_FONT);
    }

    public static void setChartFont(JFreeChart chart, Font font)
    {
        chart.getTitle().setFont(font);
        chart.getXYPlot().getDomainAxis().setLabelFont(font);
        chart.getXYPlot().getDomainAxis().setTickLabelFont(font);
        chart.getXYPlot().getRangeAxis().setLabelFont(font);
        chart.getXYPlot().getRangeAxis().setTickLabelFont(font);
        // chart.getLegend().setItemFont(font);
    }
}
