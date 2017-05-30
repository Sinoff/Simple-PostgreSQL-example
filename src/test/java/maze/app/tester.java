package maze.app;
import maze.app.business.*;

import java.util.ArrayList;

/**
 * Created by eskimolimun on 5/9/2017.
 */
public class tester {
    public static void main(String[] args) {
        try {
            Solution.createTables();
            testInsertHop();
            testGetHop();
            testUpdateHop();
            testDeleteHop();
            testClearTable();
            testCreateUser();
            testGetUser();
            testDeleteUser();
            testUpdateUserHop();
            testTopKLoaded();
            testGetPaths();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            Solution.dropTables();
        }
    }

    public static void testInsertHop() {
        Hop s1 = new Hop(1,2,1);
        Hop s2 = new Hop(0,2,1);
        Hop s3 = new Hop(1,2,1);
        assert (Solution.addHop(s1) == ReturnValue.OK);
        assert (Solution.addHop(s2) == ReturnValue.BAD_PARAMS);
        assert (Solution.addHop(s3) == ReturnValue.ALREADY_EXISTS);
        Solution.clearTables();
    }

    public static void testGetHop() {
        Hop s1 = new Hop(2,3,1);
        assert (Solution.addHop(s1) == ReturnValue.OK);
        assert (Solution.getHop(2,3).equals(new Hop(2,3,1)));
        assert (Solution.getHop(3,4).equals(Hop.badHop));
        Solution.clearTables();
    }

    public static void testUpdateHop() {
        Hop s1 = new Hop(2,3,1);
        assert (Solution.addHop(s1) == ReturnValue.OK);
        s1.setLoad(4);
        assert (Solution.updateHopLoad(s1) == ReturnValue.OK);
        assert (Solution.getHop(2,3).equals(s1));
        Hop s2 = new Hop(7,8,1);
        assert (Solution.updateHopLoad(s2) == ReturnValue.NOT_EXISTS);
        s1.setLoad(-8);
        assert (Solution.updateHopLoad(s1) == ReturnValue.BAD_PARAMS);
        assert (!Solution.getHop(2,3).equals(s1));
        Solution.clearTables();
    }

    public static void testDeleteHop(){
        Hop s1 = new Hop(2,3,1);
        assert (Solution.addHop(s1) == ReturnValue.OK);
        assert (Solution.deleteHop(2,3) ==  ReturnValue.OK);
        assert (Solution.deleteHop(2,3) ==  ReturnValue.NOT_EXISTS);
        assert (Solution.deleteHop(3,4) ==  ReturnValue.NOT_EXISTS);
        User u1 = new User(12,2,3);
        assert (Solution.addHop(s1) == ReturnValue.OK);
        assert(Solution.addUser(u1) == ReturnValue.OK);
        assert (Solution.deleteHop(2,3) ==  ReturnValue.NOT_EXISTS);
        Solution.clearTables();
    }


    public static void testClearTable(){
        Hop s1 = new Hop(1,2,1);
        assert (Solution.addHop(s1) == ReturnValue.OK);
        Solution.clearTables();
        assert (Solution.getHop(1,2) == Hop.badHop);
    }

    public static void testCreateUser(){
        Hop s1 = new Hop(2,3,1);
        User u1 = new User(1,2,3);
        assert (Solution.addUser(u1) == ReturnValue.NOT_EXISTS );
        assert (Solution.addHop(s1) == ReturnValue.OK );
//        assert (Solution.getUsersInHop(s1.getSource(),s1.getDestination()) == 0);
        assert (Solution.addUser(u1) == ReturnValue.OK);
        //assert (Solution.getUsersInHop(s1.getSource(),s1.getDestination()) == 1);
        assert (Solution.addUser(u1) == ReturnValue.ALREADY_EXISTS);
        u1.setId(-6);
        assert (Solution.addUser(u1) == ReturnValue.BAD_PARAMS);
        Solution.clearTables();
    }

    public static void testGetUser() {
        Hop s1 = new Hop(2, 5, 1);
        User u1 = new User(1, 2, 5);
        assert (Solution.getUser(u1.getId()) == User.badUser);
        Solution.addHop(s1);
        Solution.addUser(u1);
        User resUser = Solution.getUser(u1.getId());
        assert (resUser != User.badUser);
        assert (resUser.getId() == u1.getId());
        assert (resUser.getSource() == u1.getSource());
        assert (resUser.getDestination() == u1.getDestination());
        //assert (Solution.getUsersInHop(s1.getSource(),s1.getDestination()) == 1);
        Solution.clearTables();
    }


