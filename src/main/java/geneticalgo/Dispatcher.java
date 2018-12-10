package geneticalgo;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class Dispatcher {
    public static int N;//sizeOfPopulation
    public final static double P = 0.7;//PROBABILITY
    public final static Random r = new Random();

    static {
        r.setSeed(20);
    }


    public static void main(String[] args) {
        try {
            String text = new String(Files.readAllBytes(Paths.get("src/main/resources/dataset.json")), StandardCharsets.UTF_8);
            JSONArray tests = new JSONArray(text);
            for (int i = 0; i < tests.length(); i++) {

                JSONObject test = (JSONObject) tests.get(i);
                JSONArray matrix = (JSONArray) test.get("data");
                int[][] dataSet = new int[matrix.length()][];
                for (int j = 0; j < matrix.length(); j++) {
                    JSONArray array = (JSONArray) matrix.get(j);
                    dataSet[j] = new int[array.length() / 2];
                    for (int k = 0; k < array.length(); k = k + 2) {
                        dataSet[j][array.getInt(k)] = array.getInt(k + 1);
                    }

                }
                for (int j = 0; j < dataSet.length; j++) {
                    for (int k = 0; k < dataSet[j].length; k++) {
                        System.out.print(dataSet[j][k]+" ");
                    }
                    System.out.println();

                }

                runTest(dataSet);
                System.exit(47);
            }

        } //      Считуєм джейсон
        catch (IOException e) {
            e.printStackTrace();
        }
//      d.run();
    }


    public static int runTest(int[][] dataSet) {
        /**
         * in: dataSet  [job][machine] = int
         *
         * return: [machine][queue operations for current machine] = [job, time]
         *
         */
        Shedule.setOperations(dataSet);
        Chromosome chromosome = new Chromosome(4);
        double[] genes = chromosome.getGenes();
        genes[0] = 0.22;
        genes[1] = 0.2;
        genes[2] = 0.25;
        genes[3] = 0.9;
        genes[4] = 0.84;
        genes[5] = 1.44;
        genes[6] = 1.5;
        genes[7] = 4.2;

        Shedule s = new Shedule(chromosome);
        s.test();
        System.out.println(s.time());

// Shedule s = new Shedule(new Chromosome(dataSet.length*dataSet[0].length).initializedChromosome());


        return 0;
    }

}
