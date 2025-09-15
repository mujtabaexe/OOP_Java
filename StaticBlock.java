public class StaticBlock{
    static int a = 4;
    static int b;

    // Will only run once, when the first object is created i.e. when the class is loadeded first time
    static{
        System.out.println("I am a static block");
        b = a*5;

    }

    public static void main(String[] args) {
        StaticBlock obj1 = new StaticBlock();

        System.out.println(StaticBlock.a + " " + StaticBlock.b);
        
        b+= 3;
        
        StaticBlock obj2 = new StaticBlock();
        System.out.println(StaticBlock.a + " " + StaticBlock.b);

    }

}