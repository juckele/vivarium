package io.vivarium.graphing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import io.vivarium.audit.ActionFrequencyFunction;
import io.vivarium.audit.ActionFrequencyRecord;
import io.vivarium.audit.AuditFunction;
import io.vivarium.core.Action;
import io.vivarium.core.Blueprint;
import io.vivarium.core.World;
import io.vivarium.graphing.util.Utils;

public class GenerationalActionGraph extends BaseGraph
{
    public GenerationalActionGraph(ActionFrequencyRecord record)
    {
        boolean includeMales = true;
        boolean includeFemales = false;
        boolean includeNonPregnant = true;
        boolean includePregnant = true;
        int maximumGeneration = record.getMaximumGeneration();

        Collection<Action> commonActions = Arrays.asList(Action.REST, Action.TURN_LEFT, Action.TURN_RIGHT, Action.MOVE,
                Action.EAT, Action.BREED);
        Set<Action> failableActions = EnumSet.of(Action.MOVE, Action.BREED, Action.EAT);
        XYSeriesCollection dataset = new XYSeriesCollection();
        Map<Action, XYSeries> successfulActionSeries = new EnumMap<>(Action.class);
        Map<Action, XYSeries> failedActionSeries = new EnumMap<>(Action.class);
        for (Action action : commonActions)
        {
            if (failableActions.contains(action))
            {
                XYSeries successSeries = new XYSeries(action.toString() + "_SUCCESS");
                dataset.addSeries(successSeries);
                successfulActionSeries.put(action, successSeries);

                XYSeries failSeries = new XYSeries(action.toString() + "_FAIL");
                dataset.addSeries(failSeries);
                failedActionSeries.put(action, failSeries);
            }
            else
            {
                XYSeries successSeries = new XYSeries(action.toString());
                dataset.addSeries(successSeries);
                successfulActionSeries.put(action, successSeries);
            }
        }

        for (int generation = 1; generation <= maximumGeneration; generation++)
        {
            double totalActionCount = 0;
            Map<Action, Double> successfulActionCounts = new EnumMap<>(Action.class);
            Map<Action, Double> failedActionCounts = new EnumMap<>(Action.class);
            for (Action action : commonActions)
            {
                // System.out.println(successfulActionCounts);
                successfulActionCounts.put(action, 0.0);
                failedActionCounts.put(action, 0.0);
                if (includeMales)
                {
                    if (includeNonPregnant)
                    {
                        successfulActionCounts.put(action, successfulActionCounts.get(action)
                                + record.getRecord(generation, false, false, action, true));
                        if (failableActions.contains(action))
                        {
                            failedActionCounts.put(action, failedActionCounts.get(action)
                                    + record.getRecord(generation, false, false, action, false));
                        }
                    }
                }
                if (includeFemales)
                {
                    if (includeNonPregnant)
                    {
                        successfulActionCounts.put(action, successfulActionCounts.get(action)
                                + record.getRecord(generation, true, false, action, true));
                        if (failableActions.contains(action))
                        {
                            failedActionCounts.put(action, failedActionCounts.get(action)
                                    + record.getRecord(generation, true, false, action, false));
                        }
                    }
                    if (includePregnant)
                    {
                        successfulActionCounts.put(action, successfulActionCounts.get(action)
                                + record.getRecord(generation, true, true, action, true));
                        if (failableActions.contains(action))
                        {
                            failedActionCounts.put(action, failedActionCounts.get(action)
                                    + record.getRecord(generation, true, true, action, false));
                        }
                    }
                }
                totalActionCount += successfulActionCounts.get(action);
                totalActionCount += failedActionCounts.get(action);
            }
            for (Action action : commonActions)
            {
                // System.out.println("Generation: " + generation + ", Action: " + action + ", Count: " +
                // successfulActionCounts.get(action) + "/" + failedActionCounts.get(action));
                successfulActionSeries.get(action).add(generation,
                        successfulActionCounts.get(action) / totalActionCount);
                if (failableActions.contains(action))
                {
                    failedActionSeries.get(action).add(generation, failedActionCounts.get(action) / totalActionCount);
                }
            }
        }

        PlotOrientation orientation = PlotOrientation.VERTICAL;

        boolean urls = false;
        boolean tooltips = false;
        boolean legend = true;
        _chart = ChartFactory.createXYLineChart("Creature Actions by Generation", "Generation", "Action Percent",
                dataset, orientation, legend, tooltips, urls);
        Utils.setChartToDefaultFont(_chart);
    }

    public static void main(String[] args) throws IOException
    {
        Blueprint b = Blueprint.makeDefault();

        ArrayList<AuditFunction> auditFunctions = new ArrayList<>();
        ActionFrequencyFunction actionFrequencies = new ActionFrequencyFunction();
        auditFunctions.add(actionFrequencies);
        b.setAuditFunctions(auditFunctions);

        World w = new World(b);

        for (int i = 0; i < 10_000; i++)
        {
            w.tick();
        }

        // System.out.println(JSONConverter.serializerToJSONString(w.getAuditRecords().get(0)));

        BaseGraph graph = new GenerationalActionGraph((ActionFrequencyRecord) w.getAuditRecords().get(0));

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