package maze.app;

import maze.app.business.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by dvird on 17/04/22.
 */
public class SolutionTest {

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
    public void addHopTest()
    {
        Hop hop1 = new Hop(1,2);
        //  Trying to add Hop
        assertReturnValue(ReturnValue.OK, Solution.addHop(hop1));

        //  Trying to add the same Hop
        assertReturnValue(ReturnValue.ALREADY_EXISTS, Solution.addHop(hop1));
        hop1.setLoad(2);
        assertReturnValue(ReturnValue.ALREADY_EXISTS, Solution.addHop(hop1));

        //  Trying to add illegal hops
        hop1.setSource(0);
        assertReturnValue(ReturnValue.BAD_PARAMS, Solution.addHop(hop1));
        hop1.setSource(2);
        assertReturnValue(ReturnValue.BAD_PARAMS, Solution.addHop(hop1));
        hop1.setDestination(0);
        assertReturnValue(ReturnValue.BAD_PARAMS, Solution.addHop(hop1));
        hop1.setDestination(2);
        assertReturnValue(ReturnValue.BAD_PARAMS, Solution.addHop(hop1));
        hop1.setDestination(1);
        hop1.setLoad(0);
        assertReturnValue(ReturnValue.BAD_PARAMS, Solution.addHop(hop1));
    }

    @Test
    public void getHopTest()
    {
        Hop hop1 = new Hop(1,2);
        assertReturnValue(ReturnValue.OK, Solution.addHop(hop1));
        //Trying to get Hop
        Hop resHop = null;
        resHop = Solution.getHop(1, 2);
        assertNotNull(resHop);
        assertEquals(hop1, resHop);

        resHop = Solution.getHop(2, 3);
        assertNotNull(resHop);
        assertEquals(Hop.badHop, resHop);
    }

    @Test
    public void updateHopTest()
    {
        Hop hop1 = new Hop(1,2);
        assertReturnValue(ReturnValue.OK, Solution.addHop(hop1));
        hop1.setLoad(2);
        assertReturnValue(ReturnValue.OK, Solution.updateHopLoad(hop1));
        Hop resHop = Solution.getHop(1, 2);
        assertNotNull(resHop);
        assertEquals(resHop.getLoad(), 2);
        hop1.setDestination(3);
        assertReturnValue(ReturnValue.NOT_EXISTS, Solution.updateHopLoad(hop1));
        hop1.setDestination(2);
        hop1.setLoad(0);
        assertReturnValue(ReturnValue.BAD_PARAMS, Solution.updateHopLoad(hop1));
    }

    @Test
    public void deleteHopTest()
    {
        assertReturnValue(ReturnValue.OK, Solution.addHop(new Hop(1,2)));

        assertReturnValue(ReturnValue.NOT_EXISTS, Solution.deleteHop(1,3));
        assertReturnValue(ReturnValue.NOT_EXISTS, Solution.deleteHop(3,2));
        assertReturnValue(ReturnValue.NOT_EXISTS, Solution.deleteHop(2,3));
        assertReturnValue(ReturnValue.OK, Solution.deleteHop(1,2));
        assertReturnValue(ReturnValue.NOT_EXISTS, Solution.deleteHop(1,2));
    }

    @Test
    public void addUserTest()
    {
        User user1 = new User(1,1,2);
        User user2 = new User(2,1,2);
        User user3 = new User(3,3,2);
        //  Trying to add User
        assertReturnValue(ReturnValue.NOT_EXISTS, Solution.addUser(user1));
        assertReturnValue(ReturnValue.OK, Solution.addHop(new Hop(1,2)));
        assertReturnValue(ReturnValue.OK, Solution.addUser(user1));
        assertReturnValue(ReturnValue.OK, Solution.addUser(user2));

        //  Trying to add the same User
        assertReturnValue(ReturnValue.ALREADY_EXISTS, Solution.addUser(user1));

        user1.setId(0);                                    
        assertReturnValue(ReturnValue.BAD_PARAMS, Solution.addUser(user1));
    }

