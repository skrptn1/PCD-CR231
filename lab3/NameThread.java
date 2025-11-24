package lab3;

public class NameThread extends Thread {

    private String text;

    public NameThread(String text) {
        this.text = text;
    }

    @Override
    public void run() {
        for (int i = 0; i < text.length(); i++) {
            System.out.print(text.charAt(i));
            try { Thread.sleep(100); } catch (Exception e) {}
        }
        System.out.println();
        
    }
}
