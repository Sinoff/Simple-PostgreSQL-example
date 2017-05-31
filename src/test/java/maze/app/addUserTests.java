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
public class AddUserTests {

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
    public void addUserTest() {
        ReturnValue returnValue;

        int id_negative1 = -1;
        int source_negative1 = -1;
        int destination_negative1 = -1;
        int id1 = 1;
        int source1 = 1;
        int destination1 = 1;
        int id2 = 2;
        int source2 = 2;
        int destination2 = 2;
        int id3 = 3;

        // #1 - BAD_PARAMS
        // trying to add a null pointer
        returnValue = Solution.addUser(null);
        assertNotNull(returnValue);
        assertEquals(BAD_PARAMS, returnValue);

        // trying to add user when hop doesn't exist
        User user1 = new User(id1, source1, destination2);
        returnValue = Solution.addUser(user1);
        assertNotNull(returnValue);
        assertEquals(NOT_EXISTS, returnValue);

        //add Hop
        Hop testHop1 = new Hop(source1, destination2);
        returnValue = Solution.addHop(testHop1);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        // trying to add user when hop doesn't exist (dest == src)
        User userWrongHop1 = new User(id1, source1, destination1);
        returnValue = Solution.addUser(userWrongHop1);
        assertNotNull(returnValue);
        assertEquals(BAD_PARAMS, returnValue);

        // trying to add user when hop doesn't exist (dest != src)
        User userWrongHop2 = new User(id1, source2, destination1);
        returnValue = Solution.addUser(userWrongHop2);
        assertNotNull(returnValue);
        assertEquals(NOT_EXISTS, returnValue);

        // trying to add user with negative source
        User userNegativeSource = new User(id1, source_negative1, destination2);
        returnValue = Solution.addUser(userNegativeSource);
        assertNotNull(returnValue);
        assertEquals(BAD_PARAMS, returnValue);

        // trying to add user with negative destination
        User userNegativeDestination = new User(id1, source1, destination_negative1);
        returnValue = Solution.addUser(userNegativeDestination);
        assertNotNull(returnValue);
        assertEquals(BAD_PARAMS, returnValue);

        // trying to add user with negative id
        User userNegativeId = new User(id_negative1, source1, destination2);
        returnValue = Solution.addUser(userNegativeId);
        assertNotNull(returnValue);
        assertEquals(BAD_PARAMS, returnValue);

        // #3 - OK
        // adding first user (id = 1, src =1 , dest = 2).
        returnValue = Solution.addUser(user1);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        // adding second user (id = 2, src =1 , dest = 2).
        User userSameHop = new User(id2, source1, destination2);
        returnValue = Solution.addUser(userSameHop);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        // #4 - BAD_PARAMS (Again...)
        // adding user with non existing hop, reversed to an existing one (i.e. src = original dest and vice versa).
        User userReversedHop = new User(id3, source2, destination1);
        returnValue = Solution.addUser(userReversedHop);
        assertNotNull(returnValue);
        assertEquals(NOT_EXISTS, returnValue);

        // #5 - ALREADY_EXISTS
        // adding already added user (same User object)
        returnValue = Solution.addUser(user1);
        assertNotNull(returnValue);
        assertEquals(ALREADY_EXISTS, returnValue);
        // adding user with already existing id (but not same User object)
        User userSameId = new User(id1, source1, destination2);
        returnValue = Solution.addUser(userSameId);
        assertNotNull(returnValue);
        assertEquals(ALREADY_EXISTS, returnValue);

        // # 6 - OK (Again...)
        // adding another hop
        Hop testHop2 = new Hop(source2, destination1);
        returnValue = Solution.addHop(testHop2);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        // adding user3 (id = 3, source = 2, destination = 1
        returnValue = Solution.addUser(userReversedHop);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);
    }
}