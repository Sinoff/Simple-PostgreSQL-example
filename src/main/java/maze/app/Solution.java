package maze.app;

import maze.app.business.*;
import maze.data.DBConnector;
import maze.data.PostgreSQLErrorCodes;

import java.sql.*;

import java.util.ArrayList;


/**
 * Created by dvird on 17/03/29.
 */
public class Solution {

    //todo: decide if we want to check input in JAVA...
    //todo: what to do if something goes wrong in finally blocks


    /**
     * Creates the tables and views for the solution
     */
    public static void createTables()
    {
        Connection connection = DBConnector.getConnection();
        //todo: update for hop according to requests in homework.
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("CREATE TABLE hops\n" +
                    "(\n" +
                    "    source INTEGER CHECK (source >= 1),\n" +
                    "    destination INTEGER CHECK (destination >= 1),\n" +
                    "    load INTEGER CHECK (load >= 1),\n" +
                    "    PRIMARY KEY (source, destination),\n" +
                    "    CHECK (source <> destination)" +
                    ")");
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            //todo: make sure old data in pstmt is overridden
            //not implicitly checking source != dest and source >=1, dest >=1, since it's checked by (referenced) hops.
            pstmt = connection.prepareStatement("CREATE TABLE users\n" +
                    "(\n" +
                    "    id INTEGER,\n" +
                    "    source INTEGER,\n" +
                    "    destination INTEGER ,\n" +
                    "    PRIMARY KEY (id),\n" +
                    "    FOREIGN KEY (source, destination)\n" +
                    "    REFERENCES hops (source, destination),\n" +
                    "    CHECK (id > 0)" +
                    ")");
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     *Clears the tables for the solution
     */
    public static void clearTables()
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("DELETE FROM users");
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            pstmt = connection.prepareStatement("DELETE FROM hops");
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Drops the tables from DB
     */
    public static void dropTables()
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS users");
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS hops");
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *  Adds a Hop to the database
     * @param hop
     * @return
     * OK in case of success,
     * BAD_PARAMS in case of illegal parameters,
     * ALREADY_EXISTS if hop already exists,
     * ERROR in case of database error
     */
    public static ReturnValue addHop(Hop hop)
    {
        ReturnValue ret;
        if (null == hop)
        {
            //todo: should this be changed to bad params?
            return ReturnValue.BAD_PARAMS;
        }
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("INSERT INTO hops" +
                    " VALUES (" + hop.getSource() + ", " + hop.getDestination() + ", " + hop.getLoad() + ");");
            pstmt.execute();
            ret = ReturnValue.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            ret = checkSQLException(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     *  Returns a Hop according to given source and destination
     * @param source The source vertex
     * @param destination The destination vertex
     * @return
     * Hop with the wanted source and destination
     * BadHop in case the hop not found, or in case of server error
     */
    public static Hop getHop(int source, int destination)
    {
        Hop hop;
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("SELECT * FROM hops" +
                    " WHERE source = " + source + " AND destination = " + destination + ";");
            ResultSet result = pstmt.executeQuery();
            if (result.isBeforeFirst()) {
                result.next();
                hop = new Hop(result.getInt("source"), result.getInt("destination"), result.getInt("load"));
            }
            else //hop with such source and destination doesn't exist
                hop = Hop.badHop;
        } catch (SQLException e) {
            e.printStackTrace();
            hop = Hop.badHop;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return hop;
    }


    /**
     *Updates a given existing Hop with new load
     * @param hop A Hop contains updated load
     * @return
     * OK in case of success
     * NOT_EXISTS in case of the hop not existing
     * BAD_PARAMS in case of bad input load
     * ERROR in case of server error
     *
     */
    public static ReturnValue updateHopLoad(Hop hop)
    {
        ReturnValue ret;
        if (null == hop)
        {
            //todo: should this be changed to bad params?
            return ReturnValue.BAD_PARAMS;
        }
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
//        try {
//            pstmt = connection.prepareStatement("SELECT * FROM hops" +
//                    " WHERE source = " + hop.getSource() + " AND destination = " + hop.getDestination() + ";");
//            ResultSet result = pstmt.executeQuery();
//            if (!result.isBeforeFirst()) //No such hop in the table
//                ret = ReturnValue.NOT_EXISTS;
//            else {
//                //todo: why not put those two sql statements in the same one (one statement and not 2) same applies to delete
//                pstmt = connection.prepareStatement("UPDATE hops " +
//                        " SET load = " + hop.getLoad() +
//                        " WHERE source = " + hop.getSource() + " AND destination = " + hop.getDestination() + ";");
//                pstmt.execute();
//                ret = ReturnValue.OK;
//            }
        try {
            pstmt = connection.prepareStatement("UPDATE hops " +
                    " SET load = " + hop.getLoad() +
                    " WHERE source = " + hop.getSource() + " AND destination = " + hop.getDestination() + ";");
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated != 0)
            {
                ret = ReturnValue.OK;
            }
            else //no such hop in the table
            {
                ret = ReturnValue.NOT_EXISTS;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ret = checkSQLException(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

	
	  /**
     * Deletes hop from the data base according to a given source and destination
     * @param source The source vertex
     * @param destination The destination vertex
     * @return
     * OK in case of success
     * NOT_EXISTS in case the hop does not exists
     * ERROR in case of other server error
     */
    public static ReturnValue deleteHop(int source, int destination)
    {
        ReturnValue ret;
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
//        try {
//            pstmt = connection.prepareStatement("SELECT * FROM hops" +
//                    " WHERE source = " + source + " AND destination = " + destination + ";");
//            ResultSet result = pstmt.executeQuery();
//            if (!result.isBeforeFirst()) //No such hop in the table
//                ret = ReturnValue.NOT_EXISTS;
//            else {
//                pstmt = connection.prepareStatement("DELETE FROM users " +
//                        " WHERE source = " + source + " AND destination = " + destination + ";");
//                pstmt.execute();
//                ret = ReturnValue.OK;
//            }
        try {
            pstmt = connection.prepareStatement("DELETE FROM users " +
                    " WHERE source = " + source + " AND destination = " + destination + ";");
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted != 0)
            {
                ret = ReturnValue.OK;
            }
            else //no such hop in the table
            {
                ret = ReturnValue.NOT_EXISTS;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ret = checkSQLException(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * Adds a user to the database
     * @param user
     * @return
     * OK in case of success,
     * BAD_PARAMS in case of illegal input parameters
     * ALREADY_EXISTS if user is already exists
     * NOT_EXISTS if the given user's Hop does not exists
     * ERROR in case of other server error
     */
    public static ReturnValue addUser(User user)
    {
        ReturnValue ret;
        if (null == user)
        {
            //todo: should this be changed to bad params?
            return ReturnValue.NOT_EXISTS;
        }
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("INSERT INTO users" +
                    " VALUES (" + user.getId() + ", " + user.getSource() + ", " + user.getDestination() + ");");
            pstmt.execute();
            ret = ReturnValue.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            ret = checkSQLException(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * Returns a User according to a given id
     * @param id input ID
     * @return
     * new User with the id of interest
     * badUser otherwise in the case of the user not found or server error
     */
    public static User getUser(int id)
    {
        User user;
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("SELECT * FROM users" +
                    " WHERE id = " + id +";");
            ResultSet result = pstmt.executeQuery();
            if (result.isBeforeFirst()) {
                result.next();
                user = new User(result.getInt("id"), result.getInt("source"), result.getInt("destination"));
            }
            else //id doesn't exist
                user = User.badUser;
        } catch (SQLException e) {
            e.printStackTrace();
            user =  User.badUser;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    /**
     * Update a user current hop
     * @param user a user with an updated hop
     * @return
     * OK in case of success
     * NOT_EXISTS if the user or the new hop do not exist
     * BAD_PARAMS in case of illegal parameters
     * ERROR in case of other server errors
     */
    public static ReturnValue updateUserHop(User user)
    {
        //todo: understand when can we get BAD_PARAMS
        ReturnValue ret;
        if (null == user)
        {
            //todo: should this be changed to bad params?
            return ReturnValue.NOT_EXISTS;
        }
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("UPDATE users " +
                    " SET source = " + user.getSource() + ", destination = " + user.getDestination() +
                    " WHERE id = " + user.getId() + ";");
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated != 0)
            {
                ret = ReturnValue.OK;
            }
            else //no such user in the table
            {
                ret = ReturnValue.NOT_EXISTS;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ret = checkSQLException(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * Deletes user from the data base according to a given id
     * @param userId the id of the user
     * @return
     * OK in case of success
     * NOT_EXISTS in case the user does not exist
     * ERROR in case of other server error
     */
    public static ReturnValue deleteUser(int userId)
    {
        ReturnValue ret;
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("DELETE FROM users " +
                    "WHERE id = " + userId + ";");
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated != 0)
            {
                ret = ReturnValue.OK;
            }
            else //no such user in the table
            {
                ret = ReturnValue.NOT_EXISTS;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ret = checkSQLException(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     *
     * @param k - maximum results
     * @param usersThreshold - users threshold to filter hops with
     * @return
     * A list of the top-k actual loaded hops, where in each hop there are at least (greater or equal) usersThreshold users
     * hop's actual load is (hop's load * (current users in hop + 1))
     * The returned hops load field is their actual load
     *
     */
    public static ArrayList<Hop> topKLoadedHops(int k, int usersThreshold)
    {
        ArrayList<Hop> topK;
        int i = 0;
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            //todo: how do you that "COUNT(*)+1" counts the users and not all the lines?
            //todo: you can also use "LIMIT K" to limit the result(from page 38 in lecture 4) (will remove the i<k condition)
            pstmt = connection.prepareStatement("SELECT hops.source, hops.destination, " +
                    " (COUNT(*)+1)*hops.load AS \"actual_load\" " +
                    "FROM hops LEFT OUTER JOIN users " +
                    "ON (hops.source = users.source AND hops.destination = users.destination) " +
                    "GROUP BY (hops.source, hops.destination) " +
                    "HAVING count(*) >= " + usersThreshold +
                    " ORDER BY actual_load DESC;");
            ResultSet result = pstmt.executeQuery();
            topK = new ArrayList<>(k);
            while (result.next() && i < k)
            {
                Hop hop = new Hop(result.getInt("source"),
                        result.getInt("destination"),
                        result.getInt("actual_load"));
                topK.add(hop);
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //todo - decided to return null if there was a problem - is this the correct behavior?
            topK = null;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return topK;
    }


    /**
     *
     * @param source source vertex
     * @param destination destination vertex
     * @param maxLength maximal length of the path in Hops
     * @return - A paths list containing all paths with length (in hops) which is less or equal to maxLength,
     * without cycles,
     * ordered by path's actual load
     */
    public static PathsList getAllPaths(int source, int destination, int maxLength)
    {
        PathsList pathsList = null;
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(
                    "CREATE VIEW hops_actual_load (source, destination, actual_load) AS " +
                    "SELECT hops.source, hops.destination, (COUNT(*)+1)*hops.load AS \"actual_load\" " +
                    "FROM hops LEFT OUTER JOIN users " +
                    "ON (hops.source = users.source AND hops.destination = users.destination) " +
                    "GROUP BY (hops.source, hops.destination) "
            );
            pstmt.execute();

            pstmt = connection.prepareStatement(
                    "CREATE VIEW level1_path (s, d1, total_load) AS " +
                            "SELECT * " +
                            "FROM hops_actual_load " +
                            "WHERE hops_actual_load.source=" + source + " AND hops_actual_load.destination=" + destination +
                            ";"
            );
            pstmt.execute();

            pstmt = connection.prepareStatement(
                    "CREATE VIEW level1 (s, d1, total_load) AS " +
                            "SELECT * " +
                            "FROM hops_actual_load " +
                            "WHERE hops_actual_load.source=" + source +
                            ";"
            );
            pstmt.execute();
            String destination_attribute = "d1, ";
            String dests = "";
            String diff_dest = "";
            for (int i=1; i<maxLength; i++)
            {
                destination_attribute += "d"+Integer.toString(i+1)+", ";
                dests += "h1.d" +i+", ";
                pstmt = connection.prepareStatement(
                        "CREATE VIEW level"+(i+1)+"_path (s, " + destination_attribute+"total_load) AS " +
                                "SELECT h1.s," + dests + "h2.destination, h1.total_load+h2.actual_load " +
                                "FROM level"+i+" h1 LEFT OUTER JOIN hops_actual_load h2 " +
                                "ON h1.d"+ i + "=h2.source " +
                                "WHERE h2.destination="+ destination +" " +
                                ";"
                );
                pstmt.execute();

                diff_dest += " AND h2.destination <> h1.d"+i+" ";
                pstmt = connection.prepareStatement(
                        "CREATE VIEW level"+(i+1)+" (s, " + destination_attribute+"total_load) AS " +
                                "SELECT h1.s," + dests + "h2.destination, h1.total_load+h2.actual_load " +
                                "FROM level"+i+" h1 LEFT OUTER JOIN hops_actual_load h2 " +
                                "ON h2.destination <> h1.s AND h2.destination <> "+ destination + diff_dest +
                                ";"
                );
                pstmt.execute();
                //todo: has to drop the views, commented for testing
//                pstmt = connection.prepareStatement(
//                        "DROP VIEW level"+i+";");
//                pstmt.execute();
            }

            String final_query = "";
            int i;
            for (i=maxLength; i>1; i-- )
            {
                final_query+="SELECT s," + destination_attribute + " total_load FROM level"+i+"_path\n" +
                        "UNION ALL\n";
                destination_attribute = destination_attribute.replace("d"+i,"NULL as d"+i);
            }
            final_query+="SELECT s," + destination_attribute + " total_load FROM level"+i+"_path\n"
                            + "ORDER BY total_load;";
            pstmt = connection.prepareStatement(final_query);
            ResultSet result = pstmt.executeQuery();

            pathsList = new PathsList();
            while (result.next())
            {
                //todo: complete this, not sure how to do it (there are NULLs in the paths) and if all the way to here is correct
                Path tempPath = new Path();
                Hop hop = new Hop(result.getInt("s"),
                        result.getInt("d1"),
                        result.getInt("total_load"));
                tempPath.addHop(hop);
                result.getInt("d2");
                int j = 1;
                while (!result.wasNull())
                {
                    Integer temp_source = result.getInt("d"+j);
                    Integer temp_destination = result.getInt("d"+(j+1));
                    Hop temp_hop = new Hop(temp_source, temp_destination);
                    pstmt = connection.prepareStatement("SELECT actual_load FROM hops_actual_load" +
                            " WHERE source = " + temp_source + " AND destination = " + temp_destination + ";");
                    ResultSet temp_result = pstmt.executeQuery();
                    temp_hop.setLoad(temp_result.getInt("actual_load"));
                    tempPath.addHop(temp_hop);
                    result.getInt("d"+(j+2));
                }
                pathsList.addPath(tempPath);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            pathsList = null;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return pathsList;
    }

    /**
     *
     * @param e SQL exception
     * @return - A ReturnValue indicating a problem. Possible ReturnValues:
     * BAD_PARAMS in case of illegal input parameters
     * ALREADY_EXISTS if unique value (or key) already exists
     * NOT_EXISTS if a foreign key does not exist
     * ERROR in case of other server error
     */
    private static ReturnValue checkSQLException (SQLException e)
    {
        //todo: make sure this is compatible with FAQ

        if(Integer.valueOf(e.getSQLState()) == PostgreSQLErrorCodes.INTEGRITY_CONSTRAINT_VIOLATION.getValue())
        {
             return ReturnValue.BAD_PARAMS;
        }
        else if(Integer.valueOf(e.getSQLState()) == PostgreSQLErrorCodes.RESTRICT_VIOLATION.getValue())
        {
            return ReturnValue.BAD_PARAMS;
        }
        else if(Integer.valueOf(e.getSQLState()) == PostgreSQLErrorCodes.NOT_NULL_VIOLATION.getValue())
        {
            return ReturnValue.BAD_PARAMS;
        }
        else if(Integer.valueOf(e.getSQLState()) == PostgreSQLErrorCodes.FOREIGN_KEY_VIOLATION.getValue())
        {
            return ReturnValue.NOT_EXISTS;
        }
        else if(Integer.valueOf(e.getSQLState()) == PostgreSQLErrorCodes.UNIQUE_VIOLATION.getValue())
        {
            return ReturnValue.ALREADY_EXISTS;
        }
        else if(Integer.valueOf(e.getSQLState()) == PostgreSQLErrorCodes.CHECK_VIOLIATION.getValue())
        {
            return ReturnValue.BAD_PARAMS;
        }
        else //another problem
        {
            return ReturnValue.ERROR;
        }
    }

}
