package geneticalgo;

public class Test {
    public static void main(String[] args) {
        int[] test = new int[]{
                4, 4, 3, 5, 5, 5, 5, 4, 4, 6
        };

        int start, end;
        end = test.length - 1;

        while (end > -1) {
            start = end;
            int temp = test[end];

            for (int i = end - 1; i > -1; i--) {
                if (test[i] == temp) {
                    end--;
                } else {
                    break;
                }
            }

            System.out.println(end + "   " + start);
            end--;
        }

    }

}
