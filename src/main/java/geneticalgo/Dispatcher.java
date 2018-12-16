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
            JSONObject test;
            for (int i = 0; i < tests.length(); i++) {
                test = (JSONObject) tests.get(i);
                JSONArray matrix = (JSONArray) test.get("data");
                int[][] dataSet = new int[matrix.length()][];
                for (int j = 0; j < matrix.length(); j++) {
                    JSONArray array = (JSONArray) matrix.get(j);
                    dataSet[j] = new int[array.length() / 2];
                    for (int k = 0; k < array.length(); k = k + 2) {
                        dataSet[j][array.getInt(k)] = array.getInt(k + 1);
                    }

                }
                System.out.println("test name " + test.get("description") + " time = " + runTest(dataSet));
            }

        } //      Считуєм джейсон
        catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static int runTest(int[][] dataSet) {
        /**
         * in: dataSet  [job][machine] = int
         *
         * return: [machine][queue operations for current machine] = [job, time]
         *
         */
        N = dataSet.length * dataSet[0].length;
        Population populatio = new Population(2 * N).createGeneration(2 * N);

        for (int i = 0; i < 400; i++) {
//            System.out.println("i = " + i + " time " + populatio.chromosomes[0].getTime());
            populatio = populatio.newGeneration();
            // evaluate
            for (int chIndex = 0; chIndex < populatio.chromosomes.length; chIndex++) {
//                System.out.println("chIndex = " + chIndex);
                Shedule.setOperations(dataSet);
                Shedule s = new Shedule(populatio.chromosomes[chIndex]);
                s.test();
                populatio.chromosomes[chIndex].setTime(s.time(Shedule.getOperations()));

            }

            // sort
            populatio.sort();
        }
        populatio.sort();

        return populatio.chromosomes[0].getTime();
    }

}
