package flyweight;

public class FlyWeightPattern {

    public static void runExample() {
        Integer a = Integer.valueOf(1);
        Integer b = Integer.valueOf(1);

        //Set a breakpoint below to observe the address of a and b
        System.out.println(a);
        System.out.println(b);
    }
}
