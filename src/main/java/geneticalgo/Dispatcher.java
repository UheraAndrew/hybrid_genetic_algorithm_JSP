package geneticalgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class Dispatcher {
    public static int N;//sizeOfPopulation
    public final static double P = 0.7;//PROBABILITY
    public final static Random r = new Random();

    static {
        r.setSeed(20);
    }


    public static void main(String[] args) {
        Dispatcher d = new Dispatcher();
//      Считуєм джейсон
//      d.run();
    }


    public int[][][] run(int[][] dataSet) {
        /**
         * in: dataSet  [job][machine] = int
         *
         * return: [machine][queue operations for current machine] = [job, time]
         *
         */


        return new int[][][]{{{1}}};
    }

}
