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
        //PQ_EXECUTION_MODE = "PQ_PGX_API_MODE";
        //PQ_EXECUTION_MODE = "PQ_PGX_REST_MODE";
        //PQ_EXECUTION_MODE = "PQ_ORDS_REST_MODE";
        //PQ_EXECUTION_MODE = "PQ_DB_MODE";
        PQ_USERNAME       = System.getenv("PQ_USERNAME").replace("\"","");
        //PQ_USERNAME = "NonExistingUser";
        PQ_PASSWORD       = System.getenv("PQ_PASSWORD").replace("\"","");
        //PQ_PASSWORD       = "IncorrectPassword";
        PQ_QUERY          = System.getenv("PQ_QUERY").replace("\"","");
        //PQ_QUERY            = "IncorrectQuery";
        //PQ_EXECUTIONS     = Integer.valueOf(System.getenv("PQ_EXECUTIONS").replace("\"",""));
        PQ_EXECUTIONS     = 1;
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

    public static void RESTPGXlogin() throws Exception {
        String body = "{\"username\":"+
                      "\""+PQ_USERNAME+"\""+
                      ",\"password\":"+
                      "\""+PQ_PASSWORD+"\""+
                      ",\"createSession\":true"+
                      ",\"source\":\"OracleSQLPGQDemo\"}";
        HttpClient pgxServer = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(PQ_PGX_URL + "/auth/token"))
                                         .header("Content-Type", "application/json")
                                         .POST(HttpRequest.BodyPublishers.ofString(body))
                                         .build();
        HttpResponse<String> response = pgxServer.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        JSONObject obj = new JSONObject(response.body());
        if ( status != 201 )
            throw new SQLPGQDemoException(PQ_EXECUTION_MODE,status,obj.toString());
        PQ_TOKEN = obj.getString("access_token");
    }

    public static void RESTPGXexecuteQuery() throws Exception {
        String body = "{\n"+
                      "   \"statements\": [\n"+
                      "      \""+PQ_QUERY+"\"\n"+
                      "   ],\n"+
                      "   \"driver\": \"SQL_IN_DATABASE\",\n"+
                      "   \"formatter\": \"GVT\",\n"+
                      "   \"visualize\": true\n"+
                      "}";
        HttpClient pgxServer = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(PQ_PGX_URL + "/v2/runQuery"))
                                         .header("Authorization", "Bearer " + PQ_TOKEN)
                                         .header("Content-Type", "application/json")
                                         .POST(HttpRequest.BodyPublishers.ofString(body))
                                         .build();
        for ( int i=1; i<=PQ_EXECUTIONS; i++) {
            HttpResponse<String> response = pgxServer.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            JSONObject obj = new JSONObject(response.body());
            if ( status > 299 || !obj.getJSONArray("results")
                                     .getJSONObject(0)
                                     .getBoolean("success") )
                throw new SQLPGQDemoException(PQ_EXECUTION_MODE,status,obj.toString());
            System.out.println("Execution #"+i+" completed successfully");
        }
    }

    public static void RESTPGXMode() {
        try {
            RESTPGXlogin();
            RESTPGXexecuteQuery();
        }
        catch (Exception e) {e.printStackTrace();}
    }

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
        if ( PQ_EXECUTION_MODE.equals("PQ_PGX_REST_MODE"))
            RESTPGXMode();
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