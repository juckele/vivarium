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

public class CreateBlueprintTest
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
    public void testDefault() throws IOException
    {
        {
            String[] commandArgs = { "-o", path + "b.viv" };
            CreateWorldBlueprint.main(commandArgs);
        }
    }

    @Test
    @Category({ FastTest.class, SystemTest.class })
    public void testWithCreatureBlueprint() throws IOException
    {
        {
            String[] commandArgs = { "-o", path + "s.viv" };
            CreateCreatureBlueprint.main(commandArgs);
        }
        {
            String[] commandArgs = { "-o", path + "b.viv", "-s", path + "s.viv" };
            CreateWorldBlueprint.main(commandArgs);
        }
    }

}
