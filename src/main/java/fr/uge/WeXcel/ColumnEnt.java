package fr.uge.WeXcel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
//import jakarta.persistence.Id;

@Entity
@Table(name = "COLUMNS")
@Access(AccessType.FIELD)
public class ColumnEnt {



    @Id
    @Column(name = "ID")//, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;



    @Column(name = "NAME")
    private String name;

    @Column(name = "TYPE")
    private String type;

    @JsonBackReference
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TABLE", nullable = false)
    private TableEnt table;



    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setTable(TableEnt table) {
        this.table = table;
    }

    public TableEnt getTable() {
        return table;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }
}
