package fr.uge.WeXcel;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.*;

@Entity(name = "TableEnt")
@Table(name = "TABLES")
@Access(AccessType.FIELD)
@NamedQueries({
        @NamedQuery(name = "getTables",
                query = "SELECT t FROM TableEnt t LEFT JOIN FETCH t.columns"),
})
public class TableEnt {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")//, nullable = false, updatable = false)
    private int id;

    @Basic(optional = false)
    @Column(name = "NAME")//, nullable = false)
    private String name;


    @Basic(optional = false)
    @Column(name = "DATE_CREATION", nullable = false)
    private String creationDate;

    @Basic(optional = false)
    @Column(name = "DATE_MODIFICATION", nullable = false)
    private String modificationDate;

    /*@OneToOne
    @JoinColumn(name = "TABLES", referencedColumnName = "id")
    private int idTables;*/

    @Transient
    private List<HashMap<String, Element>> data = new ArrayList<>();

    @JsonManagedReference
    @OneToMany( mappedBy = "table", cascade=CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ColumnEnt> columns = new ArrayList<>();

//    private HashMap<String, String> attributes; //column name + column type




    public TableEnt() {

    }



    public int getId(){return id;}

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getCreationDate(){return creationDate;}


    public void  setCreationDate(String creationDate){
        /*LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.creationDate= currentDateTime.format(formatter);*/
        this.creationDate=creationDate;
    }


    public String getModificationDate(){return modificationDate;}



    public void setModificationDate(String modificationDate){
        /*LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.modificationDate= currentDateTime.format(formatter);*/
        this.modificationDate=modificationDate;
    }

    public void setColumns(List<ColumnEnt> columns) {
        this.columns = columns;
    }

    public List<ColumnEnt> getColumns() {
        return columns;
    }

    public List<HashMap<String, Element>> getData() {
        return data;
    }

    public void setData(List<HashMap<String, Element>> data) {
        this.data = data;
    }
/*public HashMap<String, String> getAttributes(){
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes){
        this.attributes=attributes;
    }*/


    /*public int getIdTables() {
        return idTables;
    }

    public void setIdTables(int idTables) {
        this.idTables = idTables;
    }*/
}
