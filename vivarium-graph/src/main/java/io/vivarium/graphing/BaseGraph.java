package io.vivarium.graphing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class BaseGraph
{
    protected JFreeChart _chart;

    protected ChartPanel getPanel()
    {
        return new ChartPanel(_chart);
    }

    public void saveImage(String outputFileName, String extension, int imageSizeX, int imageSizeY) throws IOException
    {
        BufferedImage buff = _chart.createBufferedImage(imageSizeX, imageSizeY);
        File outputfile = new File(outputFileName);
        ImageIO.write(buff, extension, outputfile);
    }

}
