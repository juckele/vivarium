package io.vivarium.graphing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import io.vivarium.audit.AuditBlueprint;
import io.vivarium.audit.CensusBlueprint;
import io.vivarium.audit.CensusRecord;
import io.vivarium.core.World;
import io.vivarium.core.WorldBlueprint;
import io.vivarium.graphing.util.Utils;

public class PopulationGraph extends BaseGraph
{
    public PopulationGraph(CensusRecord record)
    {
        ArrayList<Integer> popRecords = record.getPopulationRecords();
        ArrayList<Integer> ticks = record.getRecordTicks();

        PlotOrientation orientation = PlotOrientation.VERTICAL;
        XYSeries series = new XYSeries("Creature Blueprint 1");
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

    public static void main(String[] args) throws IOException
    {
        WorldBlueprint b = WorldBlueprint.makeDefault();
        b.setSize(200);

        ArrayList<AuditBlueprint> auditBlueprints = new ArrayList<>();
        CensusBlueprint census = new CensusBlueprint();
        auditBlueprints.add(census);
        b.setAuditBlueprints(auditBlueprints);

        World w = new World(b);

        for (int i = 0; i < 4000; i++)
        {
            w.tick();
        }

        BaseGraph graph = new PopulationGraph((CensusRecord) w.getAuditRecords().get(0));
        JFrame frame = new JFrame();
        frame.add(graph.getPanel());
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        BufferedImage buff = graph._chart.createBufferedImage(800, 800);
        File outputfile = new File("/tmp/graph.png");
        ImageIO.write(buff, "png", outputfile);
    }

}
