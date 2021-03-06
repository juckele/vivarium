package io.vivarium.core.audit;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.johnuckele.vtest.Tester;

import io.vivarium.audit.AuditBlueprint;
import io.vivarium.audit.CensusBlueprint;
import io.vivarium.audit.CensusRecord;
import io.vivarium.core.GridWorldBlueprint;
import io.vivarium.core.GridWorld;
import io.vivarium.test.FastTest;
import io.vivarium.test.IntegrationTest;

public class CensusTest
{
    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testCensus()
    {
        GridWorldBlueprint worldBlueprint = GridWorldBlueprint.makeDefault();
        AuditBlueprint censusBlueprint = new CensusBlueprint();
        ArrayList<AuditBlueprint> auditBlueprints = new ArrayList<>();
        auditBlueprints.add(censusBlueprint);
        worldBlueprint.setAuditBlueprints(auditBlueprints);
        GridWorld world = new GridWorld(worldBlueprint);
        world.tick();
        CensusRecord record = (CensusRecord) world.getAuditRecords().remove();
        ArrayList<Integer> populationRecords = record.getPopulationRecords();
        Tester.equal("Population records should have one entry", populationRecords.size(), 1);
        Tester.equal("Initial population should match world", (int) populationRecords.get(0), world.getCreatureCount());
        Tester.greaterThan("Initial population should be non-zero", (int) populationRecords.get(0), 0);
    }
}
