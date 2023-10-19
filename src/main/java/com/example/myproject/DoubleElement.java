package com.example.myproject;

import java.util.Objects;

public record DoubleElement(String title, String value) implements Element {

    public DoubleElement{
        Objects.requireNonNull(title);
    }




}
