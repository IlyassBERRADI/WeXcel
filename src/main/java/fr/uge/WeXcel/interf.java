package fr.uge.WeXcel;

public sealed interface interf permits FormulaElement, DoubleElement {
    /*static void meth(interf i){
        switch (i){
            case FormulaElement s: {
                System.out.println("fff");
                return;
            }

            case DoubleElement s: System.out.println("kkk");
        }
    }

    static double getDoubleUsingSwitch(Object o) {
        return switch (o) {
            case Integer i -> i.doubleValue();
            case Float f -> f.doubleValue();
            case String s -> Double.parseDouble(s);
            default -> 0d;
        };
    }*/
}
