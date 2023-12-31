package fr.uge.WeXcel.New;

import fr.uge.WeXcel.New.Entity.Column;
import fr.uge.WeXcel.New.Entity.Reference;
import fr.uge.WeXcel.New.Entity.ValueType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class contains the different methods that helps do the mapping the database tables
 * to java classes
 */
@Path("api") // Chemin de base du service web
public class Manager {

    /**
     * This attribute manages the entities, operations and transactions
     */
    @PersistenceContext(unitName = "pu1")
    private EntityManager em;


    /**
     * This method gets the data of all the tables
     *
     * @return List of all tables' data
     */
    @GET
    @Path("references")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Reference> getReferences() {
        return em.createQuery("SELECT r FROM Reference r", Reference.class).getResultList();
    }


    /**
     * Retrieves a certain column data from the database
     *
     * @param computedTableName The table's name
     * @param column The columns name
     * @return List of the column's data
     */
    @SuppressWarnings("unchecked")
    private List<String> getColumnContent(String computedTableName, String column) {
        try {
            return (List<String>) em.createNativeQuery("SELECT t." + column + " FROM " + computedTableName + " t", String.class) // Gérer potentiel sql injection
                    .getResultList();
        } catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
    }


    /**
     * Selects the table's name from the database
     *
     * @param id The table's id
     * @return the table's name
     */
    private String getTableName(Long id) {
        try {
            return em.createQuery("SELECT r.name FROM Reference r WHERE r.id = :id", String.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
    }


    /**
     * Gets the table's columns' names and types
     *
     * @param tableName The table's name
     * @return List of each column and its data
     */
    private ArrayList<String[]> getColumnsData(String tableName) {
        var result = em.createNativeQuery("SHOW COLUMNS FROM " + tableName).getResultList();
        ArrayList<String[]> columnsData = new ArrayList<>();
        for (Object obj : result) {
            Object[] row = (Object[]) obj;
            if (row != null && row.length > 1) {
                String columnType = String.valueOf(row[1]);
                if ("CHARACTER VARYING(255)".equals(columnType)) {
                    row[1] = "VARCHAR(255)";
                }
                columnsData.add(Arrays.copyOf(row, row.length, String[].class));
            }

        }
        return columnsData;
    }


    /**
     * Gets the table's data by retrieving the data of each column
     *
     * @param id The tables id
     * @return List of Column entities
     */
    @GET
    @Path("content/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Column> GetContent(@PathParam("id") Long id) {
        String computedTableName = Reference.ComputeName(getTableName(id), id);
        var columnsData = getColumnsData(computedTableName);

        return columnsData.stream().skip(2)
                .map(row -> {
                    String columnName = row[0];
//                    String columnType = row[1];
                    List<String> columnContent = getColumnContent(computedTableName, columnName);
                    ValueType valueType = ValueType.fromListContent(columnContent);

                    return new Column(columnName, valueType, columnContent);
                })
                .toList();
    }



    /**
     * Creates a new reference
     * JSON sent by the client : {
     * "name": "New Reference",
     * "creationDate": "2023-01-01",
     * "lastModificationDate": "2023-01-01"
     * }
     *
     * @param newReference Reference instance
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
     * Retrieves the last row's id of a certain table
     * @param computedTableName The table's name
     * @return Row's id
     */
    private Long getLastIdRow(String computedTableName) {
        return (long) em.createNativeQuery("SELECT COALESCE(MAX(idRow), -1) FROM " + computedTableName, Long.class)
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
        nativeQuery.setParameter(2, getLastIdRow(computedTableName) + 1); // Première Ligne index 0
        // Ajouter les valeurs comme paramètres individuels
        for (int i = 0; i < values.size(); i++) {
            nativeQuery.setParameter(3 + i, values.get(i));
        }
        nativeQuery.executeUpdate();
    }


    /**
     *Adds a new column to a certain table
     *
     * @param id Table's id
     * @param column Column entity
     */
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

    /**
     *Deletes a column from a certain table
     *
     * @param computedTableName Table's name
     * @param columnName Column's name
     */
    private void deleteColumn(String computedTableName, String columnName) {
        try {
            em.createNativeQuery("ALTER TABLE " + computedTableName + " DROP COLUMN " + columnName.toUpperCase())
                    .executeUpdate();
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }


    /**
     * Updates a column in case the type changes
     *
     * @param computedTableName Table's name
     * @param columnName Column's name
     * @param columnType Column's type
     * @param value Updated value
     * @param valueType Updated value's type
     * @param row The row's number of the updated value
     * @param columnDataIndex The column's index
     * @param columnsData The column's data
     */
    private void updateColumn(String computedTableName, String columnName, ValueType columnType, String value, ValueType valueType, Long row, int columnDataIndex, List<String[]> columnsData) {
        // A partir de là, on sait que'il faut supprimer la colonne et la recréer

        var newColumnContent = getColumnContent(computedTableName, columnName);

        newColumnContent.set(row.intValue(), (value.isEmpty()) ? null : value);
        var newColumnType = ValueType.fromListContent(newColumnContent);

        var newColumn = new Column(columnName, newColumnType, newColumnContent);
        System.out.println("newColumn = " + newColumn);

        deleteColumn(computedTableName, columnName);
        System.out.println("ALTER TABLE " + computedTableName + " DROP COLUMN " + columnName.toUpperCase());

        if (columnDataIndex + 1 == columnsData.size()) { // Si la colonne est la dernière, on la rajoute à la fin
            em.createNativeQuery("ALTER TABLE " + computedTableName + " ADD COLUMN " + columnName.toUpperCase() + " " + newColumnType)
                    .executeUpdate();
        } else { // Sinon on la rajoute avant la colonne suivante
            em.createNativeQuery("ALTER TABLE " + computedTableName + " ADD COLUMN " + columnName.toUpperCase() + " " + newColumnType + " BEFORE " + columnsData.get(columnDataIndex + 1)[0])
                    .executeUpdate();
        }
        // Mettre à jour les valeurs de la nouvelle colonne
        for (int i = 0; i < newColumn.size(); i++) { // ressource killer
            em.createNativeQuery("UPDATE " + computedTableName + " SET " + columnName.toUpperCase() + " = ? WHERE idRow = ?")
                    .setParameter(1, newColumn.getValue(i))
                    .setParameter(2, (long) i)
                    .executeUpdate();
        }
    }

    /**
     * Update cell content
     * Column's name need to be in uppercase in update query
     *
     * @param value new value
     */
    @POST
    @Path("updateCell/{id}/{column}/{row}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void updateCell(@PathParam("id") Long id, @PathParam("column") String columnName, @PathParam("row") Long row, String value) {
        String computedTableName = Reference.ComputeName(getTableName(id), id);
        var columnsData = getColumnsData(computedTableName);
        var columnDataIndex = columnsData.stream().map(rowData -> rowData[0]).toList().indexOf(columnName.toUpperCase());
        if (columnDataIndex == -1) {
            throw new BadRequestException("La colonne n'existe pas");
        }
        if (row > getLastIdRow(computedTableName)) {
            throw new BadRequestException("La ligne n'existe pas");
        }
        var columnData = columnsData.get(columnDataIndex);
        var valueType = ValueType.fromContent(value); // Check if value is a valid ValueType (throw IllegalArgumentException if not)
        var columnType = ValueType.fromString(columnData[1]);
        if ((valueType == ValueType.NUMBER) != (columnType == ValueType.NUMBER)) {
            updateColumn(computedTableName, columnName, columnType, value, valueType, row, columnDataIndex, columnsData);
        } else {
            System.out.println("value = " + value);
            var newColumnContent = getColumnContent(computedTableName, columnName);
            newColumnContent.set(row.intValue(), (value.isEmpty()) ? null : value);
            System.out.println("newColumnContent = " + newColumnContent);
            var newColumnType = ValueType.fromListContent(newColumnContent);
            if (newColumnType != columnType) { // Le type de la colonne a changé après la suppression de la valeur
                System.out.println("newColumnType = " + newColumnType);
                updateColumn(computedTableName, columnName, columnType, value, newColumnType, row, columnDataIndex, columnsData);
            } else {
                em.createNativeQuery("UPDATE " + computedTableName + " SET " + columnName.toUpperCase() + " = :value WHERE idRow = :row")
                        .setParameter("value", (value.isEmpty()) ? null : value) // setParameter is a secure way to avoid SQL injection
                        .setParameter("row", row)
                        .executeUpdate();
            }
        }

    }


    /**
     * Deletes a table
     *
     * @param id Table's id
     */
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


