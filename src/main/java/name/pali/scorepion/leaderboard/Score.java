package name.pali.scorepion.leaderboard;

import java.util.Objects;

public class Score {
    private String name;
    private int value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Score(String name, int value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score that = (Score) o;
        return value == that.value && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return "Score{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
