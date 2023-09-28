package fr.uge.java.WeXcel;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class TableDAO {
    public void createTable(String tableName, Map<String, String> columnTitles){//add table info to table table and add its details to another table
        Objects.requireNonNull(tableName);
        Objects.requireNonNull(columnTitles);
        H2DataSource h2ds = new H2DataSource();
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        String sqlInsert = "INSERT INTO Tables (Name, Creation_Date, Modification_Date) " +
                "VALUES (?, ?, ?)";
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ");
        sql.append(tableName);
        sql.append(" (");
        var separator="";
        for (var entry :
                columnTitles.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            sql.append(separator);
            sql.append(key);
            sql.append(" ");
            sql.append(value);
            separator=", ";
        }
        sql.append(")");
        Statement st = null;
        PreparedStatement st2 = null;
        Connection conn= null;
        try {
            //h2ds.createDataSource();
            //Context ctx = new InitialContext();
            //DataSource ds = (DataSource) ctx.lookup("jdbc/dsName");
            DataSource ds = (DataSource) h2ds.createDataSource();
            conn = ds.getConnection();
            System.out.println("Connected to H2 embedded database");
            st = conn.createStatement();
            st2=conn.prepareStatement(sqlInsert);
            st2.setString(1, tableName);
            st2.setString(2, formattedDateTime);
            st2.setString(3, formattedDateTime);
            st2.execute();
            int r = st.executeUpdate(sql.toString());
        } catch (SQLException e) {
            while (e != null) {
                System.out.println(e.getSQLState());
                System.out.println(e.getMessage());
                System.out.println(e.getErrorCode());
                e = e.getNextException();
            }
            System.out.println("unable to create the table");
        } catch (NamingException e){
            e.printStackTrace();
        }
        finally {
            if (st != null) { try { st.close(); } catch (SQLException e) {} st = null; }
            if (conn != null) { try { conn.close(); } catch (SQLException e) {} conn = null;}
        }

    };
    public void addRow(Map<String, String> row, Table table){
        Objects.requireNonNull(row);
        Objects.requireNonNull(table);
        H2DataSource h2ds = new H2DataSource();
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(table.name());
        sql.append(" (");
        var separator="";
        for (var key :
                row.keySet()) {
            sql.append(separator);
            sql.append(key);
            separator=", ";
        }
        sql.append(") VALUES (");
        separator="";
        for (int i = 0; i < row.values().size(); i++) {
            sql.append(separator);
            sql.append("?");
            separator=", ";
        }
        sql.append(")");

    };
    public void addColumn(String tableTitle, Table table){};

    public void deleteTable(){};
    public void updateTable(){};
    public List<Table> getTables(){
        H2DataSource h2ds = new H2DataSource();
        List<Table> tables = new ArrayList<>();
        String rqst="select * from tables";
        Statement st = null;
        ResultSet rst=null;
        Connection conn= null;
        try {
            String name=null;
            String creationDate=null;
            String modificationDate=null;
            //h2ds.createDataSource();
            //Context ctx = new InitialContext();
            //DataSource ds = (DataSource) ctx.lookup("jdbc/dsName");
            DataSource ds = (DataSource) h2ds.createDataSource();;
            conn = ds.getConnection();
            System.out.println("Connected to H2 embedded database");

            st = conn.createStatement();
            rst=st.executeQuery(rqst);
            while (rst.next()){
                name=rst.getString("Name");
                creationDate=rst.getString("Creation_Date");
                modificationDate=rst.getString("Modification_Date");
                Table t = new Table(name, creationDate, modificationDate);
                tables.add(t);
            }
            return tables;
        } catch (SQLException e) {
            while (e != null) {
                System.out.println(e.getSQLState());
                System.out.println(e.getMessage());
                System.out.println(e.getErrorCode());
                e = e.getNextException();
            }
            System.out.println("unable to get the tables");
            return  null;
        } catch (NamingException e){
            e.printStackTrace();
            System.out.println("unable to get the tables");
            return null;
        }
        finally {
            if (st != null) { try { st.close(); } catch (SQLException e) {} st = null; }
            if (conn != null) { try { conn.close(); } catch (SQLException e) {} conn = null;}
        }
    }
}
