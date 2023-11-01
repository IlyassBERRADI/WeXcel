package fr.uge.WeXcel;

import java.util.LinkedList;

public class Line {

    private int id;
    private LinkedList<Element> elements;
    private int idTable;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LinkedList<Element> getElements() {
        return elements;
    }

    public void setElements(LinkedList<Element> elements) {
        this.elements = elements;
    }

    public int getIdTable() {
        return idTable;
    }

    public void setIdTable(int idTable) {
        this.idTable = idTable;
    }
}
