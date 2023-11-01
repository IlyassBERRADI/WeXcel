package fr.uge.WeXcel;

import java.util.Objects;

public record DoubleElement(String title, String value) implements interf {

    public DoubleElement{
        Objects.requireNonNull(title);
    }




}
