package maze.app.business;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dvird on 17/03/29.
 */
public class PathsList {

    ArrayList<Path> paths = new ArrayList<>();

    public void addPath(Path path)
    {
        paths.add(path);
    }

    public int size()
    {
        return paths.size();
    }

    public int indexOf(Path path)
    {
        return paths.indexOf(path);
    }

    public Path get(int index)
    {
        return paths.get(index);
    }

    public List<Path> toList()
    {
        return paths;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PathsList{");
        sb.append("paths=").append(paths);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PathsList)) return false;

        PathsList pathsList = (PathsList) o;

        return paths != null ? paths.equals(pathsList.paths) : pathsList.paths == null;
    }

    @Override
    public int hashCode() {
        return paths != null ? paths.hashCode() : 0;
    }
}
