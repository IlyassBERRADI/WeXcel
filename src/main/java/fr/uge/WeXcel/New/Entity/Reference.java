package fr.uge.WeXcel.New.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;
import java.util.Objects;

/**
 * Entity that represents a dynamic table's data which can be
 * persisted to the database
 */
@Entity // Entité JPA qui doit être persistée en base de données
@Table(name = "Reference")  // Table de la base de données
public class Reference { // pas possible en record car besoin setter

    /**
     * The table's id
     */
    @Id // Clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrément
    private Long id;

    /**
     * The table's name
     */
    private String name;

    /**
     * The table's creation date
     */
    private Date creationDate;

    /**
     * The table's last modification date
     */
    private Date lastModificationDate;

    // Constructeur par défaut nécessaire pour JPA
    public Reference() {
    }

    // Constructeur avec les champs nécessaires
    public Reference(String name, Date creationDate, Date lastModificationDate) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(creationDate);
        Objects.requireNonNull(lastModificationDate);
        this.name = name;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    /**
     * Replaces the table's name by a concatenation of its name an id separated by
     * underscores
     *
     * @param name table's name
     * @param id table's id
     * @return New name
     */
    public static String ComputeName(String name, Long id) {
        return name.replace(" ", "_") + "_" + id + "_";
    }
    // Getters et Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getComputedName() {
        return ComputeName(name, id);
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }
}
