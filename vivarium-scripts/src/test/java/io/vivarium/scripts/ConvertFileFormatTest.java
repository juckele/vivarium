package io.vivarium.scripts;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;

import io.vivarium.test.FastTest;
import io.vivarium.test.SystemTest;

public class ConvertFileFormatTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    public String path;

    @Before
    public void setupPath() throws IOException
    {
        path = folder.getRoot().getCanonicalPath() + File.separator;
    }

    @Test
    @Category({ FastTest.class, SystemTest.class })
    public void testIteratedConversion() throws IOException
    {
        {
            String[] commandArgs = { "-o", path + "b.viv" };
            CreateWorldBlueprint.main(commandArgs);
        }
        {
            String[] commandArgs = { "-i", path + "b.viv", "-x", "JSON", "-o", path + "b.gwt", "-y", "GWT" };
            ConvertFileFormat.main(commandArgs);
        }
        {
            String[] commandArgs = { "-i", path + "b.gwt", "-x", "GWT", "-o", path + "b2.viv", "-y", "JSON" };
            ConvertFileFormat.main(commandArgs);
        }
    }

}
