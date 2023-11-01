package fr.uge.WeXcel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Path("table")
public class TableResource {
    @PersistenceContext(unitName = "pu1")
    private EntityManager entityManager;


    @GET
    @Path("tables")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Table> getTables(){
        return entityManager.createNamedQuery("getTables", Table.class).getResultList();
    }

    @GET
    @Path("{id}/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public List<HashMap<String, Element>> getTable(@PathParam("id") int id, @PathParam("name") String name){
        String sqlQuery = "SELECT * FROM "+name;
        String sqlQuery2 = "SELECT c FROM Columns c WHERE c.idTable="+id;
        List<Object[]> resultList = entityManager
                .createNativeQuery(sqlQuery)
                .getResultList();
        List<ColumnEnt> resultList2 = entityManager
                .createNativeQuery(sqlQuery2)
                .getResultList();
        //entityManager
        //       .createNativeQuery(sqlQuery).setr
        List<HashMap<String, Element>> rows = new ArrayList<>();
        ColumnEnt[] columns = resultList2.toArray(ColumnEnt[]::new);
        int i=0;
        for (Object[] os :
                resultList) {
            HashMap<String, Element> row = new HashMap<>();
            Element e = new Element();
            String val = (String) os[i];
            e.setValue(val);
            e.setType(columns[i].getType());
            row.put(columns[i].getName(), e);
            rows.add(row);
            i++;
        }
        return rows;
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void createTable(TableEnt table) {
        try {
            Objects.requireNonNull(table);
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            table.setCreationDate(formattedDateTime);
            table.setModificationDate(formattedDateTime);
            System.out.println(table.getId()+" "+
                    table.getName()+" "+table.getCreationDate()+" "+
                    table.getModificationDate());
            /*System.out.println(table.getColumns().size());
            for (var elt :
                    table.getColumns()) {
                //elt.setTable(table);
                System.out.println(elt.getId()+" "+elt.getType()+" "+elt.getName());
            }*/

            entityManager.persist(table);
            //entityManager.flush();
            System.out.println(";jbhkjhkjhn");
            /*for (var elt :
                    table.getColumns()) {
                entityManager.persist(elt);
            }*/
            System.out.println(";jbhkjhkjhn");
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("CREATE TABLE ");
            sqlQuery.append(table.getName());
            sqlQuery.append(" (");
            sqlQuery.append("ID SERIAL PRIMARY KEY,");
            var separator = "";

            for (var entry :
                    table.getColumns()) {
                var key = entry.getName();
                var value = entry.getType();
                String type;
                if (value.equals("Formule") || value.equals("Chaine")){
                    type="VARCHAR(250)";
                }
                else {
                    //type="DOUBLE PRECISION";
                    type="VARCHAR(250)";
                }
                sqlQuery.append(separator);
                sqlQuery.append(key);
                sqlQuery.append(" ");
                sqlQuery.append(type);
                separator = ", ";


            }
            
            sqlQuery.append(")");
            entityManager.createNativeQuery(sqlQuery.toString()).executeUpdate();
            StringBuilder sqlQuery2 = new StringBuilder();
            sqlQuery2.append("INSERT INTO ");
            sqlQuery2.append(table.getName());
            sqlQuery2.append(" (");
            separator = "";

            for (var entry :
                    table.getColumns()) {
                sqlQuery2.append(separator);
                sqlQuery2.append(entry.getName());
                separator = ", ";
            }
            sqlQuery2.append(") VALUES ");
            separator = "";
            for (var row :
                    table.getData()) {
                sqlQuery2.append(separator);
                sqlQuery2.append("(");
                for (var val:
                     row.values()) {
                    sqlQuery2.append(separator);
                    sqlQuery2.append(val.getValue());
                    separator = ", ";
                }
                sqlQuery2.append(")");
            }

            entityManager.createNativeQuery(sqlQuery2.toString()).executeUpdate();
        } catch (Exception e){
            throw new BadRequestException("Unable to create table" + table.getName());
        }


    }
    /*@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void addColumn(@PathParam("name") String tableTitle, Table table){
        entityManager.persist(pokemon);
    }*/


    @POST
    @Path("addRow/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void addRows(@PathParam("name") String tableTitle, HashMap<String, Element> elements){
        StringBuilder sqlQuery2 = new StringBuilder();
        sqlQuery2.append("INSERT INTO ");
        sqlQuery2.append(tableTitle);
        sqlQuery2.append(" (");
        var separator = "";

        for (var entry :
                elements.keySet()) {
            sqlQuery2.append(separator);
            sqlQuery2.append(entry);
            separator = ", ";
        }
        sqlQuery2.append(") VALUES (");
        separator = "";
        for (var val:
                elements.values()) {
            sqlQuery2.append(separator);
            sqlQuery2.append(val.getValue());
            separator = ", ";
        }
        sqlQuery2.append(")");

    }

    /*@POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteTable(Table table){
        entityManager.createNativeQuery("DROP TABLE "+table.getName()).executeUpdate();
    };*/
}
