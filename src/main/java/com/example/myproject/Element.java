package com.example.myproject;

public sealed interface Element permits DoubleElement, StringElement, FormulaElement {
    public String title();
    public String value();
    static Element convertElement(Element e){


        if(e instanceof DoubleElement){
            return new DoubleElement(e.title(), e.value());
        } else if (e instanceof StringElement) {
            return new StringElement(e.title(), e.value());
        }
        else {
            return new FormulaElement(e.title(), e.value());
        }
        /*return switch (e){
            case DoubleElement -> new DoubleElement(e.title(), e.value());
            case StringElement -> new StringElement(e.title(), e.value());
            case FormulaElement -> new FormulaElement(e.title(), e.value());
        };*/
    }

}
