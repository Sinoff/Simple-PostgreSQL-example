package maze.app.business;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dvird on 17/03/29.
 */
public class Path {

    ArrayList<Hop> path = new ArrayList<>();


    /**
     * adds a hop to path
     * @param hop Hop to add
     */
    public void addHop(Hop hop)
    {
        path.add(hop);
    }

    /**
     *
     * @return path's length
     */
    public int getLength()
    {
        return path.size();
    }

    /**
     *
     * @param hop Hop of interest
     * @return true if the path contains the hop, false otherwise
     */
    public boolean containsHop(Hop hop)
    {
        return path.contains(hop);
    }

    /**
     *
     * @param hop Hop of interest
     * @return the index of the hop
     */
    public int indexOfHop(Hop hop)
    {
        return path.indexOf(hop);
    }

    /**
     *
     * @param index index of the Hop of interest
     * @return Hop of interest
     */

    public Hop getHop(int index)
    {
        return path.get(index);
    }

    /**
     *
     * @return the actual load of the path
     */
    public int getLoad()
    {
        return path.stream().mapToInt(hop->hop.getLoad()).sum();
    }

    public boolean isEmpty()
    {
        return path.isEmpty();
    }

    /**
     *
     * @return a new list represting the path
     */
    public List<Hop> toList()
    {

        ArrayList list = new ArrayList();
        list.addAll(path);
        return list;
    }

    public Path clone()
    {
        Path newPath = new Path();
        newPath.path.addAll(path);
        return  newPath;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Path{");
        sb.append("path=").append(path);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Path)) return false;

        Path path1 = (Path) o;

        return path != null ? path.equals(path1.path) : path1.path == null;
    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }
}
