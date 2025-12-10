public class Loggernew {

    public void logInfo(String msg) {
        System.out.println("[INFO] " + msg);  
    }

    public void logError(String msg) {
        System.err.println("[ERROR] " + msg); 
    }

    public void logDebug(String msg) {
        System.out.println("[DEBUG] " + msg);
    }

    public static void main(String[] args) {
        Loggernew logger = new Loggernew();    
        logger.logInfo("App started");
        logger.logError("Error occurred");
        logger.logDebug("Debugging info");  
    }
}
