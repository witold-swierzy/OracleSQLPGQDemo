package pg.oracle;

import oracle.pg.rdbms.GraphServer;
import oracle.pgx.api.*;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import oracle.pgx.config.*;
import org.json.*;
public class Main {
    static String PQ_EXECUTION_MODE;
    static String PQ_PGX_URL;
    static String PQ_USERNAME;
    static String PQ_PASSWORD;
    static String PQ_QUERY;
    static int PQ_EXECUTIONS;
    static String PQ_JDBC_URL;
    static String PQ_TOKEN;
    public static void initDemo() {
        PQ_EXECUTION_MODE = System.getenv("PQ_EXECUTION_MODE").replace("\"","");
        //PQ_EXECUTION_MODE = "PQ_PGX_MODE";
        //PQ_EXECUTION_MODE = "PQ_REST_MODE";
        //PQ_EXECUTION_MODE = "PQ_DB_MODE";
        PQ_USERNAME       = System.getenv("PQ_USERNAME").replace("\"","");
        PQ_PASSWORD       = System.getenv("PQ_PASSWORD").replace("\"","");
        PQ_QUERY          = System.getenv("PQ_QUERY").replace("\"","");
        PQ_EXECUTIONS     = Integer.valueOf(System.getenv("PQ_EXECUTIONS").replace("\"",""));
        //PQ_EXECUTIONS     = 100;
        if ( PQ_EXECUTION_MODE.equals("PQ_DB_MODE"))
            PQ_JDBC_URL       = System.getenv("PQ_JDBC_URL").replace("\"","");
        else
            PQ_JDBC_URL       = "N/A";
        if ( PQ_EXECUTION_MODE.equals("PQ_PGX_MODE") || PQ_EXECUTION_MODE.equals("PQ_REST_MODE"))
            PQ_PGX_URL        = System.getenv("PQ_PGX_URL").replace("\"","");
        else
            PQ_PGX_URL        = "N/A";
    }

    public static void dbMode() {}

    public static void RESTMode() {}

    public static void PGXMode() {}

    public static void main(String[] args) {
        initDemo();
        System.out.println("Execution mode       : "+PQ_EXECUTION_MODE);
        System.out.println("PGX Server URL       : "+PQ_PGX_URL);
        System.out.println("JDBC URL             : "+PQ_JDBC_URL);
        System.out.println("Query                : "+PQ_QUERY);
        System.out.println("Number of executions : "+PQ_EXECUTIONS);
        System.out.println("Starting tests");
        long start = System.currentTimeMillis();
        if ( PQ_EXECUTION_MODE.equals("PQ_DB_MODE"))
            dbMode();
        if ( PQ_EXECUTION_MODE.equals("PQ_REST_MODE"))
            RESTMode();
        if ( PQ_EXECUTION_MODE.equals("PQ_PGX_MODE"))
            PGXMode();
        long end = System.currentTimeMillis();
        System.out.println("Summary : ");
        System.out.println("Execution mode           : "+PQ_EXECUTION_MODE);
        System.out.println("PGX Server URL           : "+PQ_PGX_URL);
        System.out.println("JDBC URL                 : "+PQ_JDBC_URL);
        System.out.println("Query                    : "+PQ_QUERY);
        System.out.println("Number of executions     : "+PQ_EXECUTIONS);
        System.out.println("Total elsapsed time (ms) : "+(end-start));
    }
}