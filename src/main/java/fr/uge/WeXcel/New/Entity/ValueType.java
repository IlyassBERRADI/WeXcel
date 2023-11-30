package fr.uge.WeXcel.New.Entity;

import java.util.List;
import java.util.Objects;

public enum ValueType {
    NUMBER("DOUBLE PRECISION"),
    STRING("VARCHAR(255)"),
    FORMULA("VARCHAR(255)");

    private final String value;

    private ValueType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ValueType fromString(String text) {
        for (ValueType b : ValueType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

    public static ValueType fromContent(String text) {
        Objects.requireNonNull(text);

        if (text.startsWith("=")) {
            return ValueType.FORMULA;
        }
        try {
            Double.parseDouble(text);
            return ValueType.NUMBER;
        } catch (NumberFormatException e) {
            return ValueType.STRING;
        }
    }

    public static ValueType fromListContent(List<String> list) {
        return list.stream()
                .filter(Objects::nonNull) // Ignorer les valeurs null
                .map(ValueType::fromContent)
                .reduce((first, second) -> {
                    if (first == ValueType.FORMULA || second == ValueType.FORMULA) {
                        return ValueType.FORMULA;
                    } else if (first == ValueType.NUMBER && second == ValueType.NUMBER) {
                        return ValueType.NUMBER;
                    } else {
                        return ValueType.STRING;
                    }
                })
                .orElse(ValueType.STRING);
    }



}

