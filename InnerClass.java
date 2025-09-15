public class InnerClass{
    
    static class Test{
        String name;

        public Test(String name){
            this.name = name;
        }
        
    }
    
    public static void main(String[] args) {

        Test a = new Test("Mujtaba");
        Test b = new Test("Ahmad");

        System.out.println(a.name);
        System.out.println(b.name);

        
    }
}



// public class InnerClass {

//     class Test {
//         String name;

//         public Test(String name) {
//             this.name = name; // Correct assignment
//         }
//     }

//     public static void main(String[] args) {
//         InnerClass outer = new InnerClass(); // Create outer class instance

//         // Create inner class instances using outer
//         InnerClass.Test a = outer.new Test("Mujtaba");
//         InnerClass.Test b = outer.new Test("Ahmad");

//         // Print names
//         System.out.println(a.name); // Mujtaba
//         System.out.println(b.name); // Ahmad
//     }
// }
