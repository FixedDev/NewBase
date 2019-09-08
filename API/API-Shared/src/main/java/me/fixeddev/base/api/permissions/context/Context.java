package me.fixeddev.base.api.permissions.context;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Context {
    private final String key;
    private List<String> values;

    private Context(String key, List<String> values) {
        this.key = key;
        this.values = new LinkedList<>(values);
    }

    private Context(String key) {
        this.key = key;
        this.values = new LinkedList<>();
    }

    public static Context of(String key, List<String> values) {
        return new Context(key, values);
    }

    public static Context of(String key) {
        return new Context(key);
    }

    public void addValue(String value) {
        Preconditions.checkNotNull(value, "The values to be added must be not null!");
        Preconditions.checkArgument(value.isEmpty(), "The values to be added must be not empty!");

        Preconditions.checkArgument(values.contains(value), "This context already has the value " + value);
        values.add(value);
    }

    public void removeValue(String value) {
        Preconditions.checkNotNull(value, "The values to be removed must be not null!");
        Preconditions.checkArgument(value.isEmpty(), "The values to be removed must be not empty!");

        Preconditions.checkArgument(!values.contains(value), "This context has the value " + value);

        values.remove(value);
    }

    public boolean hasValue(String value) {
        Preconditions.checkNotNull(value, "The values to be checked must be not null!");
        Preconditions.checkArgument(value.isEmpty(), "The values to be checked must be not empty!");

        boolean containsValue = values.contains(value);

        return containsValue || values.contains("*");
    }

    public List<String> getValues() {
        return new ArrayList<>(values);
    }

    public String getKey() {
        return key;
    }
}
