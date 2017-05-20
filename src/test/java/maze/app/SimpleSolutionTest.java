package maze.app;

import maze.app.business.Hop;
import maze.app.business.ReturnValue;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static maze.app.business.ReturnValue.OK;
import static org.junit.Assert.*;

/**
 * Created by dvird on 17/04/22.
 */
public class SimpleSolutionTest {

    @BeforeClass
    public static void createTables()
    {
        Solution.createTables();
    }

    @AfterClass
    public static void dropTables()
    {
        Solution.dropTables();
    }

    @Before
    public void clearTables()
    {
        Solution.clearTables();
    }

    @Test
    public void simpleTest()
    {
        int source1 = 1;
        int destination1 = 2;
        Hop testHop1 = new Hop(source1,destination1);
        Hop resHop;
        ReturnValue returnValue;

        //Trying to add Hop
        returnValue = Solution.addHop(testHop1);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        //Trying to get Hop
        resHop = Solution.getHop(source1, destination1);
        assertNotNull(resHop);
        assertEquals(testHop1, resHop);

    }



}