package com.johnuckele.vivarium.core.audit;

import java.util.ArrayList;

import org.junit.Test;

import com.johnuckele.vivarium.audit.AuditRecordType;
import com.johnuckele.vivarium.audit.Census;
import com.johnuckele.vivarium.core.Blueprint;
import com.johnuckele.vivarium.core.EntityType;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vtest.Tester;

public class CensusTest
{
    @Test
    public void testCensus()
    {
        Blueprint blueprint = Blueprint.makeDefault();
        blueprint.addAuditRecord(AuditRecordType.CENSUS);
        World world = new World(blueprint);
        world.tick();
        Census record = (Census) world.getAuditRecords().remove();
        ArrayList<Integer> populationRecords = record.getPopulationRecords();
        Tester.equal("Population records should have two entries", populationRecords.size(), 2);
        Tester.equal("Population records be equal", populationRecords.get(0), populationRecords.get(1));
        Tester.equal("Initial population should match world", (int) populationRecords.get(0),
                world.getCount(EntityType.CREATURE));
        Tester.greaterThan("Initial population should be non-zero", (int) populationRecords.get(0), 0);
    }
}
