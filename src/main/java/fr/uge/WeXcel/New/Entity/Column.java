package fr.uge.WeXcel.New.Entity;

import java.util.List;
import java.util.Objects;

public record Column(String name, String type, List<String> values) {

    public Column{
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);

    }

}
