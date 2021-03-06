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

    //todo: what to do if something goes wrong in finally blocks


    /**
     * Creates the tables and views for the solution
     */
    public static void createTables()
    {
        Connection connection = DBConnector.getConnection();
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
            pstmt = connection.prepareStatement("CREATE TABLE users\n" +
                    "(\n" +
                    "    id INTEGER,\n" +
                    "    source INTEGER,\n" +
                    "    destination INTEGER ,\n" +
                    "    PRIMARY KEY (id),\n" +
                    "    FOREIGN KEY (source, destination)\n" +
                    "    REFERENCES hops (source, destination),\n" +
                    "    CHECK (id > 0)," +
                    "    CHECK (source > 0)," +
                    "    CHECK (destination > 0)," +
                    "    CHECK (source <> destination)" +
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
            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS users CASCADE");
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS hops CASCADE");
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
            return ReturnValue.BAD_PARAMS;
        }
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
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
        try {
            pstmt = connection.prepareStatement("DELETE FROM hops " +
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
     * NOT_EXISTS if the given user's Hop does not exists //todo: according to FAQ update this case returns BAD_PARAMS
     * ERROR in case of other server error
     */
    public static ReturnValue addUser(User user)
    {
        ReturnValue ret;
        if (null == user)
        {
            return ReturnValue.BAD_PARAMS;
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
        ReturnValue ret;
        if (null == user)
        {
            return ReturnValue.BAD_PARAMS;
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
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("SELECT hops.source, hops.destination, " +
                    " (COUNT(users.id)+1)*hops.load AS \"actual_load\" " +
                    "FROM hops LEFT OUTER JOIN users " +
                    "ON (hops.source = users.source AND hops.destination = users.destination) " +
                    "GROUP BY (hops.source, hops.destination) " +
                    "HAVING count(users.id) >= " + usersThreshold +
                    " ORDER BY actual_load DESC " +
                    "Limit " + k + ";");
            ResultSet result = pstmt.executeQuery();
            topK = new ArrayList<>(k);
            while (result.next())
            {
                Hop hop = new Hop(result.getInt("source"),
                        result.getInt("destination"),
                        result.getInt("actual_load"));
                topK.add(hop);
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
        if (maxLength < 1)
        { //maxLength has to be greater or equal to 1
            return pathsList;
        }
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {

            //First, create a view of the hops with the actual load (according to the users table)
            CreateActualLoadView(connection, pstmt);

            //Because of maxLength > 0, there must be at least one level (which includes all the paths in size 1) of paths
            //Create first level path:
            CreateLevelOneViews(connection, pstmt, source, destination);

            //Create 2 views for each path length:
            //level<i>_path - includes final paths without cycles
            //level<i>      - includes paths that doesn't end in the given destination
            String destinationAttribute = CreateNextLevelsViews(connection, pstmt, source, destination, maxLength);

            //Get the result of the final statement which will include all of the paths, ordered by their total actual load
            ResultSet result = GetFinalStatementResult(connection, pstmt, maxLength, destinationAttribute);

            //Parse the result and insert it into the Path and PathList
            pathsList = new PathsList();
            //Add the paths to the pathsList
            while (result.next())
            {
                //Parse the first hop in the path. There must be a first hop if we got here, because a path is at least in size 1
                Path tempPath = new Path();
                Hop hop = new Hop(result.getInt("s"),
                        result.getInt("d1"));
                //Get the actual load of the hop
                PreparedStatement temp_pstmt = connection.prepareStatement("SELECT actual_load FROM hops_actual_load" +
                        " WHERE source = " + result.getInt("s") + " AND destination = " + result.getInt("d1") + ";");
                ResultSet temp_result = temp_pstmt.executeQuery();
                if (temp_result.isBeforeFirst()) {
                    temp_result.next();
                    hop.setLoad(temp_result.getInt("actual_load"));
                }
                tempPath.addHop(hop);

                //Get the next hops in the path
                for (int j=1; j<maxLength; j++){
                    Integer temp_source = result.getInt("d"+j);
                    Integer temp_destination = result.getInt("d"+(j+1));
                    if (result.wasNull()) break;
                    Hop temp_hop = new Hop(temp_source, temp_destination);
                    //Get the actual load of the hop
                    temp_pstmt = connection.prepareStatement("SELECT actual_load FROM hops_actual_load" +
                            " WHERE source = " + temp_source + " AND destination = " + temp_destination + ";");
                    temp_result = temp_pstmt.executeQuery();
                    temp_result.next(); //There must be next - means the hop is the hops_actual_load, because we took them from this table
                    temp_hop.setLoad(temp_result.getInt("actual_load"));
                    tempPath.addHop(temp_hop);
                }
                pathsList.addPath(tempPath);
                temp_pstmt.close();
            }

            //Drop all the created views, no need for them anymore (the CASCADE statement ensures it)
            pstmt = connection.prepareStatement("DROP VIEW hops_actual_load CASCADE ;");
            pstmt.execute();

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
     * @param connection A connection object to the DB
     * @param pstmt A prepare statement object
     * @param maxLength The max length determines the max levels
     * @param destinationAttribute A string with scheme of the last level view
     * @return The result of the final statement which will include all of the paths, ordered by their total actual load
     * @throws SQLException
     */
    private static ResultSet GetFinalStatementResult(Connection connection, PreparedStatement pstmt, int maxLength, String destinationAttribute) throws SQLException
    {
        String final_query = "";
        int i;
        for (i=maxLength; i>1; i-- )
        {
            final_query+="SELECT s," + destinationAttribute + " total_load FROM level"+i+"_path\n" +
                    "UNION ALL\n";
            destinationAttribute = destinationAttribute.replace("d"+i,"NULL as d"+i);
        }
        final_query+="SELECT s," + destinationAttribute + " total_load FROM level"+i+"_path\n"
                + "ORDER BY total_load ASC;";
        pstmt = connection.prepareStatement(final_query);
        ResultSet result = pstmt.executeQuery();
        return result;
    }

    /**
     * Create two views for path sized 1 (has only one hop)
     * explanation for each view is written above their SQL statement
     * @param connection A connection object to the DB
     * @param pstmt A prepare statement object
     * @param source source vertex
     * @param destination destination vertex
     * @throws SQLException
     */
    private static void CreateLevelOneViews(Connection connection, PreparedStatement pstmt, int source, int destination) throws SQLException
    {
        //Create a view: level1_path with paths in size 1, all the paths in this view are valid paths
        pstmt = connection.prepareStatement(
                "CREATE VIEW level1_path (s, d1, total_load) AS " +
                        "SELECT * " +
                        "FROM hops_actual_load " +
                        "WHERE hops_actual_load.source=" + source + " AND hops_actual_load.destination=" + destination +
                        ";"
        );
        pstmt.execute();

        //Create a view: level1 with paths in size that starts from the required source, but doesn't end in the destination,
        //                  this (and the conditions in the next levels) guarantees us that there are no cycles in a
        //                  bigger size paths
        pstmt = connection.prepareStatement(
                "CREATE VIEW level1 (s, d1, total_load) AS " +
                        "SELECT * " +
                        "FROM hops_actual_load " +
                        "WHERE hops_actual_load.source=" + source + " AND hops_actual_load.destination <> " + destination +
                        ";"
        );
        pstmt.execute();
    }


    /**
     * Create 2 views for each path length:
     * level<i>_path - includes final paths without cycles
     * level<i>      - includes paths that doesn't end in the given destination
     * @param connection A connection object to the DB
     * @param pstmt A prepare statement object
     * @param source source vertex
     * @param destination destination vertex
     * @param maxLength The max length determines the max levels
     * @return A string with scheme of the last level view
     * @throws SQLException
     */
    public static String CreateNextLevelsViews(Connection connection, PreparedStatement pstmt, int source, int destination, int maxLength) throws SQLException
    {
        String destinationAttribute = "d1, ";
        String destinationsToSelect = "";
        String differentFromDestinations = "";
        for (int i=1; i<maxLength; i++)
        {
            destinationAttribute += "d"+Integer.toString(i+1)+", ";
            destinationsToSelect += "h1.d" +i+", ";
            pstmt = connection.prepareStatement(
                    "CREATE VIEW level"+(i+1)+"_path (s, " + destinationAttribute+"total_load) AS " +
                            "SELECT h1.s," + destinationsToSelect + "h2.destination, h1.total_load+h2.actual_load " +
                            "FROM level"+i+" h1 LEFT OUTER JOIN hops_actual_load h2 " +
                            "ON h1.d"+ i + "=h2.source " +
                            "WHERE h2.destination="+ destination +" " +
                            ";"
            );
            pstmt.execute();

            differentFromDestinations += " AND h2.destination <> h1.d"+i+" ";
            pstmt = connection.prepareStatement(
                    "CREATE VIEW level"+(i+1)+" (s, " + destinationAttribute+"total_load) AS " +
                            "SELECT h1.s," + destinationsToSelect + "h2.destination, h1.total_load+h2.actual_load " +
                            "FROM level"+i+" h1 LEFT OUTER JOIN hops_actual_load h2 " +
                            "ON h2.destination <> h1.s AND h2.destination <> "+ destination + differentFromDestinations +
                            " WHERE h1.d"+i+"=h2.source" +
                            ";"
            );
            pstmt.execute();
        }
        return destinationAttribute;
    }

    /**
     * create a view with the actual loads according to the users in the DB
     * @param connection A connection object to the DB
     * @param pstmt A prepare statement object
     * @throws SQLException
     */
    public static void CreateActualLoadView(Connection connection, PreparedStatement pstmt) throws SQLException {
        pstmt = connection.prepareStatement(
                "CREATE VIEW hops_actual_load (source, destination, actual_load) AS " +
                        "SELECT hops.source, hops.destination, (COUNT(users.id)+1)*hops.load AS \"actual_load\" " +
                        "FROM hops LEFT OUTER JOIN users " +
                        "ON (hops.source = users.source AND hops.destination = users.destination) " +
                        "GROUP BY (hops.source, hops.destination) "
        );
        pstmt.execute();
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
