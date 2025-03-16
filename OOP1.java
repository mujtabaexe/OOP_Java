class Pen{
    String color;
    String type;

    public void write(){
        System.out.println("Writing");
    }
    public void printingColor(){
        System.out.println(this.color);
    }
    public void printingType(){
        System.out.println(this.type);
    }
}

class OOP1{
    public static void main(String[] args) {
        Pen pen1 = new Pen();
        pen1.color = "Red";
        pen1.type = "Gel pen";
        pen1.printingColor();
        pen1.printingType();
        pen1.write();
        
        Pen pen2 = new Pen();
        pen2.color = "black";
        pen2.type = "Ink Pen";
        pen2.printingColor();
        pen2.printingType();
        pen2.write();

        // Directly passing pen1 attributes to pen3

        Pen pen3 = pen1;
        pen3.printingColor();
        pen3.printingType();

        // So pen3 points to the same attributes as pen1
        pen3.color = "Green";
        pen1.printingColor();
    }

}