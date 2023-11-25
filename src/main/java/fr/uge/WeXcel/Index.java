package fr.uge.WeXcel;

public record Index(int nbLine, int nbColumn) {
    public Index{
        if (nbLine<=0 || nbColumn<=0){
            throw new IllegalArgumentException();
        }
    }
}
