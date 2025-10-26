class Th2 extends Thread {
    int[] mas;

    Th2(int[] mas) {
        this.mas = mas;
    }

    @Override
    public void run() {
        System.out.println("Th2 - Condiția 2 (varianta 8):");
        int sumaProduselor = 0;
        int count = 0;
        int produs = 1;

        // Parcurgere de la sfârșit spre început
        for (int i = mas.length - 1; i >= 0; i--) {
            if (mas[i] % 2 != 0) {  // număr impar
                produs *= mas[i];
                count++;
                if (count == 2) {  // la fiecare 2 impare
                    sumaProduselor += produs;
                    produs = 1;
                    count = 0;
                }
            }
        }

        System.out.println("Suma produselor numerelor impare doua cate doua (de la ultimul): " + sumaProduselor);
    }
}