package fr.uge.WeXcel;

import java.util.Objects;

public record FormulaElement(String title, String value) implements interf {

    public FormulaElement{
        Objects.requireNonNull(title);
    }

}
