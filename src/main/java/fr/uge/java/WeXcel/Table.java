package fr.uge.java.WeXcel;

import java.util.Objects;

public record Table(String name, String creationDate, String modificationDate) {


    public Table {
        Objects.requireNonNull(name);
        Objects.requireNonNull(creationDate);
        Objects.requireNonNull(modificationDate);
    }


}
