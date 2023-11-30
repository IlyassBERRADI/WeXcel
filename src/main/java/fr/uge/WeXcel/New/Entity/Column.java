package fr.uge.WeXcel.New.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Column {
    private String name;
    private ValueType type;
    private List<String> values;

    // Constructeur par défaut nécessaire pour JPA
    public Column() {

    }
    public Column(String name, ValueType type) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        this.name = name;
        this.type = type;
        this.values = new ArrayList<>();
    }
    public Column(String name, ValueType type, List<String> values) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        Objects.requireNonNull(values);
        this.name = name;
        this.type = type;
        this.values = values;
    }
    public String getName() {
        return name;
    }
    public ValueType getType() {
        return type;
    }
    public List<String> getValues() {
        return new ArrayList<>(values);
    }
    public String getValue(int index) {
        return values.get(index);
    }
    public void addValue(String value) {
        values.add(value);
    }
    public int size() {
        return values.size();
    }

}
