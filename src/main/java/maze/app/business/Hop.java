package maze.app.business;

/**
 * Created by dvird on 17/03/29.
 */
public class Hop {

    public static final Hop badHop;

    static {
        badHop = new Hop(-1, -1, -1);
    }

    int source;
    int destination;
    int load = 1;


    public Hop() {
    }

    public Hop (int source, int destination)
    {
        this.source = source;
        this.destination = destination;
    }

    public Hop(int source, int destination, int load) {
        this.source = source;
        this.destination = destination;
        this.load = load;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Hop{");
        sb.append("source=").append(source);
        sb.append(", destination=").append(destination);
        sb.append(", load=").append(load);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hop)) return false;

        Hop hop = (Hop) o;

        if (getSource() != hop.getSource()) return false;
        if (getDestination() != hop.getDestination()) return false;
        return getLoad() == hop.getLoad();
    }


    @Override
    public int hashCode() {
        int result = getSource();
        result = 31 * result + getDestination();
        result = 31 * result + getLoad();
        return result;
    }
}
