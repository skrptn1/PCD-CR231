public class RegexCifre {
    public static void main(String[] args) {
        String[] teste = {"123", "abc", "348", "-456", "3.14"};

        String cifre = "^[0-9]+$";

        for (String t : teste) {
            System.out.println(t + " -> " + t.matches(cifre));
        }
    }
}
