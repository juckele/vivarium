package com.johnuckele.vivarium.scripts;

import org.junit.Test;

public class CreateWorldTest
{
    // @Test
    public void testDefault()
    {
        {
            String[] commandArgs = { "-o", "/tmp/w.viv" };
            CreateWorld.main(commandArgs);
        }
    }

    @Test
    public void testWithBlueprint()
    {
        {
            String[] commandArgs = { "-o", "/tmp/b.viv" };
            CreateBlueprint.main(commandArgs);
        }
        {
            String[] commandArgs = { "-o", "/tmp/w.viv", "-b", "/tmp/b.viv" };
            CreateWorld.main(commandArgs);
        }
    }
}
