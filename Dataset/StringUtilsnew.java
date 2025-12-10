public class StringUtilsnew {

    public int getLength(String s) {
        int len = s.length();  
        return len;
    }

    public String toUpper(String s) {
        return s.toUpperCase();
    }

    public boolean isEmpty(String s) {
        if(s == null) 
            return true;
        return s.equals("");
    }

    public String reverse(String s) {
        return new StringBuilder(s).reverse().toString();
    }

    public static void main(String[] args) {
        StringUtils utils = new StringUtils();
        System.out.println("Length: " + utils.getLength("hello")); 
        System.out.println("Uppercase: " + utils.toUpper("world"));
        System.out.println("Is empty? " + utils.isEmpty(""));
    }
}
