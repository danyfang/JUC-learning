public class Sleeper {
    public static void sleep(int n) {
        try {
            Thread.sleep(n * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
