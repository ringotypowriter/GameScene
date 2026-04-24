package skypixeldev.gamescene;

import java.util.Objects;

public class Namespace {
    private String key;
    private String value;

    public Namespace(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Namespace(String[] array){
        this(array[0],array[1]);
    }

    public Namespace(String raw){
        this(raw.split(":"));
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + ":" + value;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Namespace namespace = (Namespace) o;
        return Objects.equals(key, namespace.key) &&
                Objects.equals(value, namespace.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
