package com.johnuckele.vivarium.scripts;

import org.junit.Test;

public class CreateSpeciesTest
{
    @Test
    public void testDefault()
    {
        {
            String[] commandArgs = { "-o", "/tmp/s.viv" };
            CreateSpecies.main(commandArgs);
        }
    }
}
