package fr.uge.java.WeXcel;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.jdbcx.JdbcConnectionPool;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Hashtable;

public class H2DataSource {

    public JdbcDataSource createDataSource() throws NamingException {
        //Hashtable<String, String> env = new Hashtable<>();
        //env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");

        //Context ctx = new InitialContext(env);
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:./test");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");
        return  dataSource;
        //ctx.bind("java:/comp/env/jdbc/dsName", dataSource);
    }


}
