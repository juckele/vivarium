package io.vivarium.util;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.johnuckele.vtest.Tester;

import io.vivarium.test.FastTest;
import io.vivarium.test.UnitTest;

public class FunctionsTest
{
    @Test
    @Category({ FastTest.class, UnitTest.class })
    public void testSigmoid()
    {
        Tester.equal("sigmoid(-1) = 0.2689", 0.2689, Functions.sigmoid(-1), 0.0001);

        Tester.equal("sigmoid(0) = 0.5", 0.5, Functions.sigmoid(0), 0.0001);

        Tester.equal("sigmoid(0.5) = 0.6224", 0.6224, Functions.sigmoid(0.5), 0.0001);

        Tester.equal("sigmoid(1) = 0.7310", 0.7310, Functions.sigmoid(1), 0.0001);

        Tester.equal("sigmoid(4) = 0.9820", 0.9820, Functions.sigmoid(4), 0.0001);
    }

    @Test
    @Category({ FastTest.class, UnitTest.class })
    public void testLogarithmicAverage()
    {
        double a, b, c;

        a = 1;
        b = 1;
        c = 1;
        Tester.equal("avg(1,1) = 1", Math.log(c), Functions.logarithmicAverage(Math.log(a), Math.log(b)), 0.0001);

        a = 3;
        b = 1;
        c = 2;
        Tester.equal("avg(3,1) = 2", Math.log(c), Functions.logarithmicAverage(Math.log(a), Math.log(b)), 0.0001);

        a = 2;
        b = 1;
        c = 1.5;
        Tester.equal("avg(2,1) = 1.5", Math.log(c), Functions.logarithmicAverage(Math.log(a), Math.log(b)), 0.0001);
    }
}
