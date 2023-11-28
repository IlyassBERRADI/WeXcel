package fr.uge.WeXcel.New;

import fr.uge.WeXcel.New.Entity.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import fr.uge.WeXcel.New.Entity.Reference;

import java.util.List;

@Path("api") // Chemin de base du service web
public class Manager {
    @PersistenceContext(unitName = "pu1")
    private EntityManager em;

    @GET
    @Path("references")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Reference> getReferences() {
        return em.createQuery("SELECT r FROM Reference r", Reference.class).getResultList();
    }

    private List<String> getColumnContent(String computedTableName, String column) {
        return em.createNativeQuery("SELECT t." + column + " FROM " + computedTableName + " t", String.class)
                .getResultList();
    }

    private String getTableName(Long id) {
        return em.createQuery("SELECT r.name FROM Reference r WHERE r.id = :id", String.class)
                .setParameter("id", id)
                .getSingleResult(); // Utilise JPQL( orienté objet et spécifique à JPA) pour obtenir le nom de la table
    }

    private List<Object[]> getColumnsData(String tableName) {
        return em.createNativeQuery("SHOW COLUMNS FROM " + tableName).getResultList();
    }

    @GET
    @Path("content/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Column> GetContent(@PathParam("id") Long id) {
        String computedTableName = Reference.ComputeName(getTableName(id), id);
        List<Object[]> columnsData = getColumnsData(computedTableName);

        // Créer une liste de colonnes à partir des résultats de la requête native
        return columnsData.stream().skip(2)
                .map(row -> new Column((String) row[0], (String) row[1], getColumnContent(computedTableName, (String) row[0])))
                .toList();
    }


    /**
     * Créer une nouvelle référence
     * JSON envoyé par le client : {
     * "name": "Nouvelle Référence",
     * "creationDate": "2023-01-01",
     * "lastModificationDate": "2023-01-01"
     * }
     *
     * @param newReference
     */
    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON) // Indique que la méthode accepte les données au format JSON
    @Transactional(Transactional.TxType.REQUIRED) // Indique que la méthode doit être exécutée dans une transaction
    public void addReference(Reference newReference) {
        em.persist(newReference);
        em.createNativeQuery("CREATE TABLE " + newReference.getComputedName() + " (id INTEGER, idRow BIGINT, InsertName VARCHAR(255), PRIMARY KEY (id, idRow), FOREIGN KEY(Id) REFERENCES Reference(Id))")
                .executeUpdate();
    }

    /**
     * Supprimer une référence
     *
     * column need to be .Touppercase
     * @param column nom de la colonne
     *
     */
    @POST
    @Path("updateCell/{id}/{column}/{row}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void updateCell(@PathParam("id") Long id, @PathParam("column") String column, @PathParam("row") Long row, String value) {
        String computedTableName = Reference.ComputeName(getTableName(id), id);
        var columnsData = getColumnsData(computedTableName);
        System.out.println(id + " " + column + " " + row + " " + value);
        if (columnsData.stream().noneMatch(rowData -> rowData[0].equals(column))) {
            System.out.println("La colonne n'existe pas");
            throw new BadRequestException("La colonne n'existe pas");
        }
        if (row > getLastIdRow(computedTableName)) {
            System.out.println("La ligne n'existe pas");
            throw new BadRequestException("La ligne n'existe pas");
        }
        em.createNativeQuery("UPDATE " + computedTableName + " SET " + column + " = ? WHERE idRow = ?")
                .setParameter(1, value)
                .setParameter(2, row)
                .executeUpdate();
    }

    private long getLastIdRow(String computedTableName) {
        return (long) em.createNativeQuery("SELECT COALESCE(MAX(idRow), 0) FROM " + computedTableName, Long.class)
                .getSingleResult();

    }

    @POST
    @Path("addRow/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void addRow(@PathParam("id") Long id, @DefaultValue("[]") List<String> values) {
        String computedTableName = Reference.ComputeName(getTableName(id), id);
        var columnsData = getColumnsData(computedTableName);

        if (columnsData.size() - 2 != values.size()) {
            throw new BadRequestException("Le nombre de valeurs ne correspond pas au nombre de colonnes");
        }
        // Construire la requête SQL INSERT sans spécifier le nom des colonnes
        String query = "INSERT INTO " + computedTableName + " VALUES (?, ?, ";

        // Ajouter les marqueurs de paramètres pour le nombre dynamique de colonnes
        for (int i = 0; i < values.size(); i++) {
            query += (i == 0 ? "?" : ", ?");
        }

        query += ")";

        // Exécuter la requête SQL INSERT pour chaque valeur dans la liste
        var nativeQuery = em.createNativeQuery(query);
        nativeQuery.setParameter(1, id);
        nativeQuery.setParameter(2, getLastIdRow(computedTableName) + 1);

        // Ajouter les valeurs comme paramètres individuels
        for (int i = 0; i < values.size(); i++) {
            nativeQuery.setParameter(3 + i, values.get(i));
        }

        nativeQuery.executeUpdate();
    }


    @POST
    @Path("addColumn/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void addColumn(@PathParam("id") Long id, Column column) {
        String computedTableName = Reference.ComputeName(getTableName(id), id);
        var columnsData = getColumnsData(computedTableName);

        if (columnsData.stream().anyMatch(row -> row[0].equals(column.name()))) {
            throw new BadRequestException("La colonne existe déjà");
        } // Limitation de la base de données
        if(column.values().size() != getLastIdRow(computedTableName)){
            throw new BadRequestException("Le nombre de valeurs est différent du nombre de lignes");
        }

        em.createNativeQuery("ALTER TABLE " + computedTableName + " ADD COLUMN " + column.name() + " " + column.type())
                .executeUpdate();
        for (int i = 0; i < column.values().size(); i++) {
            updateCell(id, column.name().toUpperCase(), (long) i + 1, column.values().get(i));
        }
    }

    @DELETE
    @Path("/delete/{id}")
    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteReference(@PathParam("id") Long id) {
        // Récupérer le nom de la table associée à la référence
        String computedTableName = Reference.ComputeName(getTableName(id), id);
        // Supprimer la table associée
        em.createNativeQuery("DROP TABLE IF EXISTS " + computedTableName)
                .executeUpdate();

        // Supprimer la référence
        em.createQuery("DELETE FROM Reference r WHERE r.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}
