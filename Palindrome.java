public class Palindrome{

    public static void main(String[] args) {

        String s = "non";
        int n = 0;
        int m = s.length()-1;

        palindrome(n, m, s);
        
    }

    static void palindrome(int n, int m , String s) {
        if (n > m) {
            System.out.println("TRUE");
            return;
        }
        
        if(s.charAt(n) != s.charAt(m)) {
            System.out.println("NOT PALINDROME");
            return;
        }
        
        palindrome(n+1, m-1, s);
    }
}
