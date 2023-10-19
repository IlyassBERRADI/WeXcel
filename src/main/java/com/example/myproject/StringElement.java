package com.example.myproject;

import java.util.Objects;

public record StringElement(String title, String value) implements Element {

    public StringElement{
        Objects.requireNonNull(title);
    }



}
