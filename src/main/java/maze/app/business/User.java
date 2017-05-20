package maze.app.business;

/**
 * Created by dvird on 17/04/12.
 */
public class User {

    public static User badUser;

    static {
        badUser = new User(-1, -1,-1);
    }


    int id = -1;
    int source;
    int destination;

    public User()
    {

    }

    public User(int id, int source, int destination) {
        this.id = id;
        this.source = source;
        this.destination = destination;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (getId() != user.getId()) return false;
        if (getSource() != user.getSource()) return false;
        return getDestination() == user.getDestination();
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getSource();
        result = 31 * result + getDestination();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", source=").append(source);
        sb.append(", destination=").append(destination);
        sb.append('}');
        return sb.toString();
    }
}
