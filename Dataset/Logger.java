public class Logger {

    public void logInfo(String msg) {
        System.out.println("INFO: " + msg);
    }

    public void logError(String msg) {
        System.err.println("ERROR: " + msg);
    }

    public static void main(String[] args) {
        Logger l = new Logger();
        l.logInfo("Starting app");
        l.logError("Something went wrong");
    }
}
