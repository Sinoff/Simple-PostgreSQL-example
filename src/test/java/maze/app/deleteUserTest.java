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
public class deleteUserTest {

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
    public void deleteUserTest()
    {
        ReturnValue returnValue;
        int id1 = 1;
        int source1 = 1;
        int destination2 = 2;
        int id2 = 2;
        int id3 = 3;
        int id_negative1 = -1;

        // #1 - NOT_EXISTS
        // try to delete non existing user.
        returnValue = Solution.deleteUser(id1);
        assertNotNull(returnValue);
        assertEquals(NOT_EXISTS, returnValue);

        // try to delete non existing user with illegal parameters.
        returnValue = Solution.deleteUser(id_negative1);
        assertNotNull(returnValue);
        assertEquals(NOT_EXISTS, returnValue);

        // add Hop
        Hop testHop1 = new Hop(source1, destination2);
        returnValue = Solution.addHop(testHop1);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        // try to delete non existing user.
        returnValue = Solution.deleteUser(id1);
        assertNotNull(returnValue);
        assertEquals(NOT_EXISTS, returnValue);

        // add users
        User user1 = new User(id1, source1, destination2);
        returnValue = Solution.addUser(user1);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);
        User user2 = new User(id2, source1, destination2);
        returnValue = Solution.addUser(user2);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        // try to delete non existing user.
        returnValue = Solution.deleteUser(id3);
        assertNotNull(returnValue);
        assertEquals(NOT_EXISTS, returnValue);

        //#2 - OK
        // delete user1
        returnValue = Solution.deleteUser(id1);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);
        // delete user2
        returnValue = Solution.deleteUser(id2);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        // #3 - NOT_EXISTS (again...)
        // try to delete already deleted user
        returnValue = Solution.deleteUser(id1);
        assertNotNull(returnValue);
        assertEquals(NOT_EXISTS, returnValue);

        // #4 - OK (again...)
        //try to re-add users (also check adding user in non increasing id value works ok..)
        returnValue = Solution.addUser(user2);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);
        returnValue = Solution.addUser(user1);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        // delete user1
        returnValue = Solution.deleteUser(id1);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);
        // delete user2
        returnValue = Solution.deleteUser(id2);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);
    }
}