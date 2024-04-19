package pg.oracle;

public class SQLPGQDemoException extends Exception {
    public SQLPGQDemoException(String mode, int code, String message) {
        super("Execution mode: "+mode+" ,Error Code: "+code+" ,Error Message : "+message);
    }
}
