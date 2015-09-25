package com.johnuckele.vivarium.graphing;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.johnuckele.vivarium.audit.AuditFunction;
import com.johnuckele.vivarium.audit.CensusFunction;
import com.johnuckele.vivarium.audit.CensusRecord;
import com.johnuckele.vivarium.core.Blueprint;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.graphing.util.Utils;

public class PopulationGraph extends BaseGraph
{
    public PopulationGraph(CensusRecord record)
    {
        ArrayList<Integer> popRecords = record.getPopulationRecords();
        ArrayList<Integer> ticks = record.getRecordTicks();

        PlotOrientation orientation = PlotOrientation.VERTICAL;
        XYSeries series = new XYSeries("Species 1");
        for (int i = 0; i < ticks.size(); i++)
        {
            series.add(ticks.get(i), popRecords.get(i));
        }
        XYDataset dataset = new XYSeriesCollection(series);
        boolean urls = false;
        boolean tooltips = false;
        boolean legend = false;
        _chart = ChartFactory.createXYLineChart("Creature Population", "Time", "Population", dataset, orientation,
                legend, tooltips, urls);
        Utils.setChartToDefaultFont(_chart);
    }

    public static void main(String[] args)
    {
        Blueprint b = Blueprint.makeDefault();

        ArrayList<AuditFunction> auditFunctions = new ArrayList<AuditFunction>();
        CensusFunction census = new CensusFunction();
        auditFunctions.add(census);
        b.setAuditFunctions(auditFunctions);

        World w = new World(b);

        for (int i = 0; i < 40000; i++)
        {
            w.tick();
        }

        BaseGraph graph = new PopulationGraph((CensusRecord) w.getAuditRecords().get(0));
        JFrame frame = new JFrame();
        frame.add(graph.getPanel());
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
