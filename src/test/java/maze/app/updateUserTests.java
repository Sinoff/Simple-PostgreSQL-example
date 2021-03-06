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
public class UpdateUserTests {

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
    public void updateUserTest() {
        ReturnValue returnValue;
        User retUser;

        int id1 = 1;
        int source1 = 1;
        int destination1 = 1;
        int id2 = 2;
        int source2 = 2;
        int destination2 = 2;
        int source_negative1 = -1;
        int destination_negative1 = -1;

        // #1 - BAD_PARAMS
        // trying to update a null pointer
        returnValue = Solution.updateUserHop(null);
        assertNotNull(returnValue);
        assertEquals(BAD_PARAMS, returnValue);

        // #2 - NOT_EXISTS
        // trying to update user when user doesn't exist in DB
        User user1 = new User(id1, source1, destination2);
        returnValue = Solution.updateUserHop(user1);
        assertNotNull(returnValue);
        assertEquals(NOT_EXISTS, returnValue);

        //add Hop
        Hop testHop1 = new Hop(source1, destination2);
        returnValue = Solution.addHop(testHop1);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);
        //add User
        returnValue = Solution.addUser(user1);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        // #3 - BAD_PARAMS (Again...)
        //update user to non-existing illegal hop (negative source)
        User user1_negative_source = new User(id1, source_negative1, destination1);
        returnValue = Solution.updateUserHop(user1_negative_source);
        assertNotNull(returnValue);
        assertEquals(BAD_PARAMS, returnValue);
        //update user to non-existing illegal hop (negative destination)
        User user1_negative_destination = new User(id1, source1, destination_negative1);
        returnValue = Solution.updateUserHop(user1_negative_destination);
        assertNotNull(returnValue);
        assertEquals(BAD_PARAMS, returnValue);
        //update user to non-existing illegal hop (source == destination)
        User user1_same_src_dest = new User(id1, source1, destination1);
        returnValue = Solution.updateUserHop(user1_same_src_dest);
        assertNotNull(returnValue);
        assertEquals(BAD_PARAMS, returnValue);

        // #4 - NOT_EXISTS (Again...)
        // trying to update user when user doesn't exist in DB (db not empty)
        User user2 = new User(id2, source1, destination2);
        returnValue = Solution.updateUserHop(user2);
        assertNotNull(returnValue);
        assertEquals(NOT_EXISTS, returnValue);

        //update user to non-existing hop (but legal)
        User user1_different_hop = new User(id1, source2, destination1);
        returnValue = Solution.updateUserHop(user1_different_hop);
        assertNotNull(returnValue);
        assertEquals(NOT_EXISTS, returnValue);

        // #5 - OK
        //update user to same hop
        returnValue = Solution.updateUserHop(user1);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        //add hop
        Hop testHop2 = new Hop(source2, destination1);
        returnValue = Solution.addHop(testHop2);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        //update user to different hop
        returnValue = Solution.updateUserHop(user1_different_hop);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        retUser = Solution.getUser(id1);
        assertNotNull(retUser);
        assertEquals(user1_different_hop, retUser);

        //update user to original hop
        returnValue = Solution.updateUserHop(user1);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        retUser = Solution.getUser(id1);
        assertNotNull(retUser);
        assertEquals(user1, retUser);

        //add another user
        returnValue = Solution.addUser(user2);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        //update different user to different hop
        User user2_different_hop = new User(id2, source2, destination1);
        returnValue = Solution.updateUserHop(user2_different_hop);
        assertNotNull(returnValue);
        assertEquals(OK, returnValue);

        //make sure other user is not affected by id2 change.
        retUser = Solution.getUser(id1);
        assertNotNull(retUser);
        assertEquals(user1, retUser);
    }
}