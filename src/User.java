
public record User(String firstName, String lastName) implements Comparable<User> {
    @Override
    public int compareTo(User o) {
        return this.firstName.compareTo(o.firstName);
    }
}