    @Test
    public void getUserTest()
    {
        User user1 = new User(1,1,2);
        assertReturnValue(ReturnValue.OK, Solution.addHop(new Hop(1,2)));
        assertReturnValue(ReturnValue.OK, Solution.addUser(user1));

        //Trying to get User
        User resUser = null;
        resUser = Solution.getUser(1);
        assertNotNull(resUser);
        assertEquals(user1, resUser);

        resUser = Solution.getUser(2);
        assertNotNull(resUser);
        assertEquals(User.badUser, resUser);
    }

    @Test
    public void updateUserTest()
    {
        User user1 = new User(1,1,2);
        assertReturnValue(ReturnValue.OK, Solution.addHop(new Hop(1,2)));
        assertReturnValue(ReturnValue.OK, Solution.addHop(new Hop(1,3)));
        assertReturnValue(ReturnValue.OK, Solution.addUser(user1));

        user1.setDestination(3);
        assertReturnValue(ReturnValue.OK, Solution.updateUserHop(user1));
        user1.setDestination(4);
        assertReturnValue(ReturnValue.NOT_EXISTS, Solution.updateUserHop(user1));
        user1.setDestination(2);
        user1.setId(2);
        assertReturnValue(ReturnValue.NOT_EXISTS, Solution.updateUserHop(user1));
    }

    @Test
    public void deleteUserTest()
    {
        User user1 = new User(1,1,2);
        assertReturnValue(ReturnValue.OK, Solution.addHop(new Hop(1,2)));
        assertReturnValue(ReturnValue.OK, Solution.addUser(user1));

        assertReturnValue(ReturnValue.NOT_EXISTS, Solution.deleteUser(2));
        assertReturnValue(ReturnValue.OK, Solution.deleteUser(1));
        assertReturnValue(ReturnValue.NOT_EXISTS, Solution.deleteUser(1));
    }

    @Test
    public void topKloadedHopsTest()
    {
        // add 10 hops and users
        ArrayList<Hop> topKHopsExpected = new ArrayList<>();
        for (int i = 10 ; i > 0 ; --i) {
            Hop hop = new Hop(i, i+1, i);
            if (i > 5) {
                topKHopsExpected.add(hop);
            }
            Solution.addHop(hop);
            Solution.addUser(new User(i, i, i+1));
        }
        for (Hop hop : topKHopsExpected) {
            hop.setLoad(hop.getLoad()*2);
        }
        ArrayList<Hop> topKHopsActual = Solution.topKLoadedHops(5, 1);
        assertEquals(topKHopsExpected.size(), topKHopsActual.size());
        assertTrue(topKHopsExpected.equals(topKHopsActual));
    }

    @Test
    public void getAllPathsTest()
    {
        Path path1 = new Path();
        Path path2 = new Path();

        Hop hop1 = new Hop(1, 2, 2);
        Solution.addHop(hop1);
        Hop hop2 = new Hop(2, 3, 2);
        Solution.addHop(hop2);
        Hop hop3 = new Hop(2, 1, 2);
        Solution.addHop(hop3);
        Hop hop4 = new Hop(3, 4, 2);
        Solution.addHop(hop4);
        Hop hop5 = new Hop(2, 4, 2);
        Solution.addHop(hop5);

        path1.addHop(hop1);
        path1.addHop(hop2);
        path1.addHop(hop4);

        path2.addHop(hop1);
        path2.addHop(hop5);

        PathsList actualPathsList = Solution.getAllPaths(1, 4, 2);
        assertEquals(1, actualPathsList.size());
        assertTrue(actualPathsList.indexOf(path2) >= 0);

        actualPathsList = Solution.getAllPaths(1, 4, 3);
        assertEquals(2, actualPathsList.size());
        assertTrue(actualPathsList.indexOf(path1) >= 0);
        assertTrue(actualPathsList.indexOf(path2) >= 0);
    }

    private void assertReturnValue(ReturnValue actual, ReturnValue expected)
    {
        assertNotNull(actual);
        assertEquals(actual, expected);
    }
}