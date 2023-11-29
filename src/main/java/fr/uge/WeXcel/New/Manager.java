package fr.uge.WeXcel.New;

import fr.uge.WeXcel.New.Entity.Column;
import fr.uge.WeXcel.New.Entity.Reference;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.Collections;
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
        try {
            return em.createNativeQuery("SELECT t." + column + " FROM " + computedTableName + " t", String.class) // Gérer potentiel sql injection
//                   .setParameter(1, column)
//                    .setParameter(2, computedTableName)*/
                    .getResultList();
        } catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    private String getTableName(Long id) {
        try {
            return em.createQuery("SELECT r.name FROM Reference r WHERE r.id = :id", String.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
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
        try {
            em.createNativeQuery("CREATE TABLE " + newReference.getComputedName() + " (id INTEGER, idRow BIGINT, PRIMARY KEY(id, idRow), FOREIGN KEY(Id) REFERENCES Reference(Id))")
                    .executeUpdate();
        } catch (Exception e) {
            em.remove(newReference);
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Update cell content
     * Column's name need to be in uppercase in update query
     *
     * @param value new value (optional)
     */
    @POST
    @Path("updateCell/{id}/{column}/{row}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void updateCell(@PathParam("id") Long id, @PathParam("column") String column, @PathParam("row") Long row, String value) {
        String computedTableName = Reference.ComputeName(getTableName(id), id);
        var columnsData = getColumnsData(computedTableName);
        if (columnsData.stream().noneMatch(rowData -> rowData[0].equals(column.toUpperCase()))) {
            throw new BadRequestException("La colonne n'existe pas");
        }
        if (row > getLastIdRow(computedTableName)) {
            throw new BadRequestException("La ligne n'existe pas");
        }
        em.createNativeQuery("UPDATE " + computedTableName + " SET " + column.toUpperCase() + " = :value WHERE idRow = :row")
                .setParameter("value", (value == null || value.isEmpty()) ? null : value) // setParameter is a secure way to avoid SQL injection
                .setParameter("row", row)
                .executeUpdate();
    }

    private long getLastIdRow(String computedTableName) {
        return (long) em.createNativeQuery("SELECT COALESCE(MAX(idRow), 0) FROM " + computedTableName, Long.class)
                .getSingleResult();
    }

    /**
     * Add a row to the table
     * If you want to add a row with null values, send a Post request with no body
     *
     * @param id     reference's id
     * @param values values to add (optional)
     */
    @POST
    @Path("addRow/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void addRow(@PathParam("id") Long id, List<String> values) {
        String computedTableName = Reference.ComputeName(getTableName(id), id);
        var columnsData = getColumnsData(computedTableName);
        if (values == null) {
            values = new ArrayList<>(Collections.nCopies(columnsData.size() - 2, null));
        } else if (columnsData.size() - 2 != values.size()) {
            throw new BadRequestException("Le nombre de valeurs ne correspond pas au nombre de colonnes");
        }
        // Construire la requête SQL INSERT sans spécifier le nom des colonnes
        StringBuilder query = new StringBuilder("INSERT INTO " + computedTableName + " VALUES (?, ?, ");
        // Ajouter les marqueurs de paramètres pour le nombre dynamique de colonnes
        for (int i = 0; i < values.size(); i++) {
            query.append(i == 0 ? "?" : ", ?");
        }
        query.append(")");
        // Exécuter la requête SQL INSERT pour chaque valeur dans la liste
        var nativeQuery = em.createNativeQuery(query.toString());
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
        if (columnsData.stream().anyMatch(row -> row[0].equals(column.getName().toUpperCase()))) {
            throw new BadRequestException("La colonne existe déjà");
        } // Limitation de la base de données
        em.createNativeQuery("ALTER TABLE " + computedTableName + " ADD COLUMN " + column.getName() + " " + column.getType()) // Gérer potentiel injection SQL
                .executeUpdate();

        if (column.size() != 0 && column.size() != getLastIdRow(computedTableName)) {
            throw new BadRequestException("Le nombre de valeurs est différent du nombre de lignes");
        } else {
            for (int i = 0; i < column.size(); i++) {
                updateCell(id, column.getName(), (long) i + 1, column.getValue(i));
            }
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
