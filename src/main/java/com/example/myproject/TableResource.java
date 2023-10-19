package com.example.myproject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Path("table")
public class TableResource {
    @PersistenceContext(unitName = "pu1")
    private EntityManager entityManager;


    @GET
    @Path("ff")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Table> getTables(){
        return entityManager.createNamedQuery("getTables", Table.class).getResultList();
    }

    /*@GET
    @Path("name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Table getTable(@PathParam("name") String name){
        String tableName = name;
        String nativeQuery = "SELECT d FROM " + Table.class.getSimpleName() + " d";

        // Dynamically set the table name in the native SQL query
        nativeQuery = nativeQuery.replaceAll(Table.class.getSimpleName(), tableName);

        List<Object[]> resultList = entityManager.createNativeQuery("SELECT * FROM "+name).getResultList();
        *//*List<Map<String, Object>> mappedResults = new ArrayList<>();
        Object[] firstRow = resultList.get(0);*//*


        //List<Table> resultList2 = entityManager.createNativeQuery(nativeQuery, Table.class).getResultList();
        Query query = entityManager.createNamedQuery(namedQuery);

    // Execute the query and obtain the result as a List of Object[] arrays
        List<Object[]> resultList3 = query.getResultList();

    // Process the result and map it to a list of maps
        List<Map<String, Object>> mappedResults = new ArrayList<>();
        for (Object[] row : resultList) {
            Map<String, Object> data = new HashMap<>();

            // Use JPA's native SQL result set mapping to obtain column names
            List<String> columnNames = query.unwrap(Query.class)..keySet();

            for (int i = 0; i < columnNames.size(); i++) {
                String columnName = columnNames.get(i);
                Object columnValue = row[i];
                data.put(columnName, columnValue);
            }

            mappedResults.add(data);
        }
        for (Object[] row : resultList) {
            Map<String, Object> data = new HashMap<>();
            //Line l = (Line) row[0];
            data.put("column1", row[0]);
            data.put("column2", row[1]);
            // Map other columns as needed
            mappedResults.add(data);
        }

    }*/

    @POST
    @Path("create/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createTable(Table table){
        Objects.requireNonNull(table);
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        String sqlInsert = "INSERT INTO Tables (Name, Creation_Date, Modification_Date) " +
                "VALUES (?, ?, ?)";
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ");
        sql.append(table.getName());
        sql.append(" (");
        var separator="";

        for (var line :
                table.getAttributes() ) {
            var elts = line.getElements();
            String type;
            for (var elt :
                    elts) {
                if (elt.value() instanceof String){
                    type = "VARCHAR(50)";
                }
                else {
                    type = "INTEGER";
                }
                sql.append(separator);
                sql.append(elt.title());
                sql.append(" ");
                sql.append(type);
                separator=", ";
            }

        }
        sql.append(")");

        entityManager.createNativeQuery(sqlInsert).executeUpdate();




    }

    /*@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void addColumn(@PathParam("name") String tableTitle, Table table){
        entityManager.persist(pokemon);
    }*/

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteTable(Table table){
        entityManager.createNativeQuery("DROP TABLE "+table.getName()).executeUpdate();
    };
}
