package com.johnuckele.vivarium.scripts;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CreateWorldTest
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
    public void testDefault()
    {
        {
            String[] commandArgs = { "-o", path + "w.viv" };
            CreateWorld.main(commandArgs);
        }
    }

    @Test
    public void testWithBlueprint()
    {
        {
            String[] commandArgs = { "-o", path + "b.viv" };
            CreateBlueprint.main(commandArgs);
        }
        {
            String[] commandArgs = { "-o", path + "w.viv", "-b", path + "b.viv" };
            CreateWorld.main(commandArgs);
        }
    }
}
