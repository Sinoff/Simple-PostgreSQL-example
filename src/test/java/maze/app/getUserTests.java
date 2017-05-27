package maze.app;

import maze.app.business.Hop;
import maze.app.business.User;
import maze.app.business.ReturnValue;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static maze.app.business.ReturnValue.*;
import static org.junit.Assert.*;

/**
 * Created by rssinoff on 5/25/2017.
 */
public class getUserTests {

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
    public void getUserTest()
    {
        ReturnValue returnValue;
        User returnUser;
        int id1 = 1;
        int source1 = 1;
        int destination2 = 2;
        int id2 = 2;
        int id3 = 3;

        // add Hop
        Hop testHop1 = new Hop(source1, destination2);
        returnValue = Solution.addHop(testHop1);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        // add Users
        User user1 = new User(id1, source1, destination2);
        returnValue = Solution.addUser(user1);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);
        User user2 = new User(id2, source1, destination2);
        returnValue = Solution.addUser(user2);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        // #1 - OK
        // get users which were added
        returnUser = Solution.getUser(id1);
        assertNotNull(returnUser);
        assertEquals(returnUser, user1);
        returnUser = Solution.getUser(id2);
        assertNotNull(returnUser);
        assertEquals(returnUser, user2);

        // #2 - badUser
        // get user which was never added
        returnUser = Solution.getUser(id3);
        assertNotNull(returnUser);
        assertEquals(returnUser, User.badUser);
    }
}