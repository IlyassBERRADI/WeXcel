package com.example.myproject;

import java.util.Objects;

public record FormulaElement(String title, String value) implements Element {

    public FormulaElement{
        Objects.requireNonNull(title);
    }

}
