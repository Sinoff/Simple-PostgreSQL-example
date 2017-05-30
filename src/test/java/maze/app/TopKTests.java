package maze.app;

import maze.app.business.Hop;
import maze.app.business.User;
import maze.app.business.ReturnValue;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static maze.app.business.ReturnValue.*;
import static org.junit.Assert.*;

/**
 * Created by rssinoff on 5/30/2017.
 */
public class TopKTests {

    @BeforeClass
    public static void createTables() {
        Solution.createTables();
    }

    @AfterClass
    public static void dropTables() {
        Solution.dropTables();
    }

    @Before
    public void clearTables() {
        Solution.clearTables();
    }

    @Test
    public void topKTest() {

        ReturnValue returnValue;

        ArrayList<Hop> result;
        ArrayList<Hop> local;
        int id1 = 1;
        int source1 = 1;
        int destination1 = 1;
        int id2 = 2;
        int source2 = 2;
        int destination2 = 2;
        int destination3 = 3;
        int id3 = 3;


        //#1 - call when DBs are empty.
        result = Solution.topKLoadedHops(1,1);
        local = new ArrayList<>(1);
        assertNotNull(result);
        assertEquals(local, result);

        //add hops
        Hop testHop1 = new Hop(source1, destination2, 1);
        returnValue = Solution.addHop(testHop1);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        Hop testHop2 = new Hop(source2, destination1, 2);
        returnValue = Solution.addHop(testHop2);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        Hop testHop3 = new Hop(source1, destination3, 5);
        returnValue = Solution.addHop(testHop3);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        //#2 - call when users DB is empty (but hops isn't)
        result = Solution.topKLoadedHops(1,1);
        assertNotNull(result);
        assertEquals(local, result);
        //#2 - call when users DB is empty (but hops isn't)
        result = Solution.topKLoadedHops(0,0);
        local = new ArrayList<>(0);
        assertNotNull(result);
        assertEquals(local, result);
        // threshold is 0
        result = Solution.topKLoadedHops(1,0);
        local = new ArrayList<>(1);
        local.add(testHop3);
        assertNotNull(result);
        assertEquals(local, result);
        // threshold is 0, k = 2
        result = Solution.topKLoadedHops(2,0);
        local = new ArrayList<>(2);
        local.add(testHop3);
        local.add(testHop2);
        assertNotNull(result);
        assertEquals(local, result);
        // threshold is 0, k = 3
        result = Solution.topKLoadedHops(3,0);
        local = new ArrayList<>(3);
        local.add(testHop3);
        local.add(testHop2);
        local.add(testHop1);
        assertNotNull(result);
        assertEquals(local, result);
        // threshold is 0, k = 4
        result = Solution.topKLoadedHops(4,0);
        local = new ArrayList<>(4);
        local.add(testHop3);
        local.add(testHop2);
        local.add(testHop1);
        assertNotNull(result);
        assertEquals(local, result);


        //add User
        User user1 = new User(id1, source1, destination2);
        returnValue = Solution.addUser(user1);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        //#3 - call when users has only one user
        // threshold too high
        result = Solution.topKLoadedHops(1,2);
        local = new ArrayList<>(2);
        assertNotNull(result);
        assertEquals(local, result);
        // k is 0
        result = Solution.topKLoadedHops(0,1);
        assertNotNull(result);
        assertEquals(local, result);
        // returns 1 hop - testHop1 (since threshold is 1)
        result = Solution.topKLoadedHops(1,1);
        Hop testHop1ActualLoad = new Hop(source1, destination2, 2);
        local.add(testHop1ActualLoad);
        assertNotNull(result);
        assertEquals(local, result);
        // returns 1 hop - testHop3 (since threshold is 0)
        result = Solution.topKLoadedHops(1,0);
        local.clear();
        local.add(testHop3);
        assertNotNull(result);
        assertEquals(local, result);
        // returns 2 hops
        result = Solution.topKLoadedHops(2,0);
        local = new ArrayList<>(2);
        local.add(testHop3);
        local.add(testHop1ActualLoad);
        assertNotNull(result);
        assertEquals(local, result);

        //#3 - call when users has 2 users, on same hop
        //add User
        User user2 = new User(id2, source1, destination2);
        returnValue = Solution.addUser(user2);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        // threshold too high
        result = Solution.topKLoadedHops(1,3);
        local = new ArrayList<>(1);
        assertNotNull(result);
        assertEquals(local, result);
        // returns 1 hop
        result = Solution.topKLoadedHops(1,1);
        testHop1ActualLoad = new Hop(source1, destination2, 3);
        local.add(testHop1ActualLoad);
        assertNotNull(result);
        assertEquals(local, result);
        // returns 1 hop
        result = Solution.topKLoadedHops(1,2);
        assertNotNull(result);
        assertEquals(local, result);
        // returns 1 hop - testHop3
        result = Solution.topKLoadedHops(1,0);
        local.clear();
        local.add(testHop3);
        assertNotNull(result);
        assertEquals(local, result);
        // returns 1 hop - testHop1
        result = Solution.topKLoadedHops(2,1);
        local = new ArrayList<>(2);
        local.add(testHop1ActualLoad);
        assertNotNull(result);
        assertEquals(local, result);

        //#4 - call when users has 2 users on same hop and another on different hop
        //add User
        User user3 = new User(id3, source2, destination1);
        returnValue = Solution.addUser(user3);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        // threshold too high
        result = Solution.topKLoadedHops(1,3);
        local = new ArrayList<>(1);
        assertNotNull(result);
        assertEquals(local, result);
        result = Solution.topKLoadedHops(1,2);
        local.add(testHop1ActualLoad);
        assertNotNull(result);
        assertEquals(local, result);
        result = Solution.topKLoadedHops(1,0);
        local.clear();
        local.add(testHop3);
        assertNotNull(result);
        assertEquals(local, result);

        //update hops load
        testHop1.setLoad(2);
        returnValue = Solution.updateHopLoad(testHop1);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        //#5 - after hops load update
        // threshold too high
        result = Solution.topKLoadedHops(1,3);
        local = new ArrayList<>(1);
        assertNotNull(result);
        assertEquals(local, result);
        result = Solution.topKLoadedHops(1,0);
        testHop1ActualLoad.setLoad(6);
        local.add(testHop1ActualLoad);
        assertNotNull(result);
        assertEquals(local, result);
    }
}