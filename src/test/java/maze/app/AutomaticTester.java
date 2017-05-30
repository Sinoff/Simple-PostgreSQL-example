package maze.app;

import maze.app.business.Hop;
import maze.app.business.User;
import maze.app.business.ReturnValue;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static maze.app.business.ReturnValue.*;
import static org.junit.Assert.*;

/**
 * Created by rssinoff on 5/30/2017.
 */
public class AutomaticTester {

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
        Random random = new Random();
        User newUser;
        User retUser;
        Hop newHop;
        Hop retHop;
        int max_number = 100;
        ArrayList<Hop> hops = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
//        HashMap<Hop, Integer> hopActualLoad = new HashMap<>();


        for (int i = 0; i < 10000; i++) {
            int cmd = random.nextInt(8);
            switch (cmd) {
                case 0: //addHop
                {
                    newHop = new Hop(random.nextInt(max_number) + 1, random.nextInt(max_number) + 1, random.nextInt(max_number) + 1);
                    returnValue = Solution.addHop(newHop);
                    assertNotNull(returnValue);
                    if (newHop.getSource() == newHop.getDestination()) {
                        assertEquals(BAD_PARAMS, returnValue);
                    } else if (hopFindIgnoreLoad(hops, newHop) != -1) {
                        assertEquals(ALREADY_EXISTS, returnValue);
                    } else {
                        hops.add(newHop);
//                        hopActualLoad.put(new Hop(newHop.getSource(), newHop.getDestination()), newHop.getLoad());
                        assertEquals(OK, returnValue);
                    }
                    break;
                }
                case 1: //getHop
                {
                    newHop = new Hop(random.nextInt(max_number) + 1, random.nextInt(max_number) + 1);
                    retHop = Solution.getHop(newHop.getSource(), newHop.getDestination());
                    assertNotNull(retHop);
                    int index = hopFindIgnoreLoad(hops, newHop);
                    if (index != -1) {
                        assertEquals(hops.get(index), retHop);
                    } else {
                        assertEquals(Hop.badHop, retHop);
                    }
                    break;
                }
                case 2: //update load
                {
                    newHop = new Hop(random.nextInt(max_number) + 1, random.nextInt(max_number) + 1, random.nextInt(max_number) + 1);
                    returnValue = Solution.updateHopLoad(newHop);
                    assertNotNull(returnValue);
                    int index = hopFindIgnoreLoad(hops, newHop);
                    if (index == -1) {
                        assertEquals(NOT_EXISTS, returnValue);
                    } else {
                        hops.get(index).setLoad(newHop.getLoad());
//                        Hop temp = new Hop(newHop.getSource(), newHop.getDestination());
//                        int usersAmount = hopActualLoad.get(temp) / newHop.getLoad();
//                        hopActualLoad.put(temp, newHop.getLoad() * (usersAmount + 1));
                        assertEquals(OK, returnValue);
                    }
                    break;
                }
                case 3: //delete hop
                {
                    newHop = new Hop(random.nextInt(max_number) + 1, random.nextInt(max_number) + 1);
                    returnValue = Solution.deleteHop(newHop.getSource(), newHop.getDestination());
                    assertNotNull(returnValue);
                    int index = hopFindIgnoreLoad(hops, newHop);
                    if (index == -1) {
                        assertEquals(NOT_EXISTS, returnValue);
                    } else {
                        hops.remove(index);
//                        hopActualLoad.remove(new Hop(newHop.getSource(), newHop.getDestination()));
                        assertEquals(OK, returnValue);
                    }
                    break;
                }
                case 4: // add user
                {
                    newUser = new User(random.nextInt(max_number) + 1, random.nextInt(max_number) + 1, random.nextInt(max_number) + 1);
                    returnValue = Solution.addUser(newUser);
                    assertNotNull(returnValue);
                    if (newUser.getSource() == newUser.getDestination()) {
                        assertEquals(BAD_PARAMS, returnValue);
                    } else if (usersFindIgnoreHop(users, newUser) != -1) {
                        assertEquals(ALREADY_EXISTS, returnValue);
                    } else {
                        Hop temp = new Hop(newUser.getSource(), newUser.getDestination());
                        int hopIndex = hopFindIgnoreLoad(hops, temp);
                        if (hopIndex == -1) {
                            assertEquals(BAD_PARAMS, returnValue);
                        } else {
//                            int usersAmount = hopActualLoad.get(temp) / hops.get(hopIndex).getLoad() + 1;
//                            hopActualLoad.put(temp, hops.get(hopIndex).getLoad() * (usersAmount + 1));
                            users.add(newUser);
                            assertEquals(OK, returnValue);
                        }
                    }
                    break;
                }
                case 5: //getUser
                {
                    newUser = new User(random.nextInt(max_number) + 1, random.nextInt(max_number) + 1, random.nextInt(max_number) + 1);
                    retUser = Solution.getUser(newUser.getId());
                    assertNotNull(retUser);
                    int index = usersFindIgnoreHop(users, newUser);
                    if (index == -1) {
                        assertEquals(User.badUser, retUser);
                    } else {
                        assertEquals(users.get(index), retUser);
                    }
                    break;
                }
                case 6: //update user hop
                {
                    newUser = new User(random.nextInt(max_number) + 1, random.nextInt(max_number) + 1, random.nextInt(max_number) + 1);
                    returnValue = Solution.updateUserHop(newUser);
                    assertNotNull(returnValue);
                    int index = usersFindIgnoreHop(users, newUser);
                    if (index == -1) {
                        assertEquals(NOT_EXISTS, returnValue);
                    } else {
                        if (newUser.getSource() == newUser.getDestination())
                        {
                            assertEquals(BAD_PARAMS, returnValue);
                        }
                        else {
                            Hop temp = new Hop(newUser.getSource(), newUser.getDestination());
                            int hopIndex = hopFindIgnoreLoad(hops, temp);
                            if (hopIndex == -1) {
                                assertEquals(NOT_EXISTS, returnValue);
                            } else {
                                Hop org = new Hop(users.get(index).getSource(), users.get(index).getDestination());
                                int orgIndex = hopFindIgnoreLoad(hops, org);
                                if (orgIndex != -1) {
                                    users.get(index).setSource(newUser.getSource());
                                    users.get(index).setSource(newUser.getDestination());
    //                                int orgUsersAmount = hopActualLoad.get(org) / hops.get(orgIndex).getLoad() - 1;
    //                                hopActualLoad.put(org, hops.get(orgIndex).getLoad() * (orgUsersAmount + 1));
                                }
    //                            int newUsersAmount = hopActualLoad.get(temp) / hops.get(hopIndex).getLoad() + 1;
    //                            hopActualLoad.put(temp, hops.get(hopIndex).getLoad() * (newUsersAmount + 1));

                                assertEquals(OK, returnValue);
                            }
                        }
                    }
                    break;
                }
                case 7: //delete User
                {
                    newUser = new User(random.nextInt(max_number) + 1, random.nextInt(max_number) + 1, random.nextInt(max_number) + 1);
                    returnValue = Solution.deleteUser(newUser.getId());
                    assertNotNull(returnValue);
                    int index = usersFindIgnoreHop(users, newUser);
                    if (index == -1) {
                        assertEquals(NOT_EXISTS, returnValue);
                    } else {
                        Hop org = new Hop(users.get(index).getSource(), users.get(index).getDestination());
                        int orgIndex = hopFindIgnoreLoad(hops, org);
                        users.remove(index);
//                        int usersAmount = hopActualLoad.get(org) / hops.get(orgIndex).getLoad() - 1;
//                        hopActualLoad.put(org, hops.get(orgIndex).getLoad() * (usersAmount + 1));
                        assertEquals(OK, returnValue);
                    }
                    break;
                }
//                case 8: //topK
//                {
//                    int k = random.nextInt(max_number);
//                    int threshold = random.nextInt(max_number);
//                    Map<Hop,Integer> Expected = sortByValue(hopActualLoad);
//                    break;
//                }
            }
        }
    }

    int hopFindIgnoreLoad(ArrayList<Hop> array, Hop hop)
    {
        for (int i=0; i< array.size(); i++)
        {
            if (array.get(i).getSource() == hop.getSource() && array.get(i).getDestination() == hop.getDestination())
                return i;
        }
        return -1;
    }

    int usersFindIgnoreHop(ArrayList<User> array, User user)
    {
        for (int i=0; i< array.size(); i++)
        {
            if (array.get(i).getId() == user.getId())
                return i;
        }
        return -1;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }
}