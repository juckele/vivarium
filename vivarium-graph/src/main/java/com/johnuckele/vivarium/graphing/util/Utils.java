package com.johnuckele.vivarium.graphing.util;

import java.awt.Font;

import javax.swing.JLabel;

import org.jfree.chart.JFreeChart;

public class Utils
{
    public static void setChartToDefaultFont(JFreeChart chart)
    {
        Font font = new JLabel().getFont();
        setChartFont(chart, font);
    }

    public static void setChartFont(JFreeChart chart, Font font)
    {
        chart.getTitle().setFont(font);
        chart.getXYPlot().getDomainAxis().setLabelFont(font);
        chart.getXYPlot().getDomainAxis().setTickLabelFont(font);
        chart.getXYPlot().getRangeAxis().setLabelFont(font);
        chart.getXYPlot().getRangeAxis().setTickLabelFont(font);
        chart.getLegend().setItemFont(font);
    }
}
