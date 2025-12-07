public class Test {
    public static void main(String[] args) {
        int compteur = 0;
        int x = 10; // ✅ int au lieu de Malek
        int y = 20; // ✅ int au lieu de Leiticia

        // Boucle while simple
        while (compteur < 5) {
            compteur++;
        }

        // Condition simple (pas de &&)
        while (x > 0) { // ✅ Condition simple
            x--;
        }
    }
}