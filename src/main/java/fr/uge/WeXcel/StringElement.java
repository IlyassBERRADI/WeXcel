package fr.uge.WeXcel;

import java.util.Objects;

public record StringElement(String title, String value) {

    public StringElement{
        Objects.requireNonNull(title);
    }



}
