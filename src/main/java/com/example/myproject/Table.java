package com.example.myproject;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;

@Entity
@jakarta.persistence.Table(name = "TABLE")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = "getTables",
                query = "SELECT t FROM Tables t"),
})
public class Table {




    private int id;

    private String name;

    private String creationDate;

    private String modificationDate;

    @OneToOne
    @JoinColumn(name = "TABLES", referencedColumnName = "id")
    private int idTables;

    private LinkedList<Line> attributes;




    public Table() {

    }

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    public int getId(){return id;}

    public void setId(int id) {
        this.id = id;
    }

    @Basic(optional = false)
    @Column(name = "NAME", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Basic(optional = false)
    @Column(name = "CREATION_DATE", nullable = false)
    public String getCreationDate(){return creationDate;}


    public void  setCreationDate(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.creationDate= currentDateTime.format(formatter);
    }

    public String getModificationDate(){return modificationDate;}


    @Basic(optional = false)
    @Column(name = "MODIFICATION_DATE", nullable = false)
    public void setModificationDate(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.modificationDate= currentDateTime.format(formatter);
    }


    @Transient
    public LinkedList<Line> getAttributes(){
        return attributes;
    }

    @Transient
    public void setAttributes(LinkedList<Line> attributes){
        this.attributes=attributes;
    }


    public int getIdTables() {
        return idTables;
    }

    public void setIdTables(int idTables) {
        this.idTables = idTables;
    }
}
