package com.johnuckele.vivarium.serialization;

import org.junit.Test;

import com.johnuckele.vtest.Tester;

public class SerializationCategoryTest
{
    @Test
    public void testSerializationCategoryRankedValues()
    {
        int priorRank = -1;
        for (SerializationCategory category : SerializationCategory.rankedValues())
        {
            Tester.greaterThan("Each serialization category in the ranked values should be strictly ascending",
                    category.getRank(), priorRank);
            priorRank = category.getRank();
        }
    }
}
