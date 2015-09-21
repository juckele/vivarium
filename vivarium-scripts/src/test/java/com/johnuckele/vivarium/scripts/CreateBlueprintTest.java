package com.johnuckele.vivarium.scripts;

import org.junit.Test;

public class CreateBlueprintTest
{
    @Test
    public void testDefault()
    {
        {
            String[] commandArgs = { "-o", "/tmp/b.viv" };
            CreateBlueprint.main(commandArgs);
        }
    }

    @Test
    public void testWithSpecies()
    {
        {
            String[] commandArgs = { "-o", "/tmp/s.viv" };
            CreateSpecies.main(commandArgs);
        }
        {
            String[] commandArgs = { "-o", "/tmp/b.viv", "-s", "/tmp/s.viv" };
            CreateBlueprint.main(commandArgs);
        }
    }

}
