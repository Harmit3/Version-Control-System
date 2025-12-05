public class StringUtils {

    // Returns string length
    public int getLength(String s) {
        return s.length();
    }

    // Converts to uppercase
    public String toUpper(String s) {
        return s.toUpperCase();
    }

    // Checks if string is empty
    public boolean isEmpty(String s) {
        return s == null ||
         s.equals("");
    }

    public void oldMethod() {
        System.out.println("old is gold");
    }

    public static void main(String[] args) {
        StringUtils utils = new StringUtils();
        System.out.println(utils.getLength("hello"));
        System.out.println(utils.toUpper("world"));
        System.out.println(utils.isEmpty(""));
    }
}
