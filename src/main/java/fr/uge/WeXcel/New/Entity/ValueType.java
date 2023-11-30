package fr.uge.WeXcel.New.Entity;

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
}