    public static void testDeleteUser(){
        Hop s1 = new Hop(2,3,1);
        User u1 = new User(1,2,3);
        assert (Solution.deleteUser(u1.getId()) == ReturnValue.NOT_EXISTS );
        Solution.addHop(s1);
        Solution.addUser(u1);
        assert (Solution.deleteUser(u1.getId()) == ReturnValue.OK );
        assert (Solution.getUser(u1.getId()) == User.badUser );
        Solution.clearTables();
    }
    public static void testUpdateUserHop(){
        Hop s1 = new Hop(1,3,8);
        Hop s2 = new Hop(5,9,4);
        User u1 = new User(12,1,3);
        assert(Solution.updateUserHop(u1) == ReturnValue.BAD_PARAMS);
        Solution.addHop(s1);
        Solution.addUser(u1);
        assert(Solution.updateUserHop(u1) == ReturnValue.OK);
        //assert (Solution.getUsersInHop(s1.getSource(),s1.getDestination()) == 1);
        u1.setSource(s2.getSource());
        u1.setDestination(s2.getDestination());
        assert(Solution.updateUserHop(u1) == ReturnValue.BAD_PARAMS);
        //assert (Solution.getUsersInHop(s2.getSource(),s2.getDestination()) == -1);
        //assert (Solution.getUsersInHop(s1.getSource(),s1.getDestination()) == 1);
        Solution.addHop(s2);
        assert(Solution.updateUserHop(u1) == ReturnValue.OK);
        //assert (Solution.getUsersInHop(s1.getSource(),s1.getDestination()) == 0);
        //assert (Solution.getUsersInHop(s2.getSource(),s2.getDestination()) == 1);
        Solution.clearTables();
    }

    public static void testGetPaths(){
        Hop s1 = new Hop(2,3,8);
        Hop s2 = new Hop(3,4,5);
        Hop s3 = new Hop(4,5,11);
        Hop s4 = new Hop(2,5,3);
        Hop s5 = new Hop(2,1,3);
        Hop s6 = new Hop(1,5,3);
        Solution.addHop(s1);
        Solution.addHop(s2);
        Solution.addHop(s3);
        Solution.addHop(s4);
        Solution.addHop(s5);
        Solution.addHop(s6);
        PathsList pathL = Solution.getAllPaths(2,5,3);
//        System.out.println(pathL.toString());
        assert(pathL.get(0).getHop(0).getDestination() == 5);
        assert(pathL.get(2).getHop(0).getDestination() == 3);
        pathL = Solution.getAllPaths(2,5,1);
        assert(pathL.get(0).getHop(0).getDestination() == 5);
        Solution.clearTables();
    }

    public static void testTopKLoaded()
    {
        Hop s1 = new Hop(1,3,8);
        Hop s2 = new Hop(5,9,5);
        Hop s3 = new Hop(6,7,11);
        Hop s4 = new Hop(5,2,3);
        Solution.addHop(s1);
        Solution.addHop(s2);
        Solution.addHop(s3);
        Solution.addHop(s4);
        User u1 = new User(1,1,3);
        User u2 = new User(2,5,9);
        User u3 = new User(3,5,9);
        User u4 = new User(4,5,9);
        User u5 = new User(5,5,2);
        Solution.addUser(u1);
        Solution.addUser(u2);
        Solution.addUser(u3);
        Solution.addUser(u4);
        Solution.addUser(u5);
        ArrayList<Hop> top = Solution.topKLoadedHops(3,1);
        assert(top.get(0).equals(s2));
        assert(top.get(1).equals(s1));
        assert(top.get(2).equals(s4));
        Solution.deleteUser(u1.getId());
        top = Solution.topKLoadedHops(3,1);
        assert(top.size() == 2);
        assert(top.get(0).equals(s2));
        assert(top.get(1).equals(s4));
        u5.setSource(s3.getSource());
        u5.setDestination(s3.getDestination());
        Solution.updateUserHop(u5);
        top = Solution.topKLoadedHops(3,1);
        assert(top.size() == 2);
        assert(top.get(0).equals(s3));
        assert(top.get(1).equals(s2));
        top = Solution.topKLoadedHops(1,1);
        top.remove(0);
        assert(top.isEmpty());
        top = Solution.topKLoadedHops(3,3);
        top.remove(0);
        assert(top.isEmpty());
        Solution.clearTables();
    }

}
