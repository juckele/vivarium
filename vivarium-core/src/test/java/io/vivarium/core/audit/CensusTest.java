package io.vivarium.core.audit;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.johnuckele.vtest.Tester;

import io.vivarium.audit.AuditFunction;
import io.vivarium.audit.CensusFunction;
import io.vivarium.audit.CensusRecord;
import io.vivarium.core.Blueprint;
import io.vivarium.core.EntityType;
import io.vivarium.core.World;
import io.vivarium.test.FastTest;
import io.vivarium.test.IntegrationTest;

public class CensusTest
{
    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testCensus()
    {
        Blueprint blueprint = Blueprint.makeDefault();
        AuditFunction censusFunction = new CensusFunction();
        ArrayList<AuditFunction> auditFunctions = new ArrayList<>();
        auditFunctions.add(censusFunction);
        blueprint.setAuditFunctions(auditFunctions);
        World world = new World(blueprint);
        world.tick();
        CensusRecord record = (CensusRecord) world.getAuditRecords().remove();
        ArrayList<Integer> populationRecords = record.getPopulationRecords();
        Tester.equal("Population records should have one entry", populationRecords.size(), 1);
        Tester.equal("Initial population should match world", (int) populationRecords.get(0),
                world.getCount(EntityType.CREATURE));
        Tester.greaterThan("Initial population should be non-zero", populationRecords.get(0), 0);
    }
}
