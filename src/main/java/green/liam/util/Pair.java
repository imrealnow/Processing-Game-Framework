package green.liam.util;

public class Pair<T, U> {
    private final T first;
    private final U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public static <T, U> Pair<T, U> of(T first, U second) {
        return new Pair<>(first, second);
    }

    public T first() {
        return this.first;
    }

    public U second() {
        return this.second;
    }

    @Override
    public String toString() {
        return "Pair(" + this.first + ", " + this.second + ")";
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Pair))
            return false;
        Pair<?, ?> other = (Pair<?, ?>) obj;
        return this.first.equals(other.first) && this.second.equals(other.second) ||
                this.first.equals(other.second) && this.second.equals(other.first);
    }

    @Override
    public int hashCode() {
        return (this.first.hashCode() + this.second.hashCode()) * 31;
    }
}
