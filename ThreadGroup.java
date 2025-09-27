public class ThreadGroup {
    public static void main(String[] args){
        java.lang.ThreadGroup sys=
        Thread.currentThread() .getThreadGroup();
        sys.list();


    }
    }