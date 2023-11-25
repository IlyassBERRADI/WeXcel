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
import java.util.regex.Pattern;

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
    public List<LinkedHashMap<String, Element>> getTable(@PathParam("id") int id, @PathParam("name") String name){
        String sqlQuery = "SELECT * FROM "+name;
        List<Object[]> resultList = entityManager
                .createNativeQuery(sqlQuery)
                .getResultList();

        List<ColumnEnt> resultList2 = entityManager
                .createQuery("SELECT c FROM ColumnEnt c WHERE c.table.id = :id", ColumnEnt.class)
                .setParameter("id", id)
                .getResultList();
        //entityManager
        //       .createNativeQuery(sqlQuery).setr
        List<LinkedHashMap<String, Element>> rows = new ArrayList<>();
        ColumnEnt[] columns = resultList2.toArray(ColumnEnt[]::new);
        //int i=0;

        for (var os :
                resultList) {
            LinkedHashMap<String, Element> row = new LinkedHashMap<>();
            for (int i = 1; i< os.length; i++) {
                /*System.out.println("start kukuhl");
                System.out.println(os[i]);*/
                Element e = new Element();
                e.setValue(os[i]+"");
                e.setType(columns[i-1].getType());
                row.put(columns[i-1].getName(), e);
            }



            rows.add(row);
        }
        return rows;
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void createTable(TableEnt table) {//tu met le resultat dans les champs
        try {//formule et tu le retourne
            Objects.requireNonNull(table);
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            table.setCreationDate(formattedDateTime);
            table.setModificationDate(formattedDateTime);
            /*System.out.println(table.getId()+" "+
                    table.getName()+" "+table.getCreationDate()+" "+
                    table.getModificationDate());*/

            entityManager.persist(table);
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

                type="VARCHAR(250)";
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
                separator = "";
                for (var val:
                     row.values()) {
                    sqlQuery2.append(separator);
                    sqlQuery2.append("'");
                    sqlQuery2.append(val.getValue());
                    sqlQuery2.append("'");
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
    public void addRows(@PathParam("name") String tableTitle, List<LinkedHashMap<String, Element>> elements){
        if (elements.isEmpty()){
            return;
        }
        StringBuilder sqlQuery2 = new StringBuilder();
        sqlQuery2.append("INSERT INTO ");
        sqlQuery2.append(tableTitle);
        sqlQuery2.append(" (");
        var separator = "";

        for (var entry :
                elements.get(0).keySet()) {
            sqlQuery2.append(separator);
            sqlQuery2.append(entry);
            separator = ", ";
        }
        sqlQuery2.append(") VALUES ");
        separator = "";
        for (var row :
                elements) {
            sqlQuery2.append(separator);
            sqlQuery2.append("(");
            separator = "";
            for (var val:
                    row.values()) {
                sqlQuery2.append(separator);
                sqlQuery2.append("'");
                sqlQuery2.append(val.getValue());
                sqlQuery2.append("'");
                separator = ", ";
            }
            sqlQuery2.append(")");
        }

        entityManager.createNativeQuery(sqlQuery2.toString()).executeUpdate();

    }

    @POST
    @Path("addColumn/{id}/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void addColumn(@PathParam("id") int id, @PathParam("name") String tableTitle, ColumnEnt column){
        TableEnt table = new TableEnt();
        table.setId(id);
        column.setTable(table);
        entityManager.persist(column);
        entityManager.createNativeQuery("ALTER TABLE "+tableTitle+" ADD COLUMN "+column.getName()+" VARCHAR(250)").executeUpdate();

    }

    @POST
    @Path("updateCell/{name}/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void updateCell(@PathParam("name") String tableName, @PathParam("id") int idRow, Cell cell){
        //regex (nombre, formule)
        if (cell.getElement().getType().equals("Nombre")){
            var pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
            var matcher = pattern.matcher(cell.getElement().getValue());
            if (!matcher.find()){
                //send error
                return;
            }
        }
        if (cell.getElement().getType().equals("Formule")){
            Deque<Character> stack = new LinkedList<>();
            var pattern = Pattern.compile("=.+");

            var matcher = pattern.matcher(cell.getElement().getValue());
            if (!matcher.find()){
                //send error
                return;
            }
            //well parenthesisized
            for (char c : cell.getElement().getValue().toCharArray()) {
                if (c=='('){
                    stack.push(c);
                }
                if (c==')' && stack.isEmpty()){
                    //error
                    return;
                } else if (c==')') {
                    stack.pop();
                }
            }
            if (!stack.isEmpty()){
                //error
                return;
            }

        }
        entityManager.createNativeQuery("UPDATE "+tableName+" SET "+cell.getName()+" = '"+cell.getElement().getValue()+"' WHERE ID="+idRow).executeUpdate();
    }


    @POST
    @Path("calculate/{id}/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public String calculateFormula(){
        return null;
    }

    @SuppressWarnings("unchecked")
    public Element getCell(Index ind, String nameTable, int idTable){//persist the formula and the result
        String sqlQuery = "SELECT * FROM "+nameTable+" WHERE ID = "+ind.nbLine();
        List<Object[]> result = entityManager
                .createNativeQuery(sqlQuery)
                .getResultList();
        List<ColumnEnt> resultList = entityManager
                .createQuery("SELECT c FROM ColumnEnt c WHERE c.table.id = :id", ColumnEnt.class)
                .setParameter("id", idTable)
                .getResultList();
        Element elt = new Element();
        elt.setType(resultList.get(ind.nbColumn()-1).getType());
        elt.setValue(result.get(0)[ind.nbColumn()]+"");
        return elt;
    }


    /*@POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteTable(Table table){
        entityManager.createNativeQuery("DROP TABLE "+table.getName()).executeUpdate();
    };*/
}
