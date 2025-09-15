public class Sys {
    public static void main(String[] args) {
        Out o = new Out();
        Shystem myShystem = new Shystem(o);
        myShystem.out.println();
    }
}

class Shystem {
    Out out;

    public Shystem(Out out) {
        this.out = out;
    }
}

class Out {
    public void println() {
        System.out.println("Kuch nahi");
    }
}
