package geneticalgo;

public class Test {
    public static void main(String[] args) {
        int one = 0;
        for (int i = 0; i < 1000; i++) {
            one += (int)(Configurations.random.nextDouble()/0.7) ^ 1;
        }
        System.out.println(one);

    }
}
