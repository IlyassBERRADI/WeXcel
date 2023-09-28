package fr.uge.java.WeXcel;

import org.h2.jdbcx.JdbcDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        TableDAO tdao = new TableDAO();
        Map<String, String> columnTitles = new HashMap<>();
        columnTitles.put("col1", "VARCHAR(255)");
        columnTitles.put("col2", "VARCHAR(255)");
        tdao.createTable("table1", columnTitles);

    }
}
